package instafel.patcher.handlers

import instafel.patcher.utils.Utils
import instafel.patcher.utils.modals.UpdateInfo
import instafel.patcher.utils.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.MalformedURLException
import java.net.URLClassLoader
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.util.jar.JarFile
import kotlin.system.exitProcess

object CoreHandler {
    lateinit var CORE_CLASS_LOADER: URLClassLoader
    const val CORE_PACKAGE_NAME = "instafel.patcher.core"
    lateinit var CORE_DATA_FOLDER: File
    lateinit var CORE_INFO_FILE: File
    lateinit var CORE_JAR_FILE: File
    lateinit var INFO_DATA: JSONObject

    fun initializeHandler() {
        try {
            val coreDir = Paths.get(Utils.getPatcherFolder(), "core_data")
            CORE_DATA_FOLDER = coreDir.toFile()
            CORE_INFO_FILE = coreDir.resolve("info.json").toFile()
            CORE_JAR_FILE = coreDir.resolve("core.jar").toFile()

            createOrReadCoreData()
            checkDebugCoreJAR()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("Error while initializing core")
            exitProcess(-1)
        }
    }

    fun loadCoreInfo() {
        try {
            JarFile(CORE_JAR_FILE).use { jarFile ->
                val manifest = jarFile.manifest
                val attrs = manifest.mainAttributes

                Utils.PROP_CORE_COMMIT = attrs.getValue("Patcher-Core-Commit")
                Utils.PROP_CORE_BRANCH = attrs.getValue("Patcher-Core-Branch")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("Error while loading sources from core: ${e.message}")
            exitProcess(-1)
        }
    }

    private fun checkDebugCoreJAR() {
        val debugCore = File(Paths.get(Utils.USER_DIR, "ifl-patcher-core-" + Utils.PROP_CLI_COMMIT_HASH + ".jar").toString())
        if (debugCore.exists()) {
            Log.info("Patcher uses debug core")
            CORE_JAR_FILE = debugCore
            loadCoreJAR()
        } else {
            fetchCore()
        }
    }

    fun downloadCoreJAR(updateInfo: UpdateInfo, coreJarFile: File) {
        val url = "https://github.com/mamiiblt/instafel/raw/refs/heads/ft-releases/p-core/dist/ifl-pcore-${updateInfo.commit}.jar"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val body = response.body
                    body.byteStream().use { inputStream ->
                        FileOutputStream(coreJarFile).use { outputStream ->
                            val buffer = ByteArray(4096)
                            var bytesRead: Int

                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                outputStream.write(buffer, 0, bytesRead)
                            }
                        }
                    }

                    Log.info("Latest core successfully downloaded")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.severe("Error while downloading core..." + response.code)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("An error occured while downloading core...")
            exitProcess(-1)
        }
    }

    @Throws(Exception::class)
    fun getLatestCoreUpdateInfo(): UpdateInfo {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/mamiiblt/instafel/refs/heads/ft-releases/p-core/latest.json")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val jsonObject = JSONObject(response.body.string())
                return UpdateInfo().apply {
                    commit = jsonObject.getString("commit")
                    supported_pversion = jsonObject.getString("supported_pversion")
                }
            } else {
                throw Exception("An error occurred while reading/sending API request")
            }
        }
    }


    @Throws(Exception::class)
    fun fetchCore() {
        if (CORE_JAR_FILE.exists()) {
            val lastTs = INFO_DATA.getLong("lc_ts")
            val elapsedTime = (System.currentTimeMillis() - lastTs) / 1000
            if (elapsedTime >= 14400) {
                val uInfo = getLatestCoreUpdateInfo()

                JarFile(CORE_JAR_FILE).use { jarFile ->
                    val manifest = jarFile.manifest
                    val attrs = manifest.mainAttributes
                    val currentCommit = attrs.getValue("Patcher-Core-Commit")

                    if (currentCommit == uInfo.commit) {
                        loadCoreJAR()
                    } else {
                        if (uInfo.supported_pversion != Utils.PROP_CLI_VERSION) {
                            Log.severe("Latest core supports Patcher CLI version ${uInfo.supported_pversion} and newer.")
                            Log.severe("Core isn't compatible with your patcher, please update patcher for use latest core")
                            exitProcess(-1)
                        } else {
                            downloadCoreJAR(uInfo, CORE_JAR_FILE)
                            loadCoreJAR()
                            Log.info("Core updated (${uInfo.commit})")
                        }
                    }
                }
                updateInfoTS()
            } else {
                loadCoreJAR()
            }
        } else {
            Log.info("Core JAR not found, downloading...")
            val uInfo = getLatestCoreUpdateInfo()
            if (uInfo.supported_pversion != Utils.PROP_CLI_VERSION) {
                Log.severe("Latest core isn't compatible with your patcher, please update patcher for use latest core")
                exitProcess(-1)
            } else {
                downloadCoreJAR(uInfo, CORE_JAR_FILE)
                loadCoreJAR()
            }
        }
    }

    @Throws(MalformedURLException::class)
    private fun loadCoreJAR() {
        CORE_CLASS_LOADER = URLClassLoader(
            arrayOf(CORE_JAR_FILE.toURI().toURL()),
            CoreHandler::class.java.classLoader
        )
        loadCoreInfo()
    }

    @Throws(Exception::class)
    fun createOrReadCoreData() {
        if (!CORE_DATA_FOLDER.exists() || !CORE_INFO_FILE.exists()) {
            CORE_DATA_FOLDER.mkdirs()
            if (!CORE_INFO_FILE.createNewFile()) {
                throw Exception("Information file cannot be created, ask to dev...")
            }

            INFO_DATA = JSONObject().apply {
                put("lc_ts", System.currentTimeMillis())
            }
            FileUtils.writeStringToFile(CORE_INFO_FILE, INFO_DATA.toString(), StandardCharsets.UTF_8)
        } else {
            INFO_DATA = JSONObject(CORE_INFO_FILE.readText(Charsets.UTF_8))
        }
    }


    private fun updateInfoTS() {
        INFO_DATA = JSONObject()
        INFO_DATA.put("lc_ts", System.currentTimeMillis())
        FileUtils.writeStringToFile(CORE_INFO_FILE, INFO_DATA.toString(4), StandardCharsets.UTF_8)
    }

    fun invokeKotlinObject(
        className: String,
        methodName: String,
    ): Any? {
        return try {
            val clazz = CoreHandler.CORE_CLASS_LOADER.loadClass("$CORE_PACKAGE_NAME.$className")
            val instanceField = clazz.getField("INSTANCE")
            val instance = instanceField.get(null)
            val method = clazz.getMethod(methodName)
            method.invoke(instance)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Throws(Exception::class)
    fun invokeKotlinObjectWithParams(
        className: String,
        methodName: String,
        args: Array<Any?>
    ): Any? {
        return try {
            val clazz = CORE_CLASS_LOADER.loadClass("$CORE_PACKAGE_NAME.$className")
            val instanceField = clazz.getField("INSTANCE").get(null)
            val runJobMethod = clazz.getMethod(methodName, Array<Any>::class.java)
            runJobMethod.invoke(instanceField, arrayOf(*args))
        } catch (e: Exception) {
            throw Exception("An error occurred while reflecting the class $CORE_PACKAGE_NAME.$className", e)
        }
    }

}