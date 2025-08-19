package instafel.patcher.core.patches.clone

import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos

@PInfos.PatchInfo(
    name = "Replace Instagram Strings",
    shortname = "clone_replace_strs",
    desc = "It makes app compatible for clone generation",
    author = "mamiiblt",
    isSingle = false
)
class ClonePackageReplacer: InstafelPatch() {
    override fun initializeTasks() = mutableListOf<InstafelTask>()
}