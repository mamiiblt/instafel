package me.mamiiblt.instafel.patcher.core.patches;

import java.util.List;

import me.mamiiblt.instafel.patcher.core.patches.clone.CloneGeneral;
import me.mamiiblt.instafel.patcher.core.patches.clone.ClonePackageReplacer;
import me.mamiiblt.instafel.patcher.core.utils.patch.InstafelPatch;
import me.mamiiblt.instafel.patcher.core.utils.patch.InstafelPatchGroup;
import me.mamiiblt.instafel.patcher.core.utils.patch.PInfos;

@PInfos.PatchGroupInfo (
    name = "Clone Patches",
    shortname = "clone",
    desc = "These patchs needs to be applied for generate clone in build.",
    author = "mamiiblt"
)
public class Clone extends InstafelPatchGroup {

    @Override
    public List<Class<? extends InstafelPatch>> initializePatches() throws Exception {
        return List.of(
            CloneGeneral.class,
            ClonePackageReplacer.class
        );
    }
}
