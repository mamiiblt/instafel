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

        @PInfos.TaskInfo("Patch prism black")
        object : InstafelTask() {
            override fun execute() {

                val files = scanForColors("values")

                if (files.isEmpty()) {
                    Log.info("values/colors.xml not found")
                    return
                }

                files.forEach { file ->
                    val doc = loadXML(file)
                    val colors = doc.getElementsByTagName("color")

                    setColor(colors, "igds_prism_black", "#ff000000")

                    saveXML(doc, file)
                    Log.info("Default patched: ${file.absolutePath}")
                }

                success("Prism black overridden")
            }
        }
,
        @PInfos.TaskInfo("Patch Prism Compose colors")
        object : InstafelTask() {
            override fun execute() {
                val file = getProjectDir().walkTopDown()
                    .firstOrNull { it.isFile && it.name == "BasePrismColorsV2.smali" }

                if (file == null) {
                    Log.info("BasePrismColorsV2.smali not found")
                    return
                }

                if (patchGray1600(file)) {
                    success("GRAY_1600 patched")
                } else {
                    Log.info("GRAY_1600 not patched")
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


    private fun patchGray1600(file: File): Boolean {
        val lines = file.readLines().toMutableList()

        for (i in lines.indices) {
            val line = lines[i].trim()
            if (!line.startsWith("sput-wide") || !line.contains("->GRAY_1600:J")) continue

            for (j in i - 1 downTo maxOf(0, i - 20)) {
                val prev = lines[j].trim()

                if (prev.startsWith("const-wide")) {
                    val register = prev.substringAfter(' ').substringBefore(',').trim()
                    val indent = lines[j].takeWhile { it == ' ' || it == '\t' }
                    lines[j] = "${indent}const-wide $register, 0xff000000L"
                    file.writeText(lines.joinToString("\n"))
                    Log.info("Patched GRAY_1600 -> 0xff000000L")
                    return true
                }

                if (prev.startsWith("sput-wide") || prev.startsWith(".method")) {
                    break
                }
            }
        }

        return false
    }

}
