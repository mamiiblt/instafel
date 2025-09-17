package instafel.patcher.core.utils.modals.pojo

data class PatchesInfo (
    val manifestVersion: Int,
    val corePackage: String,
    val totalPatchSize: Int,
    val singles: MutableList<PatchInfo>,
    val groups: MutableList<PatchGroupInfo>
)

data class PatchGroupInfo (
    val name: String,
    val desc: String,
    val shortname: String,
    val path: String,
    val patches: MutableList<PatchInfo>
)

data class PatchInfo (
    val name: String,
    val desc: String,
    val shortname: String,
    val path: String,
    val isSingle: Boolean,
    val groupShortname: String,
    val tasks: MutableList<String>
)