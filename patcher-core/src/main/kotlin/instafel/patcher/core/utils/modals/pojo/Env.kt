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

import java.util.Locale

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
    var appliedPatches: AppliedPatchesPOJO = AppliedPatchesPOJO(),
    var iflSourcesFolder: String = "",
    var pVClassPath: String = ""
)

data class AppliedPatchesPOJO(
    var appliedPatchCounts: MutableMap<String, Int> = mutableMapOf(),
    var singlePatches: MutableList<PatchInfo> = mutableListOf(),
    var groupPatches: MutableList<PatchGroupInfo> = mutableListOf(),
)

data class PatcherLocaleInfo(
    var androidLangCode: String,
    var locale: Locale
)