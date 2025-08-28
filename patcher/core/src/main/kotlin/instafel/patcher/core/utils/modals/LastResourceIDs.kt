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