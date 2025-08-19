package instafel.patcher.core.patches.clone

import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos

@PInfos.PatchInfo(
    name = "Clone General",
    shortname = "clone_general",
    desc = "It makes app compatible for clone generation",
    author = "mamiiblt",
    isSingle = false
)
class CloneGeneral: InstafelPatch() {
    override fun initializeTasks() = mutableListOf<InstafelTask>()
}