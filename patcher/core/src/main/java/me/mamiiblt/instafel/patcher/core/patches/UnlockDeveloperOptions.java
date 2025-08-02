package me.mamiiblt.instafel.patcher.core.patches;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.mamiiblt.instafel.patcher.core.source.SmaliParser;
import me.mamiiblt.instafel.patcher.core.source.SmaliParser.SmaliInstruction;
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

    private SmaliUtils smaliUtils = getSmaliUtils();
    private String className = null;

    @Override
    public List<InstafelTask> initializeTasks() {
        return List.of(
            getDevOptionsTask,
            addConstraintLineTask
        );
    }

    InstafelTask getDevOptionsTask = new InstafelTask("Get dev options class from whoptions") {
        @Override
        public void execute() throws IOException {
            String igDeadCodeDetectionManagerPath = "com/instagram/deadcodedetection/IgDeadCodeDetectionManager";
            List<File> detectorFileQuery = smaliUtils.getSmaliFilesByName(igDeadCodeDetectionManagerPath);
            File igDeadCodeSessionSmali = null;
            if (detectorFileQuery.size() == 0 || detectorFileQuery.size() > 1) {
                failure("IgDeadCodeDetectionManager file can't be found / selected.");
            } else {
                igDeadCodeSessionSmali = detectorFileQuery.get(0);
                Log.info("IgDeadCodeDetectionManager file is found");
            }

            List<String> detectorContent = smaliUtils.getSmaliFileContent(igDeadCodeSessionSmali.getAbsolutePath());
            List<LineData> linesWithInvokeAndUserSession = smaliUtils.getContainLines(
                detectorContent, "(Lcom/instagram/common/session/UserSession;)Z", "invoke-static"
            );

            if (linesWithInvokeAndUserSession.size() != 1) {
                failure("Static caller opcode can't found or more than 1!");
            }

            LineData callLine = linesWithInvokeAndUserSession.get(0);
            SmaliInstruction callLineInstruction = SmaliParser.parseInstruction(callLine.getContent(), callLine.getNum());
            className = callLineInstruction.getClassName().replace("LX/", "").replace(";", "");
            success("DevOptions class is " + className);
        }
    };

    InstafelTask addConstraintLineTask = new InstafelTask("Add constraint line to LX/? class") {
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
