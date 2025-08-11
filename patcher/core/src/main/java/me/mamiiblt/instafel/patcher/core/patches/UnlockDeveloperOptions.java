package me.mamiiblt.instafel.patcher.core.patches;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.mamiiblt.instafel.patcher.core.utils.Log;
import me.mamiiblt.instafel.patcher.core.utils.SmaliUtils;
import me.mamiiblt.instafel.patcher.core.utils.models.LineData;
import me.mamiiblt.instafel.patcher.core.utils.patch.InstafelPatch;
import me.mamiiblt.instafel.patcher.core.utils.patch.InstafelTask;
import me.mamiiblt.instafel.patcher.core.utils.patch.PInfos;

@PInfos.PatchInfo(
    name = "Unlock Developer Options",
    shortname = "unlock_developer_options",
    desc = "You can unlock developer options with applying this patch!",
    author = "mamiiblt",
    isSingle = true
)
public class UnlockDeveloperOptions extends InstafelPatch {

    private final SmaliUtils smaliUtils = getSmaliUtils();
    private String className = null;

    @Override
    public List<InstafelTask> initializeTasks() {
        return List.of(
            findTargetClassTask,
            applyPatchTask
        );
    }

    // Task 1: Find the target smali class file by scanning all smali folders for files containing
    // the two required fields, and inside that file find invoke-static line to extract class path.
    InstafelTask findTargetClassTask = new InstafelTask("Find target smali class with required fields and invoke-static") {
        @Override
        public void execute() throws IOException {
            File[] smaliFolders = smaliUtils.getSmaliFolders();
            if (smaliFolders == null || smaliFolders.length == 0) {
                failure("No smali folders found.");
                return;
            }

            boolean found = false;

            // Iterate over all smali files in all smali folders
            for (File smaliFolder : smaliFolders) {
                Collection<File> files = org.apache.commons.io.FileUtils.listFiles(smaliFolder, null, true);

                for (File file : files) {
                    if (!file.getName().endsWith(".smali")) continue;

                    List<String> lines = smaliUtils.getSmaliFileContent(file.getAbsolutePath());

                    // Check if both required fields exist anywhere in file
                    boolean hasFieldA06 = false;
                    boolean hasFieldA09 = false;

                    for (String line : lines) {
                        String trimmed = line.trim();
                        if (trimmed.equals(".field public final A06:Lcom/google/common/collect/EvictingQueue;")) {
                            hasFieldA06 = true;
                        } else if (trimmed.equals(".field public final A09:Lcom/instagram/common/session/UserSession;")) {
                            hasFieldA09 = true;
                        }
                        if (hasFieldA06 && hasFieldA09) break;
                    }
                    if (!(hasFieldA06 && hasFieldA09)) continue;

                    // Now find invoke-static {p1}, L<classPath>;->A00(Lcom/instagram/common/session/UserSession;)Z
                    Pattern invokeStaticPattern = Pattern.compile(
                        "invoke-static \\{p1\\},\\s+(L([^;]+));->A00\\(Lcom/instagram/common/session/UserSession;\\)Z"
                    );

                    for (String line : lines) {
                        Matcher matcher = invokeStaticPattern.matcher(line.trim());
                        if (matcher.matches()) {
                            String extractedClassPath = matcher.group(2); // like X/5fp

                            int slashIndex = extractedClassPath.indexOf('/');
                            if (slashIndex >= 0 && slashIndex + 1 < extractedClassPath.length()) {
                                className = extractedClassPath.substring(slashIndex + 1);
                            } else {
                                className = extractedClassPath;
                            }

                            Log.info("Found target smali class: " + className + " from file: " + file.getName());
                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }
                if (found) break;
            }

            if (!found) {
                failure("Pattern with required fields and invoke-static {p1} not found.");
            }
        }
    };

    // Task 2: Locate target class file in sources/smali folder and check line count = 29 and patch it.
    InstafelTask applyPatchTask = new InstafelTask("Apply patch to target smali class") {
        @Override
        public void execute() throws IOException {
            if (className == null) {
                failure("Target class not found in previous step.");
                return;
            }

            File[] smaliFolders = smaliUtils.getSmaliFolders();
            if (smaliFolders == null || smaliFolders.length == 0) {
                failure("No smali folders found.");
                return;
            }

            File targetFile = null;

            // Only check sources/smali folder (not smali_classes*)
            for (File smaliFolder : smaliFolders) {
                if (!smaliFolder.getName().equalsIgnoreCase("smali")) continue;

                List<File> candidates = smaliUtils.getSmaliFilesByName("X/" + className + ".smali");
                for (File candidate : candidates) {
                    // Ensure candidate is inside this smali folder
                    String absPath = candidate.getAbsolutePath().replace('\\', '/');
                    if (absPath.startsWith(smaliFolder.getAbsolutePath().replace('\\', '/'))) {
                        targetFile = candidate;
                        break;
                    }
                }
                if (targetFile != null) break;
            }

            if (targetFile == null) {
                failure("Target smali file not found in sources/smali for class: X/" + className);
                return;
            }

            List<String> targetLines = smaliUtils.getSmaliFileContent(targetFile.getAbsolutePath());

            if (targetLines.size() != 27) {
                failure("Target file line count is not 27, actual: " + targetLines.size());
                return;
            }

            // Find 'move-result v0' line and check if patch already applied
            List<LineData> moveResultLines = smaliUtils.getContainLines(targetLines, "move-result", "v0");

            if (moveResultLines.size() != 1) {
                failure("Expected exactly one 'move-result v0' line, found: " + moveResultLines.size());
                return;
            }

            int insertIdx = moveResultLines.get(0).getNum() + 1;
            int checkIdx = insertIdx + 1;

            if (checkIdx >= targetLines.size()) {
                failure("Unexpected file structure when adding patch.");
                return;
            }

            if (targetLines.get(checkIdx).contains("const v0, 0x1")) {
                failure("Developer options already unlocked.");
                return;
            }

            targetLines.add(insertIdx, "    ");
            targetLines.add(insertIdx + 1, "    const v0, 0x1");

            smaliUtils.writeContentIntoFile(targetFile.getAbsolutePath(), targetLines);

            Log.info("Patch applied successfully to: " + targetFile.getName());
            success("Developer options unlocked successfully.");
        }
    };
}