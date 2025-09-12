import org.gradle.api.Project
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import org.json.JSONObject

fun Project.registerGithubReleaseTask(
    owner: String,
    repo: String,
    token: String?,
    tagName: String,
    name: String,
    body: String,
    assets: List<File> = emptyList()
) {
    println("${rootProject.projectDir}")
    println("Creating release named as \"$tagName\"")

    val releaseUrl = URL("https://api.github.com/repos/$owner/$repo/releases")
    val releaseConnection = (releaseUrl.openConnection() as HttpURLConnection).apply {
        requestMethod = "POST"
        setRequestProperty("Authorization", "Bearer $token")
        setRequestProperty("Accept", "application/vnd.github+json")
        setRequestProperty("Content-Type", "application/json")
        setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
        doOutput = true
    }

    val body = JSONObject()
        .put("tag_name", tagName)
        .put("target_commitish", "main")
        .put("name", name)
        .put("body", body)
        .put("draft", false)
        .put("prerelease", false)
        .toString()

    releaseConnection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

    if (releaseConnection.responseCode !in 200..299) {
        val errorBody = releaseConnection.errorStream?.bufferedReader()?.readText()
        error("An error occurred while creating release, ${releaseConnection.responseCode}: $errorBody")
    }

    val response = releaseConnection.inputStream.bufferedReader().readText()
    val releaseId = Regex("\"id\":\\s*(\\d+)").find(response)?.groupValues?.get(1)
        ?: error("Release ID couldn't get")

    println("Release created with ID $releaseId in repository $owner/$repo")

    println("Uploading artifacts as assets...")
    for (asset in assets) {
        println("Uploading asset: ${asset.name}")

        val uploadUrl = URL(
            "https://uploads.github.com/repos/$owner/$repo/releases/$releaseId/assets?name=${asset.name}"
        )
        val uploadConnection = (uploadUrl.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Authorization", "Bearer $token")
            setRequestProperty("Content-Type", "application/octet-stream")
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            doOutput = true
        }

        Files.newInputStream(Paths.get(asset.path)).use { input ->
            uploadConnection.outputStream.use { output -> input.copyTo(output) }
        }

        if (uploadConnection.responseCode !in 200..299) {
            val errorBody = uploadConnection.errorStream?.bufferedReader()?.readText()
            error("An error occurred while uploading asset, ${uploadConnection.responseCode}: $errorBody")
        }

        println("Asset successfully uploaded: ${asset.name}")
    }
    println("All assets uploaded successfully")
    println("Everything is completed successfully.")
}

fun writeBuildInfoJSON(buildDir: File, jsonObj: JSONObject): File {
    println("Generating build_info.json file...")
    val infoFile = File("$buildDir/generated-build-infos/build_info.json")
    infoFile.parentFile.mkdirs()
    infoFile.writeText(jsonObj.toString(2))
    println("File saved into ${infoFile.absolutePath} successfully.")
    return infoFile
}

fun Project.generatePatcherBuildJSON(
    version: String,
    commit: String,
    channel: String
): File = writeBuildInfoJSON(buildDir, JSONObject()
    .put("version", version)
    .put("channel", channel)
    .put("commit", commit))

fun Project.generateUpdaterBuildJSON(
    version: String,
    commit: String,
    channel: String,
    branch: String
): File = writeBuildInfoJSON(buildDir, JSONObject()
    .put("version", version)
    .put("channel", channel)
    .put("commit", commit)
    .put("branch", branch))

fun Project.generatePatcherCoreBuildJSON(
    commit: String,
    branch: String,
    supportedVer: String
): File = writeBuildInfoJSON(buildDir, JSONObject()
    .put("commit", commit)
    .put("branch", branch)
    .put("supported_patcher_v", supportedVer))