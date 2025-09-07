package instafel.patcher.core.utils.resources

import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.modals.LastResourceIDs
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.StandardCharsets

object ResourceHelper {
    fun convertToHex(value: Int): String {
        return "0x${Integer.toHexString(value)}"
    }

    fun parseHexString(value: String): Int {
        return Integer.parseInt(value.substring(2), 16)
    }

    fun updateRClass(iflPublic: Resources<RTypes.TPublic>, rFile: File) {
        val lines = FileUtils.readLines(rFile, StandardCharsets.UTF_8)
        val ids = mutableMapOf<String, String>()
        iflPublic.forEach { tPublic -> ids[tPublic.name] = tPublic.id }

        for (i in lines.indices) {
            val line = lines[i]
            if ("ifl_" in line) {
                val lName = line.substringAfter("static ").substringBefore(":I = ")
                ids[lName]?.let { value ->
                    lines[i] = ".field public static $lName:I = $value"
                }
            }
        }

        FileUtils.writeLines(rFile, lines)
        Log.info("Class ${rFile.name} succesfully updated.")
    }

    fun getIDsWithCategory(publicValues: Resources<RTypes.TPublic>): MutableMap<String, MutableList<Int>> {
        val categorizedIGPublicIDs = mutableMapOf<String, MutableList<Int>>()

        publicValues.forEach { item ->
            categorizedIGPublicIDs.getOrPut(item.type) { mutableListOf() }
                .add(item.id.substring(2).toInt(16))
        }

        return categorizedIGPublicIDs
    }

    fun getBiggestResourceID(categorizedIDs: Map<String, List<Int>>): LastResourceIDs {
        val lastResourceIds = LastResourceIDs()

        categorizedIDs.forEach { (key, ids) ->
            when (key) {
                "attr", "color", "string", "id", "layout", "drawable", "xml", "style" -> {
                    ids.forEach { id ->
                        if (id > lastResourceIds.get(key)) {
                            lastResourceIds.set(key, id)
                        }
                    }
                }
            }
        }

        return lastResourceIds
    }
}