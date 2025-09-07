package instafel.patcher.core.source

import java.io.*
import org.apache.commons.io.FileUtils
import instafel.patcher.core.utils.Log

object APKSigner {

    @Throws(IOException::class, InterruptedException::class)
    @JvmStatic
    fun execSigner(params: List<String>, apkSignerJar: File): Int {
        val cmd = mutableListOf("java", "-jar", apkSignerJar.absolutePath)
        cmd.addAll(params)

        val builder = ProcessBuilder(cmd)
        builder.redirectErrorStream(true)
        val process = builder.start()

        BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                Log.info("[SIGNER] $line")
            }
        }

        return process.waitFor()
    }

    @Throws(IOException::class)
    @JvmStatic
    fun moveOrDeleteApkSigner(status: Boolean, apkSignerJar: File, keystore: File) {
        if (status) {
            APKSigner::class.java.getResourceAsStream("/signing/uber-apk-signer.zip")?.use { apkSignerJarStream ->
                FileOutputStream(apkSignerJar).use { outputStream ->
                    apkSignerJarStream.copyTo(outputStream)
                }
            } ?: throw IOException("uber-apk-signer.zip not found in core resources")

            APKSigner::class.java.getResourceAsStream("/signing/testkey.keystore")?.use { defaultKeystoreStream ->
                FileOutputStream(keystore).use { outputStream ->
                    defaultKeystoreStream.copyTo(outputStream)
                }
            } ?: throw IOException("testkey.keystore not found in core resources")
        } else {
            FileUtils.delete(apkSignerJar)
            FileUtils.delete(keystore)
        }
    }
}
