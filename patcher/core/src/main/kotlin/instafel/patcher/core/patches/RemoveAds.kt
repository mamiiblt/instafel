package instafel.patcher.core.patches

import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos

// Thanks to ReVanced developers for made this patch possible!
@PInfos.PatchInfo(
    name = "Remove Ads",
    shortname = "remove_ads",
    desc = "Remove Ads in Instagram",
    author = "mamiiblt",
    isSingle = true
)
class RemoveAds: InstafelPatch() {
    override fun initializeTasks() = mutableListOf<InstafelTask>()
}