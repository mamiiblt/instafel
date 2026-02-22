package instafel.patcher.core.utils

import instafel.patcher.core.utils.modals.LineData
import instafel.patcher.core.utils.modals.MethodContent
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class SmaliUtils(private val projectDir: String) {
    val smaliFolders = getSmaliFolderArray()

    fun extractAllMethodsAsLineArrays(fContent: List<String>): List<List<String>> {
        val methods = ArrayList<List<String>>()

        var i = 0
        while (i < fContent.size) {
            val line = fContent[i]
            val trimmed = line.trimStart()

            if (trimmed.startsWith(".method ")) {
                val methodLines = ArrayList<String>()
                methodLines.add(line)
                i++

                var foundEnd = false
                while (i < fContent.size) {
                    val l = fContent[i]
                    methodLines.add(l)

                    if (l.trim() == ".end method") {
                        foundEnd = true
                        i++
                        break
                    }
                    i++
                }
                methods.add(methodLines)
                continue
            }
            i++
        }

        return methods
    }

    fun removeMethodContent(
        fContent: List<String>,
        methodName: String,
        methodParams: String
    ): List<String> {
        val out = ArrayList<String>(fContent.size)

        var i = 0
        while (i < fContent.size) {
            val line = fContent[i]
            val trimmed = line.trimStart()

            val isTargetMethodHeader = trimmed.startsWith(".method ") && trimmed.contains("$methodName$methodParams")

            if (isTargetMethodHeader) {
                i++
                while (i < fContent.size) {
                    if (fContent[i].trim() == ".end method") {
                        i++
                        break
                    }
                    i++
                }
                continue
            }

            out.add(line)
            i++
        }

        return out
    }

    fun getMethodContent(fContent: List<String>, methodStart: Int): MethodContent {
        if (methodStart !in fContent.indices) return MethodContent(linkedMapOf(), "")

        val lineMap = linkedMapOf<Int, String>()
        val textBuilder = StringBuilder()

        for (i in methodStart until fContent.size) {
            val line = fContent[i]
            lineMap[i] = line
            textBuilder.appendLine(line)

            if (line.trim() == ".end method") break
        }

        return MethodContent(lineMap, textBuilder.toString())
    }

    fun getUnusedRegistersOfMethod(fContent: List<String>, methodStart: Int, lineEnd: Int): Int {
        val pattern = Regex("""\bv(\d+)\b""")
        val registers = mutableListOf<Int>()

        for (i in methodStart..lineEnd) {
            val line = fContent[i]
            pattern.findAll(line).forEach { matchResult ->
                registers.add(matchResult.groupValues[1].toInt())
            }
        }

        val sortedRegisters = registers.toSet().sorted()

        for (i in 0..(sortedRegisters.maxOrNull() ?: -1) + 1) {
            if (i !in sortedRegisters) return i
        }

        return (sortedRegisters.lastOrNull() ?: -1) + 1
    }

    fun getContainLines(fContent: List<String>, vararg searchParams: String): List<LineData> {
        val lineDataList = mutableListOf<LineData>()
        for (i in fContent.indices) {
            if (containsAllKeys(fContent[i], *searchParams)) {
                lineDataList.add(LineData(i, fContent[i]))
            }
        }
        return lineDataList
    }

    fun getSmaliFilesByName(fileNamePart: String): List<File> {
        return smaliFolders.flatMap { smaliFolder ->
            val folder = File(smaliFolder.absolutePath)
            val files: Collection<File> = FileUtils.listFiles(
                folder,
                TrueFileFilter.INSTANCE,
                TrueFileFilter.INSTANCE
            )
            files.filter { it.absolutePath.contains(fileNamePart) }
        }
    }

    @Throws(IOException::class)
    fun writeContentIntoFile(filePath: String, fContent: List<String>) {
        BufferedWriter(FileWriter(filePath)).use { bw ->
            fContent.forEach { line ->
                bw.write(line)
                bw.newLine()
            }
        }
    }

    @Throws(IOException::class)
    fun getSmaliFileContent(filePath: String): List<String> = Files.readAllLines(Paths.get(filePath))

    fun getSmallSizeSmaliFolder(smaliFolders: Array<File>?): File? {
        if (smaliFolders.isNullOrEmpty()) return null

        return smaliFolders
            .filter { it.isDirectory }
            .minByOrNull { getFolderSize(it) }
    }

    fun getSmaliFolderByPaths(vararg folders: String): File? {
        if (smaliFolders.isEmpty()) return null

        for (smaliFolder in smaliFolders) {
            val extPath = File(Utils.mergePaths(smaliFolder.absolutePath, *folders))
            if (extPath.exists()) return smaliFolder
        }

        return null
    }

    fun getFolderSize(folder: File): Long {
        if (!folder.exists() || folder.isFile) return 0

        return folder.listFiles()
            ?.sumOf { if (it.isFile) it.length() else getFolderSize(it) }
            ?: 0
    }

    fun getSmaliFolderArray(): Array<File> {
        val decompiledClassesFolder = File(Utils.mergePaths(projectDir, "sources"))

        if (!decompiledClassesFolder.exists() || !decompiledClassesFolder.isDirectory) {
            return emptyArray()
        }

        return decompiledClassesFolder
            .listFiles(File::isDirectory)
            ?.filter { it.name.lowercase().startsWith("smali") }
            ?.sortedWith { f1, f2 ->
                when {
                    f1.name == "smali" -> -1
                    f2.name == "smali" -> 1
                    else -> extractCDexNumber(f1.name).compareTo(extractCDexNumber(f2.name))
                }
            }
            ?.toTypedArray()
            ?: emptyArray()
    }

    fun extractCDexNumber(folderName: String): Int {
        return if (folderName.startsWith("smali_classes")) {
            folderName.substring("smali_classes".length).toIntOrNull() ?: Int.MAX_VALUE
        } else {
            Int.MAX_VALUE
        }
    }

    private fun containsAllKeys(input: String, vararg keys: String): Boolean {
        for (key in keys) {
            if (!input.contains(key)) {
                return false
            }
        }
        return true
    }
}