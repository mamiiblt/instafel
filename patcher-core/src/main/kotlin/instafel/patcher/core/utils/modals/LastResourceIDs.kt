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

package instafel.patcher.core.utils.modals

import org.json.JSONObject

class LastResourceIDs {
    private val data = JSONObject()

    init {
        data.put("attr", 0)
        data.put("color", 0)
        data.put("drawable", 0)
        data.put("id", 0)
        data.put("layout", 0)
        data.put("string", 0)
        data.put("style", 0)
        data.put("xml", 0)
    }

    operator fun get(type: String): Int = data.getInt(type)
    operator fun set(type: String, value: Int) = data.put(type, value)
}