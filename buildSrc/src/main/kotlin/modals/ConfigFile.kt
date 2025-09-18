package modals

data class ConfigFile(
    val gplayapi: Gplayapi,
    val patcher: Patcher,
    val app: App,
    val updater: Updater,
)

data class Patcher(
    val version: String,
    val tag: String
)

data class Gplayapi(
    val version: String,
    val tag: String
)

data class App(
    val signing: SigningOptions,
)

data class Updater(
    val version: String,
    val versionCode: Int,
    val signing: SigningOptions,
)

data class SigningOptions(
    val ksPath: String,
    val ksKeyPass: String,
    val ksAlias: String,
    val ksPass: String,
)