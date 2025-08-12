package instafel.patcher.cli

fun main(args: Array<String>) {
    System.setProperty("java.awt.headless", "true")
    System.setProperty("jdk.nio.zipfs.allowDotZipEntry", "true")
    System.setProperty("jdk.util.zip.disableZip64ExtraFieldValidation", "true")

    println("Project will be recoded from scratch.")
}