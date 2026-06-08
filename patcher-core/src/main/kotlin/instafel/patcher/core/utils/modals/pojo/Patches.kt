/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

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