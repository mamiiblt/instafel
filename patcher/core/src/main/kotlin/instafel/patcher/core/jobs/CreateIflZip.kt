package instafel.patcher.core.jobs

import brut.directory.ExtFile
import instafel.patcher.core.source.SourceManager
import instafel.patcher.core.source.SourceUtils
import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.Utils
import instafel.patcher.core.utils.modals.CLIJob
import instafel.patcher.core.utils.resources.IFLResData
import instafel.patcher.core.utils.resources.ResourceParser
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.PrefixFileFilter
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

object CreateIflZip: CLIJob {

    lateinit var baseValuesDir: File
    lateinit var resDataBuilder: IFLResData.Builder

    override fun runJob(vararg args: Any) {
        val apkPath = args.getOrNull(0) as? String

        if (apkPath !is String) {
            Log.severe("Arguments must be strings")
            exitProcess(-1)
        }

        val apkFile = File(apkPath)
        val outputFolder = File(
            Utils.mergePaths(
                Env.USER_DIR,
                apkFile.name.replace(".apk", "") + "_temp"
            )
        )

        if (outputFolder.exists()) {
            Env.PROJECT_DIR = outputFolder.absolutePath
            Log.info("Output folder is exist")
        } else {
            Env.PROJECT_DIR = SourceUtils.createTempSourceDir(apkFile.name)
            val sourceManager = SourceManager().apply {
                config = SourceUtils.getDefaultIflConfigDecoder(config)
                config.frameworkDirectory = SourceUtils.getDefaultFrameworkDirectory()
                decompile(
                    ExtFile(
                        Utils.mergePaths(apkFile.absolutePath)
                    )
                )
            }
            Log.info("Base APK succesfully decompiled.")
        }

        baseValuesDir = File(
            Utils.mergePaths(
                Env.PROJECT_DIR,
                "sources",
                "res",
                "values"
            )
        )

        copyInstafelSmaliSources()
        copyRawSources()
    }

    fun copyInstafelSmaliSources() {
        Log.info("Copying Instafel smali sources")
        val sourceFolder = File(Utils.mergePaths(Env.PROJECT_DIR, "sources", "smali", "me", "mamiiblt", "instafel"))
        val destFolder = File(Utils.mergePaths(Env.PROJECT_DIR, "smali_sources"))
        FileUtils.copyDirectoryToDirectory(sourceFolder, destFolder)
        Log.info("Smali sources successfully copied")
    }

    fun copyRawSources() {
        Log.info("Copying Instafel resources into /res folder")

        val resFolder = File(Utils.mergePaths(Env.PROJECT_DIR, "sources", "res"))
        if (!resFolder.exists()) {
            FileUtils.forceMkdir(resFolder)
        }

        copyRawResource("drawable")
        copyRawResource("layout")
        parseResources()

        Utils.zipDirectory(
            Paths.get(Utils.mergePaths(Env.PROJECT_DIR, "smali_sources")),
            Paths.get(Utils.mergePaths(Env.PROJECT_DIR, "ifl_sources.zip"))
        )
        Utils.zipDirectory(
            Paths.get(Utils.mergePaths(Env.PROJECT_DIR, "res")),
            Paths.get(Utils.mergePaths(Env.PROJECT_DIR, "ifl_resources.zip"))
        )

        Utils.deleteDirectory(Utils.mergePaths(Env.PROJECT_DIR, "sources"))
        Utils.deleteDirectory(Utils.mergePaths(Env.PROJECT_DIR, "smali_sources"))
        Utils.deleteDirectory(Utils.mergePaths(Env.PROJECT_DIR, "res"))

        Log.info("Assets are ready!")
    }


    fun copyRawResource(folderName: String) {
        Log.info("Copying $folderName files...")

        val source = File(Utils.mergePaths(Env.PROJECT_DIR, "sources", "res", folderName))
        val dest = File(Utils.mergePaths(Env.PROJECT_DIR, "res", folderName))

        val files: Collection<File> = FileUtils.listFiles(
            source,
            PrefixFileFilter("ifl_"),
            null
        )

        for (file in files) {
            FileUtils.copyFileToDirectory(file, dest)
            Log.info("${file.name} copied.")
        }

        Log.info("Totally ${files.size} resource copied from $folderName")
    }

    fun parseResources() {
        try {
            resDataBuilder = IFLResData.Builder(
                File(Utils.mergePaths(Env.PROJECT_DIR, "ifl_data.xml"))
            )

            getAndAddInstafelString("")
            for (locale in Env.INSTAFEL_LOCALES) {
                getAndAddInstafelString(locale)
            }

            copyResourceAttr()
            copyResourceColor()
            copyResourceId()
            copyResourceStyle()
            copyResourcePublic()
            exportManifestThingsToResData()
            resDataBuilder.buildXml()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("Error while parsing / extracting resources")
        }
    }

