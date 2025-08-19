package instafel.patcher.core.patches

import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos

@PInfos.PatchInfo(
    name = "Example Patch",
    desc = "Example Patch Desc",
    author = "mamiiblt",
    shortname = "example_patch",
    isSingle = true
)
class ExamplePatch: InstafelPatch() {
    override fun initializeTasks() = mutableListOf(
        object : InstafelTask("Example Task 1") {
            override fun execute() {
                Log.info("Example Task 1 is running...")
                success("Task finished completely")
            }
        },
        object: InstafelTask("Example Task 2") {
            override fun execute() {
                Log.info("Example Task #2 is running...")
                success("Task finished completely")
            }
        }
    )
}