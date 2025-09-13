package instafel.patcher.core.patches.general

import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.Utils
import instafel.patcher.core.utils.modals.LastResourceIDs
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import instafel.patcher.core.utils.resources.IFLResData
import instafel.patcher.core.utils.resources.RTypes
import instafel.patcher.core.utils.resources.ResourceHelper
import instafel.patcher.core.utils.resources.ResourceParser
import instafel.patcher.core.utils.resources.ResourceType
import instafel.patcher.core.utils.resources.Resources
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.PrefixFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File

@PInfos.PatchInfo(
    name = "Copy Instafel Sources",
    shortname = "copy_instafel_src",
    desc = "This patch needs to executed for use Instafel stuffs",
    author = "mamiiblt",
    isSingle = false
)
class CopyInstafelSources: InstafelPatch() {

    lateinit var resDataParser: IFLResData.Parser
    var valuesFolderPath = Utils.mergePaths(Env.PROJECT_DIR, "sources", "res", "values");

    init {
        prepareResData()
    }

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Copy smali / resources")
        object: InstafelTask() {
            override fun execute() {
                val smallDexFolder: File? = smaliUtils.getSmallSizeSmaliFolder(smaliUtils.smaliFolders)

                if (smallDexFolder == null) {
                    failure("Smallest smali folder not found")
                    return;
                }

                val destFolder = File(
                    Utils.mergePaths(
                        Env.PROJECT_DIR,
                        "sources",
                        smallDexFolder.name,
                        "me", "mamiiblt"
                    )
                )

                Env.Project.setString(Env.Project.Keys.IFL_SOURCES_FOLDER, smallDexFolder.name)

                Log.info("Copying instafel resources")
                Utils.unzipFromResources(false, "/ifl_sources/ifl_sources.zip", destFolder.absolutePath)

                val igResourcesFolder = File(Utils.mergePaths(Env.PROJECT_DIR, "sources", "res"))
                Utils.unzipFromResources(false, "/ifl_sources/ifl_resources.zip", igResourcesFolder.absolutePath)
                success("Instafel resources copied")
            }
        },
        @PInfos.TaskInfo("Add activities & providers to manifest")
        object: InstafelTask() {
            override fun execute() {
                val manifestFile = File(Utils.mergePaths(Env.PROJECT_DIR, "sources", "AndroidManifest.xml"))
                val manifestDoc = ResourceParser.parseResourceDocument(manifestFile)

                val applicationElement = ResourceParser.getNodesFromResFile(manifestDoc, "application").item(0)
                val manifestElement = manifestDoc.documentElement

                val iflActivities = resDataParser.activities
                iflActivities.forEach { activity ->
                    applicationElement?.appendChild(manifestDoc.importNode(activity, true))
                }
                Log.info("Totally ${iflActivities.size} activity added")

                val iflProviders = resDataParser.providers
                iflProviders.forEach { provider ->
                    applicationElement?.appendChild(manifestDoc.importNode(provider, true))
                }
                Log.info("Totally ${iflProviders.size} provider added")

                val requestPermEl = manifestDoc.createElement("uses-permission").apply {
                    setAttribute("android:name", "android.permission.REQUEST_INSTALL_PACKAGES")
                }
                manifestElement.appendChild(requestPermEl)

                ResourceParser.buildXmlFile(manifestDoc, manifestFile) // build manifest xml file
                success("Activities & providers added successfully from Instafel base")
            }
        },
        @PInfos.TaskInfo("Merge Instafel strings with IG")
        object: InstafelTask() {
            override fun execute() {
                val igResources = ResourceParser.parseResString(getValueResourceFile("strings.xml"))

                resDataParser.resourcesStrings["strings"]?.let { sourceResources ->
                    mergeResources(igResources, sourceResources)
                }

                igResources.document?.let { doc ->
                    igResources.sourceFile?.let { file ->
                        ResourceParser.buildXmlFile(doc, file)
                    }
                }

                val strings: Map<String, Resources<RTypes.TString>> = resDataParser.resourcesStrings

                Env.INSTAFEL_LOCALES.forEach { locale ->
                    val targetFile = File(Utils.mergePaths("$valuesFolderPath-$locale", "strings.xml"))
                    val targetResources = ResourceParser.parseResString(targetFile)

                    strings["strings-$locale"]?.let { sourceResources ->
                        mergeResources(targetResources, sourceResources)
                    }
                }

                success("Instafel strings merged successfully.")
            }
        },
        @PInfos.TaskInfo("Copy IFL resources into Instagram")
        object: InstafelTask() {
            override fun execute() {
                mergeResources(ResourceParser.parseResColor(
                    getValueResourceFile("colors.xml")
                ), resDataParser.resourcesColor);
                mergeResources(ResourceParser.parseResAttr(
                    getValueResourceFile("attrs.xml")
                ), resDataParser.resourcesAttr);
                mergeResources(ResourceParser.parseResId(
                    getValueResourceFile("ids.xml")
                ), resDataParser.resourcesId);
                mergeResources(ResourceParser.parseResStyle(
                    getValueResourceFile("styles.xml")
                ), resDataParser.resourcesStyle);

                success("All resources merged succesfully");
            }
        },
        @PInfos.TaskInfo("Copy public resources with new IDs")
        object: InstafelTask() {
            override fun execute() {
                val igPublic: Resources<RTypes.TPublic> = ResourceParser.parseResPublic(getValueResourceFile("public.xml"))
                val categorizedIGPublics: Map<String, List<Int>> = ResourceHelper.getIDsWithCategory(igPublic)
                val lastResourceIds = ResourceHelper.getBiggestResourceID(categorizedIGPublics)

                val iflPublic: Resources<RTypes.TPublic> = resDataParser.resourcesPublic
                iflPublic.forEach { tPublic ->
                    val newId = lastResourceIds[tPublic.type] + 1
                    lastResourceIds[tPublic.type] = newId
                    tPublic.id = ResourceHelper.convertToHex(newId)
                }

                Log.info("Totally ${iflPublic.count()} public's id updated.")

                mergeResources(igPublic, iflPublic)
                success("ID's successfully defined.")
            }
        },
        @PInfos.TaskInfo("Update Instafel R classes")
        object: InstafelTask() {
            override fun execute() {
                val smaliFolder = File(
                    Utils.mergePaths(
                        smaliUtils.getSmaliFolderByPaths("me", "mamiiblt")!!.absolutePath,
                        "me", "mamiiblt", "instafel"
                    )
                )

                val files: Collection<File> = FileUtils.listFiles(smaliFolder, PrefixFileFilter("R$"), TrueFileFilter.INSTANCE)
                val igResources: Resources<RTypes.TPublic> = ResourceParser.parseResPublic(getValueResourceFile("public.xml"))
                igResources.resources.removeIf { !it.name.startsWith("ifl_") }
                files.forEach { file -> ResourceHelper.updateRClass(igResources, file) }

                success("All R classes updated successfully.")
            }
        }
    )

    private fun <T : ResourceType> mergeResources(target: Resources<T>, source: Resources<T>) {
        source.forEach { resource ->
            target.addExternalResource(resource)
        }

        target.document?.let { doc ->
            target.sourceFile?.let { file ->
                ResourceParser.buildXmlFile(doc, file)
            }
        }

        Log.info("Totally ${source.count()} resource(s) added to ${target.resTypeName}")
    }

    private fun getValueResourceFile(fileName: String): File {
        return File(Utils.mergePaths(valuesFolderPath, fileName))
    }

    private fun prepareResData() {
        val resDataPath = File(Utils.mergePaths(Env.PROJECT_DIR, "ifl_data_temp.xml"))
        Utils.copyResourceToFile("/ifl_sources/ifl_data.xml", resDataPath)
        resDataParser = IFLResData.Parser(resDataPath)
    }
}