package instafel.patcher.cli

import instafel.patcher.cli.handlers.CommandHandler
import instafel.patcher.cli.handlers.CoreHandler
import instafel.patcher.cli.utils.Log
import instafel.patcher.cli.utils.Utils

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