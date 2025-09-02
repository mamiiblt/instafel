package instafel.patcher.core.patches.clone

import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.Utils
import instafel.patcher.core.utils.modals.ProviderData
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import instafel.patcher.core.utils.resources.RTypes
import instafel.patcher.core.utils.resources.ResourceParser
import instafel.patcher.core.utils.resources.Resources
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FalseFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import org.json.JSONArray
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.InputStream

@PInfos.PatchInfo(
    name = "Clone General",
    shortname = "clone_general",
    desc = "It makes app compatible for clone generation",
    author = "mamiiblt",
    isSingle = true
)
class CloneGeneral: InstafelPatch() {


    lateinit var manifest: Document
    lateinit var manifestTag: Element
    var providerDatas = mutableListOf<ProviderData>()

    var cloneRefFolder = File(Utils.mergePaths(Env.PROJECT_DIR, "clone_ref"))
    var blacklistedPermissions = JSONArray(
    slurp(CloneGeneral::class.java.getResourceAsStream("/blacklisted_perms.json"))
    )
    val NEW_PACKAGE_NAME = "com.instafel.android"
    val NEW_APP_NAME = "Instafel"
    val TN_ANDROID_AUTHORITIES = "android:authorities"
    val TN_ANDROID_NAME = "android:name"
    val TN_ANDROID_LABEL = "android:label"

