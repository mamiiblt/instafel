package instafel.patcher.core.patches

import instafel.patcher.core.patches.clone.CloneGeneral
import instafel.patcher.core.patches.clone.ClonePackageReplacer
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelPatchGroup
import instafel.patcher.core.utils.patch.PInfos
import kotlin.reflect.KClass

@PInfos.PatchGroupInfo(
    name = "Clone Patches",
    shortname = "clone",
    desc = "These patches needs to be applied for generate clone in build."
)
class Clone: InstafelPatchGroup() {
    override fun initializePatches(): List<KClass<out InstafelPatch>> = mutableListOf(
        CloneGeneral::class,
        ClonePackageReplacer::class
    )
}