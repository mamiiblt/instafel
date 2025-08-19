package instafel.patcher.core.patches

import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos

@PInfos.PatchInfo(
    name = "Change Visible Channel Name",
    shortname = "change_channel_name",
    desc = "Change visible channel name in Developer Options",
    author = "mamiiblt",
    isSingle = true
)
class ChangeVisibleChannelName: InstafelPatch() {
    override fun initializeTasks() = mutableListOf<InstafelTask>()
}