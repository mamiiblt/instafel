import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.HttpURLConnection

data class PCoreReleaseInfo(
    val releaseViewLink: String,
    val releaseId: Int,
    val baseCommitHash: String
)

class PatcherCoreUpdaterUtils(
    val managerToken: String,
    val ghPatToken: String,
) {

    fun getLatestPCoreRelease(): PCoreReleaseInfo {
        val url = URL("https://api.github.com/repos/instafel/pc-rel/releases/latest")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Authorization", "Bearer $ghPatToken")
        conn.setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
        conn.setRequestProperty("Accept", "application/vnd.github+json")

        val responseText = conn.inputStream.bufferedReader().readText()
        val releaseData = JSONObject(responseText)
        val releaseViewLink = releaseData.getString("html_url")
        val releaseId = releaseData.getInt("id")
        val body = releaseData.getString("body").split("\n")
        lateinit var baseCommitHash: String
        body.forEach { line -> if (line.contains("| Commit  |")) baseCommitHash = Regex("""\[(\b[a-f0-9]{7,40}\b)]\(""").find(line)?.groupValues?.get(1)?.trim()!! }
        return PCoreReleaseInfo(
            releaseViewLink, releaseId, baseCommitHash
        )
    }

    fun sendActionCompletedReq() {
        val uData = getLatestPCoreRelease()
        val body = JSONObject()
        body.put("release_id", uData.releaseId)
        body.put("commit_hash", uData.baseCommitHash)
        body.put("view_url", uData.releaseViewLink)

        val url = URL("https://api.instafel.app/manager/update-op-completed")
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("manager-token", managerToken)

        val outputBytes = body.toString().toByteArray(Charsets.UTF_8)
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
}