package instafel.gplayapi.utils

import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.helpers.AuthHelper
import java.nio.file.Paths
import java.util.Properties
import kotlin.system.exitProcess

class General {
    companion object {
        fun authenticateUser (email: String, aasKey: String, deviceProp: Properties): AuthData? {
            try {
                return AuthHelper.build(email, aasKey, deviceProp)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.println("E", "An error occured while authenticating user with GPlay servers.")
                exitProcess(-1)
            }
        }

        fun mergePaths(basePath: String, vararg args: String): String {
            return Paths.get(basePath, *args).toString()
        }

        fun isNumeric(part: String): Boolean {
            return part.any { it.isDigit() }
        }
    }
}
