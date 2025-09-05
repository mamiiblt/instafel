package instafel.patcher

import instafel.patcher.handlers.CommandHandler
import instafel.patcher.handlers.CoreHandler
import instafel.patcher.utils.Log
import instafel.patcher.utils.Utils

fun main(args: Array<String>) {
    System.setProperty("java.awt.headless", "true")
    System.setProperty("jdk.nio.zipfs.allowDotZipEntry", "true")
    System.setProperty("jdk.util.zip.disableZip64ExtraFieldValidation", "true")

    Log.setupLogger()
    Utils.readPatcherProps()
    Utils.printPatcherHeader()

    CoreHandler.initializeHandler()
    CommandHandler(args)
}