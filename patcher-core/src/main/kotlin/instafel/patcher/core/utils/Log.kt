/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.patcher.core.utils

import java.util.logging.*

object Log {
    // This logger will be connected to CLI's logger directly
    val LOGGER: Logger? = Logger.getLogger(Log::class.java.name)

    fun info(msg: String) = when (msg) {
        "" -> println("");
        Env.SEPARATOR_LINE -> println(Env.SEPARATOR_LINE)
        else -> LOGGER!!.info(msg)
    }

    fun warning(msg: String) = LOGGER!!.warning(msg)
    fun severe(msg: String) = LOGGER!!.severe(msg)
}