    fun copyResourceColor() {
        val resColors = ResourceParser.parseResColor(File(Utils.mergePaths(baseValuesDir.absolutePath, "colors.xml")))
        val iflColors = resColors.resources.filter { it.name.startsWith("ifl_") }
        iflColors.forEach { resDataBuilder.addElToCategory("colors", it.element) }
        Log.info("Totally ${iflColors.size} color added to resource data.")
    }

    fun copyResourceAttr() {
        val resAttrs = ResourceParser.parseResAttr(File(Utils.mergePaths(baseValuesDir.absolutePath, "attrs.xml")))
        val iflAttrs = resAttrs.resources.filter { it.name.startsWith("ifl_") }
        iflAttrs.forEach { resDataBuilder.addElToCategory("attrs", it.element) }
        Log.info("Totally ${iflAttrs.size} attr added to resource data.")
    }

    fun copyResourceId() {
        val resIds = ResourceParser.parseResId(File(Utils.mergePaths(baseValuesDir.absolutePath, "ids.xml")))
        val iflIds = resIds.resources.filter { it.name.startsWith("ifl_") }
        iflIds.forEach { resDataBuilder.addElToCategory("ids", it.element) }
        Log.info("Totally ${iflIds.size} id added to resource data.")
    }

    fun copyResourcePublic() {
        val resPublics = ResourceParser.parseResPublic(File(Utils.mergePaths(baseValuesDir.absolutePath, "public.xml")))
        val iflPublics = resPublics.resources
            .filter { it.name.startsWith("ifl_") }
            .filterNot { it.name in listOf("ifl_ic_launcher", "ifl_ic_launcher_round") }

        iflPublics.forEach {
            it.element.removeAttribute("id")
            resDataBuilder.addElToCategory("public", it.element)
        }

        Log.info("Totally ${iflPublics.size} public added to resource data.")
    }

    fun copyResourceStyle() {
        val resStyles = ResourceParser.parseResStyle(File(Utils.mergePaths(baseValuesDir.absolutePath, "styles.xml")))
        val iflStyles = resStyles.resources.filter { it.name.startsWith("ifl_") }

        iflStyles.forEach { style ->
            if (style.name == "ifl_theme_light") {
                style.element.removeAttribute("parent")
            }

            resStyles.document!!.createElement("item").apply {
                setAttribute("name", "igds_color_link")
                textContent = "@color/ifl_white"
                style.element.appendChild(this)
            }

            resDataBuilder.addElToCategory("styles", style.element)
        }

        Log.info("Totally ${iflStyles.size} style added to resource data.")
    }

    fun exportManifestThingsToResData() {
        Log.info("Exporting activities & providers from Instafel base")

        val manifestPath = File(Utils.mergePaths(Env.PROJECT_DIR, "sources", "AndroidManifest.xml"))

        ResourceParser.getActivitiesFromManifest(manifestPath).forEach { element ->
            if (element.getAttribute("android:name").contains("ifl_a_menu")) {
                element.setAttribute("android:exported", "false")

                while (element.hasChildNodes()) {
                    element.removeChild(element.firstChild)
                }
            }
            resDataBuilder.addElToCategory("activities", element)
        }

        Log.info("Exporting providers from Instafel base")

        ResourceParser.getProvidersFromManifest(manifestPath)
            .filterNot { it.getAttribute("android:authorities").contains("androidx-startup") }
            .forEach { provider ->
                resDataBuilder.addElToCategory("providers", provider)
            }

        Log.info("Succesfully exported")
    }

    fun getAndAddInstafelString(langCodeParam: String) {
        var langCode = langCodeParam

        if (langCode.isNotEmpty()) {
            langCode = "-$langCode"
        }

        val resStrings = ResourceParser.parseResString(
            File(Utils.mergePaths(baseValuesDir.absolutePath + langCode, "strings.xml"))
        )

        val iflStrings = resStrings.resources.toMutableList()

        iflStrings.removeIf { !it.name.startsWith("ifl_") }

        for (iflStr in iflStrings) {
            resDataBuilder.addElToCategory("strings$langCode", iflStr.element)
        }

        if (langCode.contains("-")) {
            val cleanedLang = langCode.replace("-", "")
            Log.info("Totally ${iflStrings.size} strings added from $cleanedLang to resource data.")
        } else {
            Log.info("Totally ${iflStrings.size} strings added to resource data.")
        }
    }

}