import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.net.HttpURLConnection
import java.io.OutputStreamWriter
import java.util.zip.ZipInputStream

fun File.deleteRecursivelySafe(): Boolean {
    if (!exists()) return true
    return try {
        deleteRecursively()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

class CrowdinMergeUtils(
    val crowdinApiToken: String,
    val outputDir: File
) {
    val crowdinProjectID = 838578
    val pollInterval = 1000L
    lateinit var buildId: String
    lateinit var exportFile: File

    fun startBuild() {
        println("Sending build request...")
        outputDir.deleteRecursivelySafe()

        val url = URL("https://api.crowdin.com/api/v2/projects/$crowdinProjectID/translations/builds")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Authorization", "Bearer $crowdinApiToken")
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true

        val body = JSONObject().apply {
            put("exportApprovedOnly", true)
        }.toString()

        OutputStreamWriter(conn.outputStream).use { it.write(body) }

        val responseText = conn.inputStream.bufferedReader().readText()
        val json = JSONObject(responseText)
        val buildId = json.getJSONObject("data").getInt("id")
        println("Build started with ID: $buildId")
        this.buildId = buildId.toString()
    }

    fun waitUntilBuildFinish() {
        println("Waiting for build $buildId to finish...")

        while (true) {
            val url = URL("https://api.crowdin.com/api/v2/projects/$crowdinProjectID/translations/builds/$buildId")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $crowdinApiToken")

            val responseText = conn.inputStream.bufferedReader().readText()
            val status = JSONObject(responseText)
                .getJSONObject("data")
                .getString("status")

            when (status) {
                "finished" -> {
                    println("Build finished!")
                    return
                }
                "failed" -> error("Crowdin build failed!")
                else -> {
                    println("Current status: $status, waiting...")
                    Thread.sleep(pollInterval)
                }
            }
        }
    }

    fun downloadBuild(downloadBuildId: String = this.buildId) {
        val url = URL("https://api.crowdin.com/api/v2/projects/$crowdinProjectID/translations/builds/$downloadBuildId/download")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Authorization", "Bearer $crowdinApiToken")

        val response = conn.inputStream.bufferedReader().readText()
        val downloadUrl = JSONObject(response).getJSONObject("data").getString("url")

        val zipUrl = URL(downloadUrl)
        val zipConn = zipUrl.openConnection() as HttpURLConnection
        zipConn.requestMethod = "GET"

        exportFile = File(outputDir, "crowdin_export.zip")
        exportFile.parentFile.mkdirs()
        BufferedOutputStream(FileOutputStream(exportFile)).use { output ->
            zipConn.inputStream.use { input ->
                input.copyTo(output)
            }
        }

        println("Downloaded Build ZIP → ${exportFile.absolutePath}")
    }

    fun unzipBuildFile() {
        val extractDir = File(outputDir, "sources")
        ZipInputStream(FileInputStream(exportFile)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val outFile = File(extractDir, entry.name)
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile.mkdirs()
                    FileOutputStream(outFile).use { output ->
                        zip.copyTo(output)
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }

        println("Extracted ZIP to → ${outputDir.absolutePath}")
    }
}