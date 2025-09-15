package instafel.patcher.core.utils.modals

data class PatchesInfo (
    val manifestVersion: Int,
    val corePackage: String,
    val totalPatchSize: Int,
    val singles: List<PatchInfo>,
    val groups: List<PatchGroupInfo>
)

data class PatchGroupInfo (
    val path: String,
    val author: String,
    val shortname: String,
    val name: String,
    val desc: String,
    val patches: List<PatchInfo>
)

data class PatchInfo (
    val path: String,
    val author: String,
    val shortname: String,
    val name: String,
    val desc: String,
    val isSingle: Boolean,
    val tasks: Map<String, String> = emptyMap()
)