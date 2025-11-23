import org.gradle.internal.impldep.kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.net.HttpURLConnection
import java.io.OutputStreamWriter
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
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

fun copyFolder(sourceDir: Path, targetDir: Path) {
    Files.walkFileTree(sourceDir, object : SimpleFileVisitor<Path>() {
        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
            val targetPath = targetDir.resolve(sourceDir.relativize(dir))
            if (!Files.exists(targetPath)) {
                Files.createDirectory(targetPath)
            }
            return FileVisitResult.CONTINUE
        }

        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            Files.copy(file, targetDir.resolve(sourceDir.relativize(file)), StandardCopyOption.REPLACE_EXISTING)
            return FileVisitResult.CONTINUE
        }
    })
}

class CrowdinMergeUtils(
    val crowdinApiToken: String,
    val managerToken: String,
    val outputDir: File,
    val rootDir: File
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
        conn.setRequestProperty("Accept", "application/json")
        conn.doOutput = true

        val body = JSONObject().apply {
            put("exportApprovedOnly", false)
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
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")

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
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Accept", "application/json")

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

    fun updateLocalesInPatcher() {
        println("Updating locales in :patcher-core sources")
        val projectDir = File(rootDir, "patcher-core")
        val crowdinLocalizationFolderDir = Paths.get(outputDir.absolutePath, "sources", "main", "app").toFile()
        val localeAssetFile = Paths.get(projectDir.absolutePath, "src", "main", "resources", "supported_locales.json").toFile()
        val locales = loadCrowdinAndroidLocales(crowdinLocalizationFolderDir)
        val normalizedLocaleNames = mutableListOf<String>()
        locales.forEach { locale -> normalizedLocaleNames.add(locale.replace("-r", "-")) }
        val localesJsonArr = JSONArray(normalizedLocaleNames.sorted())
        localeAssetFile.writeText(localesJsonArr.toString())
        println("Patcher's SUPPORTED_LANGUAGES definition successfully updated!")
    }

    fun mergeAppSources() {
        println("Merging :app project sources")
        val projectDir = File(rootDir, "app")
        val crowdinLocalizationFolderDir = Paths.get(outputDir.absolutePath, "sources", "main", "app").toFile()
        val appLocalizationFolderDir = Paths.get(projectDir.absolutePath, "src", "main", "res").toFile()
        val instafelEnvFile = Paths.get(projectDir.absolutePath, "src", "main", "java", "instafel", "app", "InstafelEnv.java").toFile()

        val languages = loadCrowdinAndroidLocales(crowdinLocalizationFolderDir)
        val newLanguagesArr = arrayOf("en-US") + languages.sorted()
        newLanguagesArr.forEachIndexed { index, langCode -> newLanguagesArr[index] = langCode.replace("-r", "-") }

        languages.forEach { lang -> copyStringsFile(lang, crowdinLocalizationFolderDir, appLocalizationFolderDir) }
        println("All localization sources (${languages.size} file) copied into res/values-... folders")

        val content = instafelEnvFile.readText()
        val regex = Regex("""String\[\]\s+supportedLanguages\s*=\s*\{.*?};""", RegexOption.DOT_MATCHES_ALL)
        val newArrayContent = newLanguagesArr.joinToString(", ") { "\"$it\"" }
        val newLine = """String[] supportedLanguages = { $newArrayContent };"""
        val newContent = content.replace(regex, newLine)
        instafelEnvFile.writeText(newContent)
        println("App's SUPPORTED_LANGUAGES definition successfully updated!")
    }

    fun mergeUpdaterSources() {
        println("Merging :updater project sources")
        val projectDir = File(rootDir, "updater")
        val crowdinLocalizationFolderDir = Paths.get(outputDir.absolutePath, "sources", "main", "updater").toFile()
        val updaterLocalizationFolderDir = Paths.get(projectDir.absolutePath, "src", "main", "res").toFile()
        val mainStringFile = Paths.get(projectDir.absolutePath, "src", "main", "res", "values", "arrays.xml").toFile()

        val languages = loadCrowdinAndroidLocales(crowdinLocalizationFolderDir)
        val newLanguagesArr = arrayOf("en-US") + languages.sorted()

        languages.forEach { lang -> copyStringsFile(lang, crowdinLocalizationFolderDir, updaterLocalizationFolderDir) }
        println("All localization sources (${languages.size} file) copied into res/values-... folders")

        val content = mainStringFile.readText()
        val items = newLanguagesArr.joinToString(separator = "\n") { "        <item>${it.replace("-r", "-")}</item>" }
        val regex = Regex("""<string-array\s+name="supported_languages".*?>.*?</string-array>""", RegexOption.DOT_MATCHES_ALL)
        val newContent = content.replace(regex) { "<string-array name=\"supported_languages\">\n$items\n    </string-array>" }
        mainStringFile.writeText(newContent)
        println("Totally ${languages.size} translation file updated.")
    }

    fun mergeWebsiteSources() {
        println("Merging :website project sources...")
        val projectDir = File(rootDir, "website")
        val crowdinLocalizationFolderDir = Paths.get(outputDir.absolutePath, "sources", "main", "website").toFile()
        val websiteLocalizationFolderDir = Paths.get(projectDir.absolutePath, "src", "locales").toFile()
        val settingsTsFile = Paths.get(projectDir.absolutePath, "src", "i18n", "settings.ts").toFile()

        val crowdinLocalizationFolders = loadFolderContents(crowdinLocalizationFolderDir, "en-EN")
        val localWebsiteLocaleFolders = loadFolderContents(websiteLocalizationFolderDir, "en-EN")

        localWebsiteLocaleFolders.forEach { folderName ->
            val folder = File(websiteLocalizationFolderDir, folderName)
            if (folder.exists()) {
                folder.deleteRecursivelySafe()
            }
        }

        println("Totally ${localWebsiteLocaleFolders.size} localization folder deleted from website sources.")

        crowdinLocalizationFolders.forEach { folderName ->
            val destFolder = Paths.get(websiteLocalizationFolderDir.absolutePath, folderName)
            copyFolder(
                Paths.get(crowdinLocalizationFolderDir.absolutePath, folderName),
                destFolder
            )
        }

        println("Totally ${crowdinLocalizationFolders.size} localization folder copied into website sources.")

        val localeCodes = mutableListOf<String>()
        localeCodes.add("en-EN")
        crowdinLocalizationFolders.forEach { folderName -> localeCodes.add(folderName) }

        val sortedLocales = listOf("en-EN") + localeCodes.sorted()
            .filter { it != "en-EN" }
            .sortedBy { it.lowercase() }

        val content = settingsTsFile.readText()
        val regex = Regex("""supportedLocales\s*=\s*\[.*?]""", RegexOption.DOT_MATCHES_ALL)
        val newArray = sortedLocales.joinToString(
            prefix = "[\"",
            separator = "\",\"",
            postfix = "\"]"
        )
        val newContent = content.replace(regex, "supportedLocales = $newArray")
        settingsTsFile.writeText(newContent)

        println("Locale constraints updated with ${localeCodes.size} locale")
        println("Website sources updated with latest sources successfully.")
    }

    fun sendActionCompletedReq(commitHash: String) {
        val url = URL("https://api.instafel.app/manager/merge-op-completed")
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("manager-token", managerToken)

        val json = JSONObject()
        json.put("commitHash", commitHash)
        json.put("branch", "main")

        val outputBytes = json.toString().toByteArray(Charsets.UTF_8)
        connection.outputStream.use { os ->
            os.write(outputBytes)
        }

        val responseCode = connection.responseCode
        val response = if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
        } else {
            BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
        }

        println("Status: $responseCode")
        println("Response: $response")
    }

    fun loadFolderContents(baseFolder: File, filterFolderName: String): Array<String> {
        return baseFolder.listFiles()
            ?.filter { it.isDirectory && it.name != filterFolderName }
            ?.map { it.name }
            ?.toTypedArray() ?: emptyArray()
    }

    fun loadCrowdinAndroidLocales(baseFolder: File): Array<String> {
        return baseFolder.listFiles()
            ?.filter { it.isFile && it.name.startsWith("strings_") }
            ?.map { it.name.replace("strings_", "").replace(".xml", "") }
            ?.toTypedArray() ?: emptyArray()
    }

    fun copyStringsFile(lang: String, sourceDir: File, destBaseDir: File) {
        val source = sourceDir.toPath().resolve("strings_$lang.xml")
        val destDir = destBaseDir.toPath().resolve("values-$lang")
        val dest = destDir.resolve("strings.xml")

        Files.createDirectories(destDir)
        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING)
    }
}