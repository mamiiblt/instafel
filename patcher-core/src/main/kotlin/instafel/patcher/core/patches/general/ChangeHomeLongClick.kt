package instafel.patcher.core.patches.general

import instafel.patcher.core.source.SmaliParser
import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.SearchUtils
import instafel.patcher.core.utils.modals.FileSearchResult
import instafel.patcher.core.utils.modals.LineData
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

@PInfos.PatchInfo(
    name = "Change Home Long Click",
    shortname = "change_home_long_click",
    desc = "Changes the home button long press function to call InstafelHomeSheet",
    isSingle = true
)
class ChangeHomeLongClick: InstafelPatch() {

    lateinit var homeLongClickClass: File
    lateinit var activityVariableName: String
    lateinit var castClassVariableName: String
    lateinit var fNavigatorClassName: String
    lateinit var fNavigatorCreatorMethodName: String
    lateinit var fNavigatorTransitionMethodName: String

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Find home button long click smali class")
        object: InstafelTask() {
            override fun execute() {
                when (val result = runBlocking {
                    SearchUtils.getFileContainsAllCords(smaliUtils,
                        listOf(
                            listOf(".super", "Ljava/lang/Object;"),
                            listOf(".implements", "Landroid/view/View\$OnLongClickListener"),
                            listOf("iput-object", ":Lcom/instagram/mainactivity/InstagramMainActivity;"),
                            listOf("iput-object", ":Lcom/instagram/common/session/UserSession;"),
                            listOf("\"click\""),
                            listOf("\"activity\"")
                        ))
                }) {
                    is FileSearchResult.Success -> {
                        homeLongClickClass = result.file
                        success("Home long click class found successfully")
                    }
                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no any matching classes found.")
                    }
                }
            }
        },
        @PInfos.TaskInfo("Change onLongClick function for handle home sheet operations")
        object: InstafelTask() {
            override fun execute() {
                val fContent = smaliUtils.getSmaliFileContent(homeLongClickClass.absolutePath).toMutableList()
                activityVariableName = homeLongClickClass.name.replace(".smali", "")
                Log.info("Home long click handler class is LX/$activityVariableName")

                val classFieldVNameRegex = Regex("""\.field\s+public\s+final\s+synthetic\s+(A\d+):""")
                val userSessionFields: List<LineData> = smaliUtils.getContainLines(fContent, ".field", "Lcom/instagram/common/session/UserSession;")
                val mainActivityFields: List<LineData> = smaliUtils.getContainLines(fContent, ".field", "Lcom/instagram/mainactivity/InstagramMainActivity;")

                if (userSessionFields.isEmpty() || mainActivityFields.isEmpty()) {
                    failure("UserSession and InstagramMainActivity variable cannot detected in handler class.")
                    exitProcess(-1)
                }

                var userSessionVariable = classFieldVNameRegex.find(userSessionFields[0].content)?.groupValues?.get(1)
                var mainActivityVariable = classFieldVNameRegex.find(mainActivityFields[0].content)?.groupValues?.get(1)

                var newMethodContent = """
                    .method public final onLongClick(Landroid/view/View;)Z
                        .registers 6

                        # Added for class re-foundabilidity
                        # "click"
                        # "activity"

                        iget-object v0, p0, LX/$activityVariableName;->$mainActivityVariable:Lcom/instagram/mainactivity/InstagramMainActivity;

                        iget-object v1, p0, LX/$activityVariableName;->$userSessionVariable:Lcom/instagram/common/session/UserSession;

                        invoke-static {v0, v1}, Linstafel/app/utils/DevHolder;->set(Lcom/instagram/mainactivity/InstagramMainActivity;Lcom/instagram/common/session/UserSession;)V

                        new-instance v2, Linstafel/app/utils/HomeSheetHandler;

                        invoke-direct {v2, v0}, Linstafel/app/utils/HomeSheetHandler;-><init>(Landroid/content/Context;)V

                        invoke-interface {v2, p1}, Landroid/view/View${'$'}OnLongClickListener;->onLongClick(Landroid/view/View;)Z

                        move-result v0

                        return v0
                    .end method
                """.trimIndent()

                val newFileContent = smaliUtils.removeMethodContent(fContent, "onLongClick", "(Landroid/view/View;)Z").toMutableList()
                newFileContent.addAll(newMethodContent.split("\n"))
                smaliUtils.writeContentIntoFile(homeLongClickClass.absolutePath, newFileContent)
                success("Home button long press event successfully modified.")
            }
        },
        @PInfos.TaskInfo("Add DevHolder class into app/utils package.")
        object: InstafelTask() {
            override fun execute() {
                val classContent = Env.slurp(ChangeHomeLongClick::class.java.getResourceAsStream("/patch_exts/DevHolder.smali"))
                val filePath = "${Env.PROJECT_DIR}/sources/${Env.Project.iflSourcesFolder}/instafel/app/utils/DevHolder.smali"
                smaliUtils.writeContentIntoFile(filePath, classContent.split("\n"))
                success("DevHolder class successfully created.")
            }
        },
        @PInfos.TaskInfo("Find session casting class from a reference class.")
        object: InstafelTask() {
            override fun execute() {
                // get casting class name via a reference class
                val refClass = smaliUtils.getSmaliFilesByName("/com/facebook/FacebookActivity.smali")[0]
                val refClassContent = smaliUtils.getSmaliFileContent(refClass.absolutePath)

                refClassContent.forEachIndexed { i, line ->
                    if (line.contains("invoke-static") &&
                        line.contains("(Landroid/app/Activity;)LX/")
                    ) {
                        val parsedInst = SmaliParser.parseInstruction(line.trim(), i)
                        castClassVariableName = parsedInst.returnType.replace("LX/", "").replace(";", "")
                    }
                }

                if (castClassVariableName.isEmpty()) failure("Casting class name cannot catch from method return type...")
                success("Caster class name is $castClassVariableName")
            }
        },
        @PInfos.TaskInfo("Find correct class name and method names of FragmentNavigator class")
        object: InstafelTask() {
            override fun execute() {
                val refClass = smaliUtils.getSmaliFilesByName("/com/instagram/profile/fragment/UserDetailFragment.smali")[0]
                val refClassContent = smaliUtils.getSmaliFileContent(refClass.absolutePath)
                val invokeVirtualMainCall = smaliUtils.getContainLines(refClassContent, "(Landroidx/fragment/app/Fragment;)V")
                if (invokeVirtualMainCall.size > 1 || invokeVirtualMainCall.isEmpty()) failure("Correct caller line couldn't be found")

                val lineCreator = SmaliParser.parseInstruction(refClassContent[invokeVirtualMainCall[0].num], invokeVirtualMainCall[0].num)
                val lineCaller = SmaliParser.parseInstruction(refClassContent[invokeVirtualMainCall[0].num + 2], invokeVirtualMainCall[0].num + 2)

                val cName = lineCreator.className.replace("LX/", "").replace(";", "")
                fNavigatorClassName = cName
                fNavigatorCreatorMethodName = lineCreator.methodName
                fNavigatorTransitionMethodName = lineCaller.methodName

                Log.info("fNavigatorClassName is $cName")
                Log.info("fNavigatorCreatorMethodName is ${lineCreator.methodName}")
                Log.info("fNavigatorTransitionMethodName is ${lineCaller.methodName}")
                success("Everything is found successfully.")
            }
        },
        @PInfos.TaskInfo("Update openDeveloperOptions method")
        object: InstafelTask() {
            override fun execute() {
                val instafelSheetClass = smaliUtils.getSmaliFilesByName("/instafel/app/utils/InstafelHomeSheet.smali")[0]
                val classContent = smaliUtils.getSmaliFileContent(instafelSheetClass.absolutePath).toMutableList()
                val newMethodContent = """
                    
                    .method public openDeveloperOptions()V
                        .registers 7

                        invoke-static {}, Linstafel/app/utils/DevHolder;->getActivity()Lcom/instagram/mainactivity/InstagramMainActivity;

                        move-result-object v0

                        invoke-static {}, Linstafel/app/utils/DevHolder;->getSession()Lcom/instagram/common/session/UserSession;

                        move-result-object v1

                        check-cast v1, LX/$castClassVariableName;

                        new-instance v2, Lcom/instagram/debug/quickexperiment/QuickExperimentCategoriesFragment;

                        invoke-direct {v2}, Lcom/instagram/debug/quickexperiment/QuickExperimentCategoriesFragment;-><init>()V

                        new-instance v3, LX/$fNavigatorClassName;

                        invoke-direct {v3, v0, v1}, LX/$fNavigatorClassName;-><init>(Landroidx/fragment/app/FragmentActivity;LX/$castClassVariableName;)V

                        invoke-virtual {v3, v2}, LX/$fNavigatorClassName;->$fNavigatorCreatorMethodName(Landroidx/fragment/app/Fragment;)V

                        invoke-virtual {v3}, LX/$fNavigatorClassName;->$fNavigatorTransitionMethodName()V

                        invoke-virtual {p0}, Linstafel/app/utils/InstafelHomeSheet;->dismissSheetDialog()V

                        return-void
                    .end method
                """.trimIndent()

                val newFileContent = smaliUtils.removeMethodContent(classContent, "openDeveloperOptions", "()V").toMutableList()
                newFileContent.addAll(newMethodContent.split("\n"))
                smaliUtils.writeContentIntoFile(instafelSheetClass.absolutePath, newFileContent)
                success("openDeveloperOptions method successfully updated.")
            }
        },
    )
}
