package instafel.patcher.core.patches

import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos

@PInfos.PatchInfo(
    name = "Extend Snooze Warning Duration",
    shortname = "ext_snooze_warning_dur",
    desc = "You can extend snooze activity duration with this patch",
    author = "mamiiblt",
    isSingle = true
)
class ExtendSnoozeWarningDuration: InstafelPatch() {
    override fun initializeTasks() = mutableListOf<InstafelTask>()
}