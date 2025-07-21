package me.mamiiblt.instafel.patcher.core.source;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;

import me.mamiiblt.instafel.patcher.core.utils.Log;

public class APKSigner {

    public static int execSigner(List<String> params, File APK_SIGNER_JAR) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        cmd.add("java");
        cmd.add("-jar");
        cmd.add(APK_SIGNER_JAR.getAbsolutePath());
        cmd.addAll(params);

        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Log.info("[SIGNER] " + line);
            }
        }

        return process.waitFor();
    }

    public static void moveOrDeleteApkSigner(boolean status, File APK_SIGNER_JAR, File KEYSTORE) throws IOException {
        if (status) {
            InputStream apkSignerJar = APKSigner.class.getResourceAsStream("/signing/uber-apk-signer.zip");
            InputStream defaultKeystore = APKSigner.class.getResourceAsStream("/signing/testkey.keystore");
            try (OutputStream outputStream = new FileOutputStream(APK_SIGNER_JAR)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = apkSignerJar.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (OutputStream outputStream = new FileOutputStream(KEYSTORE)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = defaultKeystore.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FileUtils.delete(APK_SIGNER_JAR);
            FileUtils.delete(KEYSTORE);
        }
    }
}
