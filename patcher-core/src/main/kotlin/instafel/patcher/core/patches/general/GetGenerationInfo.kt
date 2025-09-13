package instafel.patcher.core.patches.general

import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@PInfos.PatchInfo(
    name = "Get Generation Info",
    shortname = "get_generation_info",
    desc = "Grab IFL Version and Generation ID from API",
    author = "mamiiblt",
    isSingle = false
)
class GetGenerationInfo: InstafelPatch() {

    var apiBase: String = Env.Project.getString(Env.Project.Keys.API_BASE, "api.mamii.me/ifl")
    var httpClient = OkHttpClient()
    var isProdMode = Env.Config.getBoolean(Env.Config.Keys.prod_mode, false)

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Get last IFL version from API")
        object: InstafelTask() {
            override fun execute() {
                if (isProdMode) {
                    val iflVersionRequest = Request.Builder()
                        .url("https://$apiBase/manager_new/lastInstafelData")
                        .addHeader("Authorization", Env.Config.getString(Env.Config.Keys.manager_token, "null"))
                        .build()

                    try {
                        httpClient.newCall(iflVersionRequest).execute().use { response ->

                            if (!response.isSuccessful) {
                                failure("Request failed for iflVersionRequest, code: ${response.code}")
                                return@use
                            }

                            val iflVRequestParsed = JSONObject(response.body.string())
                            val iflVersion = iflVRequestParsed.getInt("ifl_version")

                            Env.Project.setInteger(Env.Project.Keys.INSTAFEL_VERSION, iflVersion + 1)
                            Log.info("Instafel version for this generation is ${iflVersion + 1}")
                            success("IFL version successfully saved to env")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        failure("Exception while requesting IFL version: ${e.message}")
                    }
                } else {
                    success("You are using non-prod mode patcher, skipping this patch.")
                }
            }
        },
        @PInfos.TaskInfo("Generate a new Generation ID from API")
        object: InstafelTask() {
            override fun execute() {
                if (isProdMode) {
                    val genIDRequest = Request.Builder()
                        .url("https://$apiBase/manager_new/createGenerationId")
                        .addHeader("Authorization", Env.Config.getString(Env.Config.Keys.manager_token, "null"))
                        .build()

                    try {
                        httpClient.newCall(genIDRequest).execute().use { response ->

                            if (!response.isSuccessful) {
                                failure("Request failed for genIDRequest, code: ${response.code}")
                                return@use
                            }

                            val genIdResParsed = JSONObject( response.body.string())
                            val genId = genIdResParsed.getString("generation_id")

                            Env.Project.setString(Env.Project.Keys.GENID, genId)
                            Log.info("Generation ID for this generation is $genId")
                            success("Generation ID successfully saved to env")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        failure("Exception while requesting Generation ID: ${e.message}")
                    }
                } else {
                    success("You are using non-prod mode generator, skipping this patch.")
                }
            }
        }
    )
}