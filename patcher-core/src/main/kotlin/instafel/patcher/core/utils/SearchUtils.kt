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
        searchConditions: List<String>
    ): FileSearchResult {
        var scannedFileSize = 0
        val foundFiles = mutableListOf<File>()

        getXIterators(smaliUtils.smaliFolders).forEach { xFolder ->
            xFolder.forEach { file ->
                scannedFileSize++
                val fContent = smaliUtils.getSmaliFileContent(file.absolutePath)
                val passStatuses = BooleanArray(searchConditions.size)
                for (line in fContent) {
                    searchConditions.forEachIndexed { i, str ->
                        if (line.contains(str)) passStatuses[i] = true
                    }
                }
                val passStatus = passStatuses.all { it }
                if (passStatus) {
                    Log.info("Found file, $file")
                    foundFiles.add(file)
                }
            }
        }

        Log.info("Totally scanned $scannedFileSize file(s) in X folders")


        if (foundFiles.isEmpty() || foundFiles.size > 1) {
            Log.severe("Found more files than one (or no any file found) for apply patch, add more condition for find correct file.")
            return FileSearchResult.NotFound(scannedFileSize)
        } else {
            Log.info("Class ${foundFiles[0].name} meets all requirements")
            return FileSearchResult.Success(foundFiles[0])
        }
    }
}