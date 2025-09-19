package instafel.patcher.core.utils

import org.apache.commons.io.FileUtils
import java.io.File

sealed class FileSearchResult {
    data class Success(val file: File) : FileSearchResult()
    data class NotFound(val scannedFiles: Int) : FileSearchResult()
}

object SearchUtils {

    private fun getXIterators(smaliFolders: Array<File>): List<Iterator<File>> {
        return smaliFolders.map { smaliFolder ->
            val xFolder = File(smaliFolder, "X")
            FileUtils.iterateFiles(xFolder, null, true)
        }
    }

    fun getFileContainsAllCords(
        smaliUtils: SmaliUtils,
        searchConditions: List<List<String>>
    ): FileSearchResult {
        var scannedFileSize = 0
        val foundFiles = mutableListOf<File>()

        getXIterators(smaliUtils.smaliFolders).forEach { xFolder ->
            xFolder.forEach { file ->
                scannedFileSize++
                val fContent = smaliUtils.getSmaliFileContent(file.absolutePath)
                val passStatuses = BooleanArray(searchConditions.size)
                for (line in fContent) {
                    searchConditions.forEachIndexed { i, lineConditions ->
                        if (lineConditions.all { condition -> line.contains(condition) }) {
                            passStatuses[i] = true
                        }
                    }
                }
                val passStatus = passStatuses.all { it }
                if (passStatus) {
                    Log.info("A file found, ${Utils.makeSmaliPathShort(file)}")
                    foundFiles.add(file)
                }
            }
        }

        Log.info("Totally scanned $scannedFileSize file(s) in X folders")


        if (foundFiles.isEmpty() || foundFiles.size > 1) {
            Log.severe("Found more files than one (or no any file found) for apply patch, add more condition for find correct file.")
            return FileSearchResult.NotFound(scannedFileSize)
        } else {
            Log.info("Class ${Utils.makeSmaliPathShort(foundFiles[0])} meets all requirements")
            return FileSearchResult.Success(foundFiles[0])
        }
    }
}