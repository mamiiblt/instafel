package instafel.patcher.core.utils

object OSDetector {
    val userOS: String = System.getProperty("os.name").lowercase()

    fun isWin(): Boolean = userOS.contains("win")
    fun isMac(): Boolean = userOS.contains("mac")
    fun isUnix(): Boolean = userOS.contains("nix") ||
            userOS.contains("nux") ||
            userOS.contains("aix") ||
            userOS.contains("sunos")
}