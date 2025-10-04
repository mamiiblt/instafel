package instafel.patcher.core.utils.modals.pojo

data class BuildInfo(
    val manifestVersion: Int,
    var patcher: Patcher,
    var patcherData: PatcherData,
    var fileInfos: FileInfos
)

data class Patcher(
    var commit: String,
    var version: String,
    var channel: String
)

data class PatcherData(
    var buildDate: String,
    var generationId: String,
    var iflVersion: Number,
    var igVersion: String,
    var igVersionCode: String
)

data class FileInfos(
    var unclone: FileInfo,
    var clone: FileInfo
)

data class FileInfo(
    var fileName: String,
    var fileHash: String
)