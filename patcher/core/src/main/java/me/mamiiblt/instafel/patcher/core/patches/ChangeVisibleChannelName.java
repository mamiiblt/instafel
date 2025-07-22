package me.mamiiblt.instafel.patcher.core.patches;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import me.mamiiblt.instafel.patcher.core.utils.Env;
import me.mamiiblt.instafel.patcher.core.utils.Log;
import me.mamiiblt.instafel.patcher.core.utils.SmaliUtils;
import me.mamiiblt.instafel.patcher.core.utils.Utils;
import me.mamiiblt.instafel.patcher.core.utils.patch.InstafelPatch;
import me.mamiiblt.instafel.patcher.core.utils.patch.InstafelTask;
import me.mamiiblt.instafel.patcher.core.utils.patch.PInfos;

@PInfos.PatchInfo(
    name = "Change Visible Channel Name", 
    shortname = "change_channel_name", 
    desc = "Change visible channel name in Developer Options", 
    author = "mamiiblt",
    isSingle = true
)
public class ChangeVisibleChannelName extends InstafelPatch {

    private final SmaliUtils smaliUtils = getSmaliUtils();
    private File constFile = null;
    String[] searchConstStrings = { "\"NONE\"", "\"ALPHA\"", "\"BETA\"", "\"PROD\"" };

    @Override
    public List<InstafelTask> initializeTasks() throws Exception {
        return List.of(findConstDefinationClass, changeConsts);
    }

    InstafelTask changeConsts = new InstafelTask("Change string constraints in file") {
        @Override
        public void execute() throws Exception {
            List<String> fContent = smaliUtils.getSmaliFileContent(constFile.getAbsolutePath());

            for (int i = 0; i < fContent.size(); i++) {
                String line = fContent.get(i);

                for (int a = 0; a < searchConstStrings.length; a++) {
                    String searchConst = searchConstStrings[a];
                    if (line.contains(searchConst)) {
                        int IFL_VER = Env.Project.getInteger(Env.Project.Keys.INSTAFEL_VERSION, 0);
                        String changeToStr = IFL_VER == 0 ? "Instafel" : "Instafel v" + IFL_VER + " ";
                        fContent.set(i, line.replace(searchConst, "\"" + changeToStr + "\""));
                        Log.info("Constraint " + searchConst + " found at line " + i);
                    }
                }
            }

            FileUtils.writeLines(constFile, fContent);
            success("All changable channel name constraints changed succesfully.");
        }
    };

    InstafelTask findConstDefinationClass = new InstafelTask("Find const defination class in X classes") {
        @Override
        public void execute() throws Exception {
            int scannedFileSize = 0;
            String cachePath = Env.Project.getString(Env.Project.Keys.P_VCLASS_PATH, "NFN");
            constFile = cachePath != "NFN" ? new File(cachePath) : null;

            if (constFile != null) {
                success("File path cached in project dir");
            } else {
                List<File> foundFiles = new ArrayList<>();

                for (File folder : smaliUtils.getSmaliFolders()) {
                    if (constFile != null) {
                        break;
                    } else {
                        File xFolder = new File(Utils.mergePaths(folder.getAbsolutePath(), "X"));

                        Iterator<File> fileIterator = FileUtils.iterateFiles(xFolder, null, true);
                        while (fileIterator.hasNext()) {
                            scannedFileSize++;
                            File file = fileIterator.next();
                            List<String> fContent = smaliUtils.getSmaliFileContent(file.getAbsolutePath());

                            boolean[] passStatues = new boolean[4];

                            for (String line : fContent) {
                                for (int i = 0; i < searchConstStrings.length; i++) {
                                    if (line.contains(searchConstStrings[i])) {
                                        passStatues[i] = true;
                                    }
                                }
                            }

                            boolean passStatus = true;
                            for (int i = 0; i < passStatues.length; i++) {
                                boolean cond = passStatues[i];
                                if (cond == false) {
                                    passStatus = false;
                                }
                            }

                            if (passStatus) {
                                Log.info("A file found in " + file.getName() + " at " + folder.getName());
                                foundFiles.add(file);
                            }
                        }
                    }
                }

                if (foundFiles.size() == 0 || foundFiles.size() > 1) {
                    failure("Found more files than one (or no any file found) for apply patch, add more condition for find correct file.");
                } else {
                    Log.info("Totally scanned " + scannedFileSize + " file in X folders");
                    Log.info("File name is " + foundFiles.get(0));
                    constFile = foundFiles.get(0);
                    Env.Project.setString(Env.Project.Keys.P_VCLASS_PATH, constFile.getAbsolutePath());
                    success("Const defination class succesfully found in X files");
                }
            }
        }
    };
}
