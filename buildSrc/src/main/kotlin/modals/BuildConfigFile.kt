package modals

data class BuildConfigFile(
    val gson: String,
    val kotlin_coroutines: String,
    val kotlin_stdlib: String,
    val kotlin_reflect: String,
    val gplayapi: String,
    val org_json: String,
    val okhttp: String,
    val commons_io: String,
    val apktool_lib: String,
    val classgraph: String,
    val jackson_databind: String,
    val jackson_yaml: String,
    val shizuku_api: String,
    val shizuku_provider: String,
    val rootbeer: String,
    val m3_preferences: String,
    val android: AndroidDependencies
)

data class AndroidDependencies(
    val work_manager: String,
    val appcompat: String,
    val material: String,
    val activity: String,
    val constraintlayout: String,
    val navigation_fragment: String,
    val navigation_ui: String,
    val preference: String
)