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

package instafel.gplayapi.utils

data class EnvConfig(
    val email: String,
    val aasToken: String,
)

data class LatestReleaseInfo(
    val versionName: String,
    val versionCode: Int,
    val updatedOn: String,
    val apkUrlBase: String,
    val apkUrlRes: String,
)