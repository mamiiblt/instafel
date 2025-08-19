package instafel.patcher.core.patches

import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos

@PInfos.PatchInfo(
    name = "Unlock Developer Options",
    shortname = "unlock_developer_options",
    desc = "You can unlock developer options with applying this patch!",
    author = "mamiiblt",
    isSingle = true
)
class UnlockDeveloperOptions: InstafelPatch() {
    override fun initializeTasks() = mutableListOf<InstafelTask>()
}