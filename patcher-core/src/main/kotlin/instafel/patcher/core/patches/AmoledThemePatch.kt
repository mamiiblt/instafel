/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

/* Thanks to piko patches */

package instafel.patcher.core.patches

import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

@PInfos.PatchInfo(
    name = "AMOLED Theme",
    shortname = "amoled_theme",
    desc = "Forces Instagram dark theme surfaces to pure black (AMOLED).",
    isSingle = true
)
class AmoledThemePatch : InstafelPatch() {

    override fun initializeTasks() = mutableListOf(

        @PInfos.TaskInfo("Patch night mode colors")
        object : InstafelTask() {
            override fun execute() {
                val files = scanForColors("values-night")

                if (files.isEmpty()) {
                    Log.info("values-night/colors.xml not found")
                    return
                }

                files.forEach { file ->
                    val doc = loadXML(file)
                    val colors = doc.getElementsByTagName("color")

                    setColor(colors, "igds_secondary_background", "@color/bds_black")
                    setColor(colors, "igds_elevated_background", "@color/bds_black")
                    setColor(colors, "igds_elevated_highlight_background", "@color/bds_black")

                    saveXML(doc, file)
                    Log.info("Night patched: ${file.absolutePath}")
                }

                success("Night AMOLED applied")
            }
        },

        @PInfos.TaskInfo("Patch dark root backgrounds")
        object : InstafelTask() {
            override fun execute() {
                val files = scanForStyles()

                if (files.isEmpty()) {
                    Log.info("res/values/styles.xml not found")
                    return
                }

                val targets = listOf(
                    Triple("EffectDarkMode", "backgroundDrawable", "@color/bds_black"),
                    Triple("EffectDarkMode", "igds_color_primary_background", "@color/bds_black"),
                    Triple("IgdsPrismSemanticColorsExperimentDark", "igds_color_primary_background", "@color/bds_black"),
                    Triple("IgdsPrismGrayOverridesDark", "igds_color_primary_background", "@color/bds_black")
                )

                var patched = 0
                files.forEach { file ->
                    val doc = loadXML(file)
                    var fileChanged = false

                    targets.forEach { target ->
                        if (setStyleItem(doc, target.first, target.second, target.third)) {
                            patched++
                            fileChanged = true
                        }
                    }

                    if (fileChanged) {
                        saveXML(doc, file)
                        Log.info("Dark root styles patched: ${file.absolutePath}")
                    }
                }

                if (patched > 0) {
                    success("Patched $patched dark root background entries")
                } else {
                    Log.info("Dark root background entries already patched or not found")
                }
            }
        },

        @PInfos.TaskInfo("Patch Prism Compose colors")
        object : InstafelTask() {
            override fun execute() {
                val projectDir = getProjectDir()
                val targets = listOf(
                    "BasePrismColors.smali" to "BLACK",
                    "BasePrismColorsV2.smali" to "GRAY_1600"
                )

                var patched = 0
                targets.forEach { (fileName, fieldName) ->
                    val file = projectDir.walkTopDown()
                        .firstOrNull { it.isFile && it.name == fileName }

                    if (file == null) {
                        Log.info("$fileName not found")
                    } else if (patchPrismField(file, fieldName)) {
                        patched++
                    } else {
                        Log.info("$fileName $fieldName not changed")
                    }
                }

                if (patched > 0) {
                    success("Prism AMOLED fields patched")
                } else {
                    Log.info("Prism AMOLED fields already patched or not found")
                }
            }
        }
    )

    private fun getProjectDir(): File {
        val field = smaliUtils.javaClass.getDeclaredField("projectDir")
        field.isAccessible = true
        val path = field.get(smaliUtils) as String
        return File(path)
    }

    private fun scanForColors(folder: String): List<File> {
        val result = mutableListOf<File>()
        val baseDir = getProjectDir()

        baseDir.walkTopDown().forEach { file ->
            val path = file.absolutePath.replace("\\", "/")

            if (file.isFile &&
                file.name == "colors.xml" &&
                path.contains("/res/") &&
                path.contains("/$folder")
            ) {
                result.add(file)
            }
        }

        Log.info("Found ${result.size} colors.xml in $folder")
        return result
    }

    private fun scanForStyles(): List<File> {
        val result = mutableListOf<File>()
        val baseDir = getProjectDir()

        baseDir.walkTopDown().forEach { file ->
            val path = file.absolutePath.replace("\\", "/")

            if (file.isFile &&
                file.name == "styles.xml" &&
                path.contains("/res/values")
            ) {
                result.add(file)
            }
        }

        Log.info("Found ${result.size} styles.xml files")
        return result
    }

    private fun loadXML(file: File) =
        DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(file)

    private fun saveXML(doc: org.w3c.dom.Document, file: File) {
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.transform(DOMSource(doc), StreamResult(file))
    }

    private fun setColor(nodes: org.w3c.dom.NodeList, name: String, value: String) {
        for (i in 0 until nodes.length) {
            val el = nodes.item(i) as org.w3c.dom.Element
            if (el.getAttribute("name") == name) {
                val old = el.textContent.trim()
                el.textContent = value
                Log.info("[$name] $old -> $value")
                return
            }
        }
        Log.info("$name not found")
    }

    private fun setStyleItem(
        doc: org.w3c.dom.Document,
        styleName: String,
        itemName: String,
        value: String
    ): Boolean {
        val styles = doc.getElementsByTagName("style")

        for (i in 0 until styles.length) {
            val style = styles.item(i) as org.w3c.dom.Element
            if (style.getAttribute("name") != styleName) continue

            val items = style.getElementsByTagName("item")
            for (j in 0 until items.length) {
                val item = items.item(j) as org.w3c.dom.Element
                if (item.getAttribute("name") != itemName) continue

                val old = item.textContent.trim()
                if (old == value) {
                    Log.info("[$styleName.$itemName] already $value")
                    return false
                }

                item.textContent = value
                Log.info("[$styleName.$itemName] $old -> $value")
                return true
            }

            Log.info("$styleName.$itemName not found")
            return false
        }

        Log.info("$styleName style not found")
        return false
    }

    private fun patchPrismField(file: File, fieldName: String): Boolean {
        val lines = file.readLines().toMutableList()
        val fieldSuffix = "->$fieldName:J"
        var storeFound = false

        for (i in lines.indices) {
            val line = lines[i].trim()
            if (!line.startsWith("sput-wide") || !line.contains(fieldSuffix)) continue
            storeFound = true

            for (j in i - 1 downTo maxOf(0, i - 20)) {
                val previous = lines[j].trim()

                if (previous.startsWith("const-wide")) {
                    val register = previous.substringAfter(' ').substringBefore(',').trim()
                    if (register.isEmpty()) break

                    val oldValue = previous.substringAfter(',', "").trim()
                    if (oldValue == "0xff000000L") {
                        Log.info("$fieldName is already pure black")
                        return false
                    }

                    val indent = lines[j].takeWhile { it == ' ' || it == '\t' }
                    lines[j] = "${indent}const-wide $register, 0xff000000L"
                    file.writeText(lines.joinToString("\n", postfix = "\n"))
                    Log.info("Patched $fieldName: $oldValue -> 0xff000000L")
                    return true
                }

                if (previous.startsWith("sput-wide") || previous.startsWith(".method")) {
                    break
                }
            }
        }

        if (!storeFound) {
            Log.info("$fieldName store not found in ${file.name}")
        }
        return false
    }
}
