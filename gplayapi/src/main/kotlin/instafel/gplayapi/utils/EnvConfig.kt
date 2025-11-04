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