package me.mamiiblt.instafel.patcher.core.patches;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import me.mamiiblt.instafel.patcher.core.source.SmaliParser;
import me.mamiiblt.instafel.patcher.core.source.SmaliParser.SmaliInstruction;
import me.mamiiblt.instafel.patcher.core.utils.Log;
import me.mamiiblt.instafel.patcher.core.utils.SmaliUtils;
import me.mamiiblt.instafel.patcher.core.utils.Utils;
import me.mamiiblt.instafel.patcher.core.utils.models.LineData;
import me.mamiiblt.instafel.patcher.core.utils.patch.InstafelPatch;
import me.mamiiblt.instafel.patcher.core.utils.patch.InstafelTask;
import me.mamiiblt.instafel.patcher.core.utils.patch.PInfos;
import org.apache.commons.io.FileUtils;

@PInfos.PatchInfo(
    name = "Unlock Developer Options",
    shortname = "unlock_developer_options",
    desc = "You can unlock developer options with applying this patch!",
    author = "mamiiblt",
    isSingle = true
)
public class UnlockDeveloperOptions extends InstafelPatch {

    private SmaliUtils smaliUtils = getSmaliUtils();
    private String className = null;
    private File unlockRefSmali = null;

    @Override
    public List<InstafelTask> initializeTasks() {
        return List.of(
                findReferanceSmaliFile,
                getDevOptionsClass,
                addConstraintLineTask
        );
    }

    InstafelTask getDevOptionsClass = new InstafelTask("Get contraint defination class") {
        @Override
        public void execute() throws Exception {
            List<String> referanceFileContent = smaliUtils.getSmaliFileContent(unlockRefSmali.getAbsolutePath());
            List<LineData> linesWithInvokeAndUserSession = smaliUtils.getContainLines(
                    referanceFileContent, "(Lcom/instagram/common/session/UserSession;)Z", "invoke-static"
            );

            if (linesWithInvokeAndUserSession.size() != 1) {
                for (String line : referanceFileContent) {
                    Log.info(line );
                }
                failure("Static caller opcode can't found or more than 1!");
            }

            LineData callLine = linesWithInvokeAndUserSession.get(0);
            SmaliInstruction callLineInstruction = SmaliParser.parseInstruction(callLine.getContent(), callLine.getNum());
            className = callLineInstruction.getClassName().replace("LX/", "").replace(";", "");
            success("DevOptions class is " + className);
        }
    };

    InstafelTask findReferanceSmaliFile = new InstafelTask("Find referance smali file in X folders") {
        @Override
        public void execute() throws Exception {
            boolean fileFoundLock = false;

            for (File folder : smaliUtils.getSmaliFolders()) {
                if (fileFoundLock) {
                    break;
                } else {
                    File xFolder = new File(Utils.mergePaths(folder.getAbsolutePath(), "X"));
                    Log.info("Searching in X folder of " + folder.getName());

                    Iterator<File> fileIterator = FileUtils.iterateFiles(xFolder, null, true);
                    while (fileIterator.hasNext()) {
                        File file = fileIterator.next();
                        List<String> fContent = smaliUtils.getSmaliFileContent(file.getAbsolutePath());

                        boolean[] conditions = {false, false, false, false};
                        for (String line : fContent) {
                            if (line.contains(".field public final") && line.contains(":Lcom/google/common/collect/EvictingQueue;")) {
                                conditions[0] = true;
                            }

                            if (line.contains(".field public final") && line.contains(":Lcom/instagram/common/session/UserSession;")) {
                                conditions[1] = true;
                            }

                            if (line.contains(".field public") && line.contains(":Ljava/lang/String;")) {
                                conditions[2] = true;
                            }

                            if (line.contains(".super LX/")) {
                                conditions[3] = true;
                            }
                        }

                        boolean passStatus = true;
                        for (boolean cond : conditions) {
                            if (!cond) {
                                passStatus = false;
                            }
                        }

                        if (passStatus) {
                            unlockRefSmali = file;
                            Log.info("File found in " + unlockRefSmali.getName() + " at " + folder.getName());
                            fileFoundLock = true;
                            break;
                        }
                    }
                }
            }
        }
    };

    InstafelTask addConstraintLineTask = new InstafelTask("Add constraint line to DevOptions class") {
        @Override
        public void execute() throws IOException {
            File devOptionsFile = smaliUtils.getSmaliFilesByName(
                "X/" + className + ".smali"
            ).get(0);
            List<String> devOptionsContent = smaliUtils.getSmaliFileContent(devOptionsFile.getAbsolutePath());
            List<LineData> moveResultLines = smaliUtils.getContainLines(
                devOptionsContent, "move-result", "v0");
            if (moveResultLines.size() != 1) {
                failure("Move result line size is 0 or bigger than 1");
            } 
            LineData moveResultLine = moveResultLines.get(0);
            if (devOptionsContent.get(moveResultLine.getNum() + 2).contains("const v0, 0x1")) {
                failure("Developer options already unlocked.");
            }

            devOptionsContent.add(moveResultLine.getNum() + 1, "    ");
            devOptionsContent.add(moveResultLine.getNum() + 2, "    const v0, 0x1");
            smaliUtils.writeContentIntoFile(devOptionsFile.getAbsolutePath(), devOptionsContent);
            Log.info("Contraint added succesfully.");
            success("Developer options unlocked succesfully.");
        }
    };
}
