package instafel.patcher.core.utils.modals.pojo

data class ConfigPOJO (
    var manifestVersion: Int = 1,
    var productionMode: Boolean = false,
    var managerToken: String = "",
    var githubPatToken: String = ""
)

data class ProjectPOJO(
    var apiBase: String = "",
    var igVersion: String = "",
    var igVersionCode: String = "",
    var generationId: String = "",
    var iflVersion: Int = 0,
    var appliedPatches: MutableList<PatchInfo> = mutableListOf(),
    var iflSourcesFolder: String = "",
    var pVClassPath: String = ""
)