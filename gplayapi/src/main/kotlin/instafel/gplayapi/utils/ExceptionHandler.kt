package instafel.gplayapi.utils

import kotlin.system.exitProcess

class ExceptionHandler : Thread.UncaughtExceptionHandler{
    override fun uncaughtException(t: Thread?, e: Throwable?) {
        if (e != null) {
            e.printStackTrace();
            Log.println("E", e.message ?: "Unknown error")
        } else {
            println("An error occurred while handling gplay API exception")
        }

        exitProcess(-1)
    }
}