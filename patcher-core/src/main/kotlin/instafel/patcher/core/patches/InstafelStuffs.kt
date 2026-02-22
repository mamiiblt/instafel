package instafel.patcher.core.patches

import instafel.patcher.core.patches.general.AddAppTrigger
import instafel.patcher.core.patches.general.ChangeHomeLongClick
import instafel.patcher.core.patches.general.AddInitInstafel
import instafel.patcher.core.patches.general.CopyInstafelSources
import instafel.patcher.core.patches.general.GetGenerationInfo
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelPatchGroup
import instafel.patcher.core.utils.patch.PInfos
import kotlin.reflect.KClass

@PInfos.PatchGroupInfo(
    name = "Instafel Stuffs",
    shortname = "instafel",
    desc = "You can add Instafel stuffs with this patches"
)
class InstafelStuffs: InstafelPatchGroup() {
    override fun initializePatches(): List<KClass<out InstafelPatch>> = mutableListOf(
        GetGenerationInfo::class,
        CopyInstafelSources::class,
        AddInitInstafel::class,
        ChangeHomeLongClick::class,
        AddAppTrigger::class
    )
}