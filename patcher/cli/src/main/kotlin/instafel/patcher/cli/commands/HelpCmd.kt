package instafel.patcher.cli.commands

import instafel.patcher.cli.handlers.Command

class HelpCmd: Command{
    override fun execute(args: Array<String>) {
        println("""
            usage:
              help                      Shows the list of available commands
              about                     Displays patcher info (version, apktool, etc.)
              list                      Lists all available patches
              init <apk file>           Initializes a working directory with the Instagram APK
              run <wdir> <patch name>   Applies the specified patch to the working directory
              build <wdir>              Builds the modified APK from the working directory
              update-core               Update Core JAR manually
              clear-cache               Clear patcher caches
              
            For a complete guide on using the patcher, visit:
            https://github.com/mamiiblt/instafel
        """.trimIndent())
    }
}