package instafel.patcher.core.utils.patch

class PInfos {
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    annotation class PatchInfo(
        val name: String,
        val author: String,
        val desc: String,
        val shortname: String,
        val isSingle: Boolean,
    )

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    annotation class PatchGroupInfo(
        val name: String,
        val author: String,
        val desc: String,
        val shortname: String
    )

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    annotation class TaskInfo(
        val name: String
    )
}