package instafel.patcher.commands

import instafel.patcher.handlers.Command
import instafel.patcher.utils.Utils
import brut.androlib.ApktoolProperties

class AboutCmd: Command {
    override fun execute(args: Array<String>) {
        println("""
            CLI Version    : v${Utils.PROP_CLI_VERSION} (${Utils.PROP_CLI_PROJECT_TAG})
            CLI Commit     : ${Utils.PROP_CLI_COMMIT_HASH}@${Utils.PROP_CLI_PROJECT_BRANCH}
            Core Commit    : ${Utils.PROP_CORE_COMMIT}@${Utils.PROP_CORE_BRANCH}
            Apktool Ver.   : v${ApktoolProperties.getVersion()}
            Baksmali Ver.  : v${ApktoolProperties.getBaksmaliVersion()}
            Community      : t.me/instafel
            Repository     : github.com/mamiiblt/instafel
        """.trimIndent())
    }
}