/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

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