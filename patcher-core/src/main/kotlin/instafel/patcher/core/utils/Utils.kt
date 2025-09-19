package instafel.patcher.core.utils

import org.apache.commons.io.FileUtils
import java.io.*
import java.nio.file.*
import java.util.zip.*
import java.security.MessageDigest

object Utils {
    fun getSuggestedThreadCount(): Int {
        val threadCount = Runtime.getRuntime().availableProcessors()
        return threadCount.coerceAtMost(10) // use max 8 threads
    }

    fun makeSmaliPathShort(file: File): String = "'smali${file.absolutePath.substringAfter("/smali")}'"

    fun mergePaths(basePath: String, vararg args: String) = Paths.get(basePath, *args).toString()

    @Throws(IOException::class, FileNotFoundException::class)
    fun copyResourceToFile(resourcePath: String, destFile: File) {
        Utils::class.java.getResourceAsStream(resourcePath).use { inputStream ->
            FileOutputStream(destFile).use { outputStream ->
                if (inputStream == null) {
                    throw FileNotFoundException("Resource not found: $resourcePath")
                }
                val buffer = ByteArray(1024)
                var length: Int
                while ((inputStream.read(buffer).also { length = it }) > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }
    }

    fun deleteDirectory(path: String) {
        val file = File(path)
        if (file.exists() && file.isDirectory) {
            FileUtils.deleteDirectory(file)
        }
    }

    fun unzipFromResources(showLog: Boolean, zipFilePath: String, destDirectory: String) {
        val destDir = File(destDirectory)
        if (!destDir.exists()) destDir.mkdirs()

        Utils::class.java.getResourceAsStream(zipFilePath)?.use { inputStream ->
            ZipInputStream(inputStream).use { zipIn ->
                var entry: ZipEntry?
                while (zipIn.nextEntry.also { entry = it } != null) {
                    val file = File(destDirectory, entry!!.name)
                    if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        file.parentFile.mkdirs()
                        BufferedOutputStream(FileOutputStream(file)).use { bos ->
                            val buffer = ByteArray(4096)
                            var bytesRead: Int
                            while (zipIn.read(buffer).also { bytesRead = it } != -1) {
                                bos.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                    if (showLog) {
                        Log.info("Copying ${file.name}")
                    }
                    zipIn.closeEntry()
                }
            }
        }
    }

    fun getFileMD5(file: File): String? {
        return try {
            FileInputStream(file).use { fis ->
                val md = MessageDigest.getInstance("MD5")
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    md.update(buffer, 0, bytesRead)
                }
                md.digest().joinToString("") { "%02x".format(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.info("Error while creating MD5 hash for ${file.name}")
            null
        }
    }

    fun zipDirectory(sourceDir: Path, zipFilePath: Path) {
        ZipOutputStream(FileOutputStream(zipFilePath.toFile())).use { zipOut ->
            Files.walk(sourceDir).forEach { path ->
                val zipEntryName = sourceDir.relativize(path).toString().replace("\\", "/")
                try {
                    if (Files.isDirectory(path)) {
                        if (zipEntryName.isNotEmpty()) {
                            zipOut.putNextEntry(ZipEntry("$zipEntryName/"))
                            zipOut.closeEntry()
                        }
                    } else {
                        zipOut.putNextEntry(ZipEntry(zipEntryName))
                        Files.copy(path, zipOut)
                        zipOut.closeEntry()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}