    override fun initializeTasks() = mutableListOf(
        object: InstafelTask("Replace app icon") {
            override fun execute() {
                val valuesFolder = File(Utils.mergePaths(Env.PROJECT_DIR, "sources", "res"))

                val allDirs = FileUtils.listFilesAndDirs(
                    valuesFolder,
                    FalseFileFilter.FALSE,
                    TrueFileFilter.INSTANCE
                )

                allDirs
                    .filter { it.isDirectory && it.name.startsWith("mipmap") }
                    .forEach { dir ->
                        listOf(
                            "ig_launcher_background.png" to "/clone_assets/ifl_clone_background.png",
                            "ig_launcher_foreground.png" to "/clone_assets/ifl_clone_foreground.png"
                        ).forEach { (fileName, assetPath) ->
                            val targetFile = File(Utils.mergePaths(dir.absolutePath, fileName))
                            if (targetFile.exists()) {
                                copyAssetFromResources(
                                    assetPath,
                                    File(targetFile.absolutePath.replace("sources", "clone_ref"))
                                )
                                Log.info("${fileName.substringBeforeLast('.').replace('_', ' ').capitalize()} copied to clone_ref in ${dir.name}")
                            }
                        }
                    }

                success("Icon images successfully copied.")
            }
        },
        object: InstafelTask("Change application package in manifest") {
            override fun execute() {
                FileUtils.forceMkdir(cloneRefFolder)
                val manifestFile = File(Utils.mergePaths(Env.PROJECT_DIR, "sources", "AndroidManifest.xml"))
                manifest = ResourceParser.parseResourceDocument(manifestFile)
                manifestTag = manifest.documentElement
                manifestTag.setAttribute("package", NEW_PACKAGE_NAME)
                updateRefManifest(manifest)
                success("Package attribute changed to $NEW_PACKAGE_NAME")
            }
        },
        object: InstafelTask("Replace app name") {
            override fun execute() {
                val appTag = ResourceParser.getElementsFromResFile(manifest, "application")[0]
                val appLabelResource = appTag.getAttribute(TN_ANDROID_LABEL).replace("@string/", "")

                val stringsFile = File(Utils.mergePaths(Env.PROJECT_DIR, "sources", "res", "values", "strings.xml"))
                val appStrings: Resources<RTypes.TString> = ResourceParser.parseResString(stringsFile)

                val targetString = appStrings.firstOrNull { it.name == appLabelResource }

                targetString?.let { tString ->
                    tString.element.textContent = NEW_APP_NAME
                    Log.info("Name changed in resource ${tString.name}")

                    val cloneStringsFile = File(appStrings.sourceFile?.absolutePath?.replace("sources", "clone_ref"))
                    FileUtils.copyFile(appStrings.sourceFile, cloneStringsFile)
                    ResourceParser.buildXmlFile(appStrings.document!!, cloneStringsFile)
                    success("App name successfully changed within clone ref.")
                } ?: run {
                    failure("String resource cannot be found.")
                }
            }
        },
        object: InstafelTask("Change providers in manifest") {
            override fun execute() {
                val providers = ResourceParser.getElementsFromResFile(manifest, "provider")
                providers.forEach { provider ->
                    val oldAuthority = provider.getAttribute(TN_ANDROID_AUTHORITIES)
                    val newAuthority = if ("com.instagram.android" in oldAuthority) {
                        oldAuthority.replace("com.instagram.android", NEW_PACKAGE_NAME)
                    } else {
                        "patcher_renamed_$oldAuthority"
                    }

                    provider.setAttribute(TN_ANDROID_AUTHORITIES, newAuthority)
                    val providerData = ProviderData(oldAuthority, newAuthority)
                    providerDatas.add(providerData)
                    Log.info("Authority changed, ${providerData.newAuthority}")
                }

                updateRefManifest(manifest)
                Log.info("All authorities (${providerDatas.size}) of providers are changed in manifest")
                Log.info("Changing providers in smali files...")

                smaliUtils.smaliFolders.forEach { folder ->
                    FileUtils.iterateFiles(File(folder.absolutePath), null, true).forEach { file ->
                        val fContent = smaliUtils.getSmaliFileContent(file.absolutePath).toMutableList()
                        var fileModified = false

                        fContent.forEachIndexed { index, line ->
                            providerDatas.forEach { pData ->
                                if (line.contains(pData.oldAuthority)) {
                                    fContent[index] = line.replace(pData.oldAuthority, pData.newAuthority)
                                    Log.info("Provider fixed in ${folder.name}/${file.name}")
                                    fileModified = true
                                }
                            }
                        }

                        if (fileModified) {
                            val cloneFile = File(file.absolutePath.replace("sources", "clone_ref"))
                            FileUtils.writeLines(cloneFile, fContent)
                        }
                    }
                }

                success("All providers updated.")
            }
        },
        object: InstafelTask("Change permissions in manifest") {
            override fun execute() {
                val permissions = mutableListOf<Element>().apply {
                    addAll(ResourceParser.getElementsFromResFile(manifest, "permission"))
                    addAll(ResourceParser.getElementsFromResFile(manifest, "uses-permission"))
                }

                permissions.removeIf { perm ->
                    (0 until blacklistedPermissions.length()).any { i ->
                        perm.getAttribute(TN_ANDROID_NAME).startsWith(blacklistedPermissions.getString(i))
                    }
                }

                permissions.forEach { perm ->
                    var name = perm.getAttribute(TN_ANDROID_NAME)
                    if ("com.instagram.android" in name) {
                        name = name.replace("com.instagram.android", NEW_PACKAGE_NAME)
                        perm.setAttribute(TN_ANDROID_NAME, name)
                        Log.info("Updated ${perm.tagName} to $name")
                    }
                }

                updateRefManifest(manifest)
                success("Permissions successfully updated")
            }
        }
    )

    // Patch utility functions

    fun copyAssetFromResources(resDir: String, distPath: File) {
        val inputStream = CloneGeneral::class.java.getResourceAsStream(resDir)
        FileUtils.copyToFile(inputStream, distPath)
    }

    fun updateRefManifest(manifest: Document) {
        val manifestFile = File(Utils.mergePaths(cloneRefFolder.absolutePath, "AndroidManifest.xml"))
        ResourceParser.buildXmlFile(manifest, manifestFile)
        Log.info("Reference manifest file updated.")
    }

    fun slurp(inputStream: InputStream): String = inputStream.bufferedReader().use { it.readText() }
}