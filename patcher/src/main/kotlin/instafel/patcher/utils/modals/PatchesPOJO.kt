package instafel.patcher.utils.modals

data class PatchesInfo (
    val manifestVersion: Int,
    val corePackage: String,
    val totalPatchSize: Int,
    val singles: List<PatchInfo>,
    val groups: List<PatchGroupInfo>
)

data class PatchGroupInfo (
    val name: String,
    val desc: String,
    val shortname: String,
    val path: String,
    val patches: List<PatchInfo>
)

data class PatchInfo (
    val name: String,
    val desc: String,
    val shortname: String,
    val path: String,
    val isSingle: Boolean,
    val groupShortname: String,
    val tasks: List<String>
)
