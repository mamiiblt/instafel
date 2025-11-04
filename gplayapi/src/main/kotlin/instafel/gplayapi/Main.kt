package instafel.gplayapi

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import instafel.gplayapi.utils.EnvConfig
import instafel.gplayapi.utils.Log

lateinit var params: Array<String>
val gson: Gson = GsonBuilder().setPrettyPrinting().create()

fun main(args: Array<String>) {
    params = args;
    Log.println("I", "Starting a new GPlayAPI instance...")

    val envConfig = EnvConfig(
        loadParamFromArgs("email"),
        loadParamFromArgs("aasToken"),
    )
    Env.updateEnvironment(envConfig)
    Env.updateDeviceProp()
    Log.println("RESP", gson.toJson(Env.getLatestRelease()))
}

fun loadParamFromArgs(paramName: String): String {
    params.forEach { param ->
        if (param.startsWith("$paramName=")) {
            return param.replace("$paramName=", "")
        }
    }
    return ""
}