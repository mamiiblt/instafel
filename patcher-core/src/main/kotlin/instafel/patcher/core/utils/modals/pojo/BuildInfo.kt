/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

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