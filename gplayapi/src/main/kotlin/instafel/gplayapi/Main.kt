package instafel.gplayapi

import instafel.gplayapi.utils.ExceptionHandler
import me.mamiiblt.instafel.gplayapi.utils.Log

@Throws(Exception::class)
fun main() {
    Log.println("I", "Instafel GPlayAPI (with Kotlin)")

    Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler());

    // load email & aas token from gplayapi.properties
    Env.updateEnvironment()
    // set device config (you can get device props from https://gitlab.com/AuroraOSS/gplayapi , And don't forget to import prop file into resources)
    Env.updateDeviceProp("gplayapi_px_3a.properties")
    // this method starts checker, you can customize it whatever you want :)
    Env.startChecker()
}