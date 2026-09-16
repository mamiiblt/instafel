/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.patcher.core.patches.general

import instafel.patcher.core.source.SmaliParser
import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.SearchUtils
import instafel.patcher.core.utils.modals.FileSearchResult
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
class ChangeHomeLongClick : InstafelPatch() {

    lateinit var homeLongClickClass: File
    lateinit var activityVariableName: String

    lateinit var fNavigatorClassName: String
    lateinit var fNavigatorCreatorMethodName: String
    lateinit var fNavigatorTransitionMethodName: String

    var fNavigatorConstructorParams = ""
    var fNavigatorSessionType = ""

    override fun initializeTasks() = mutableListOf(

        @PInfos.TaskInfo("Find home button long click smali class")
        object : InstafelTask() {
            override fun execute() {
                when (
                    val result = runBlocking {
                        SearchUtils.getFileContainsAllCords(
                            smaliUtils,
                            listOf(
                                listOf(".super", "Ljava/lang/Object;"),
                                listOf(
                                    ".implements",
                                    "Landroid/view/View\$OnLongClickListener"
                                ),
                                listOf(
                                    "iput-object",
                                    ":Lcom/instagram/mainactivity/InstagramMainActivity;"
                                ),
                                listOf(
                                    "iput-object",
                                    ":Lcom/instagram/common/session/UserSession;"
                                ),
                                listOf("\"click\""),
                                listOf("\"activity\"")
                            )
                        )
                    }
                ) {
                    is FileSearchResult.Success -> {
                        homeLongClickClass = result.file
                        success("Home long click class found successfully.")
                    }

                    is FileSearchResult.NotFound -> {
                        failure(
                            "Patch aborted because no matching home long click class was found."
                        )
                        exitProcess(-1)
                    }
                }
            }
        },

        @PInfos.TaskInfo(
            "Change onLongClick function for handle home sheet operations"
        )
        object : InstafelTask() {
            override fun execute() {
                val content = smaliUtils
                    .getSmaliFileContent(homeLongClickClass.absolutePath)
                    .toMutableList()

                activityVariableName =
                    homeLongClickClass.name.removeSuffix(".smali")

                val fieldRegex =
                    Regex("""\.field\s+public\s+final\s+synthetic\s+(A\d+):""")

                val userSessionField = smaliUtils.getContainLines(
                    content,
                    ".field",
                    "Lcom/instagram/common/session/UserSession;"
                )

                val activityField = smaliUtils.getContainLines(
                    content,
                    ".field",
                    "Lcom/instagram/mainactivity/InstagramMainActivity;"
                )

                if (
                    userSessionField.isEmpty() ||
                    activityField.isEmpty()
                ) {
                    failure(
                        "UserSession and InstagramMainActivity fields could not be detected."
                    )
                    exitProcess(-1)
                }

                val userSessionVariable =
                    fieldRegex
                        .find(userSessionField[0].content)
                        ?.groupValues
                        ?.getOrNull(1)

                val mainActivityVariable =
                    fieldRegex
                        .find(activityField[0].content)
                        ?.groupValues
                        ?.getOrNull(1)

                if (
                    userSessionVariable == null ||
                    mainActivityVariable == null
                ) {
                    failure("Could not extract Activity/UserSession field names.")
                    exitProcess(-1)
                }

                val newMethod = """
                    .method public final onLongClick(Landroid/view/View;)Z
                        .registers 6

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

                val updatedContent = smaliUtils
                    .removeMethodContent(
                        content,
                        "onLongClick",
                        "(Landroid/view/View;)Z"
                    )
                    .toMutableList()

                updatedContent.addAll(newMethod.split("\n"))

                smaliUtils.writeContentIntoFile(
                    homeLongClickClass.absolutePath,
                    updatedContent
                )

                success("Home button long press event successfully modified.")
            }
        },

        @PInfos.TaskInfo("Add DevHolder class into app/utils package.")
        object : InstafelTask() {
            override fun execute() {
                val inputStream =
                    ChangeHomeLongClick::class.java.getResourceAsStream(
                        "/patch_exts/DevHolder.smali"
                    )

                if (inputStream == null) {
                    failure("DevHolder.smali resource not found.")
                    exitProcess(-1)
                }

                val classContent = Env.slurp(inputStream)

                val filePath =
                    "${Env.PROJECT_DIR}/sources/" +
                    "${Env.Project.iflSourcesFolder}/" +
                    "instafel/app/utils/DevHolder.smali"

                smaliUtils.writeContentIntoFile(
                    filePath,
                    classContent.split("\n")
                )

                success("DevHolder class successfully created.")
            }
        },

        @PInfos.TaskInfo(
            "Find correct class name and method names of FragmentNavigator class"
        )
        object : InstafelTask() {
            override fun execute() {
                val refClass = smaliUtils
                    .getSmaliFilesByName(
                        "/com/instagram/profile/fragment/UserDetailFragment.smali"
                    )
                    .firstOrNull()

                if (refClass == null) {
                    failure("UserDetailFragment.smali not found.")
                    exitProcess(-1)
                }

                val content =
                    smaliUtils.getSmaliFileContent(refClass.absolutePath)

                val navigatorCall = smaliUtils.getContainLines(
                    content,
                    "invoke-virtual",
                    "(Landroidx/fragment/app/Fragment;)V"
                )

                if (navigatorCall.isEmpty()) {
                    failure(
                        "Correct FragmentNavigator caller line could not be found."
                    )
                    exitProcess(-1)
                }

                val matchLine = navigatorCall[0]

                val creator = SmaliParser.parseInstruction(
                    content[matchLine.num].trim(),
                    matchLine.num
                )

                fNavigatorClassName =
                    creator.className
                        .removePrefix("LX/")
                        .removeSuffix(";")

                fNavigatorCreatorMethodName =
                    creator.methodName

                val transitionIndex = matchLine.num + 2

                if (transitionIndex >= content.size) {
                    failure(
                        "Navigator transition call could not be found."
                    )
                    exitProcess(-1)
                }

                val transition = SmaliParser.parseInstruction(
                    content[transitionIndex].trim(),
                    transitionIndex
                )

                fNavigatorTransitionMethodName =
                    transition.methodName

                val initNeedle =
                    "LX/$fNavigatorClassName;-><init>("

                for (
                    index in
                    (matchLine.num - 1) downTo maxOf(0, matchLine.num - 15)
                ) {
                    val line = content[index].trim()

                    if (
                        line.contains("invoke-direct") &&
                        line.contains(initNeedle)
                    ) {
                        val start = line.indexOf("<init>(") + 7
                        val end = line.lastIndexOf(")")

                        if (start > 6 && end > start) {
                            fNavigatorConstructorParams =
                                line.substring(start, end)
                        }

                        break
                    }
                }

                if (fNavigatorConstructorParams.isEmpty()) {
                    val navigatorFiles = smaliUtils.getSmaliFilesByName(
                        "/X/$fNavigatorClassName.smali"
                    )

                    if (navigatorFiles.isNotEmpty()) {
                        val navigatorContent =
                            smaliUtils.getSmaliFileContent(
                                navigatorFiles[0].absolutePath
                            )

                        for (line in navigatorContent) {
                            val trimmed = line.trim()

                            if (
                                trimmed.startsWith(
                                    ".method public constructor <init>("
                                ) &&
                                !trimmed.contains("<init>()V")
                            ) {
                                val start = trimmed.indexOf("<init>(") + 7
                                val end = trimmed.lastIndexOf(")")

                                if (start > 6 && end > start) {
                                    fNavigatorConstructorParams =
                                        trimmed.substring(start, end)
                                }

                                break
                            }
                        }
                    }
                }

                if (fNavigatorConstructorParams.isEmpty()) {
                    failure(
                        "FragmentNavigator constructor parameters could not be detected."
                    )
                    exitProcess(-1)
                }

                val constructorTypes =
                    tokenizeDescriptor(fNavigatorConstructorParams)

                if (constructorTypes.size != 2) {
                    failure(
                        "Unsupported FragmentNavigator constructor. " +
                        "Expected 2 parameters but found ${constructorTypes.size}."
                    )
                    exitProcess(-1)
                }

                if (
                    constructorTypes[0] !=
                    "Landroidx/fragment/app/FragmentActivity;"
                ) {
                    failure(
                        "FragmentNavigator first constructor parameter is not FragmentActivity."
                    )
                    exitProcess(-1)
                }

                fNavigatorSessionType =
                    constructorTypes[1]

                Log.info("Navigator class: LX/$fNavigatorClassName")
                Log.info("Creator method: $fNavigatorCreatorMethodName")
                Log.info("Transition method: $fNavigatorTransitionMethodName")
                Log.info("Constructor params: $fNavigatorConstructorParams")
                Log.info("Session type: $fNavigatorSessionType")

                success(
                    "FragmentNavigator information found successfully."
                )
            }
        },

        @PInfos.TaskInfo("Update openDeveloperOptions method")
        object : InstafelTask() {
            override fun execute() {
                if (
                    fNavigatorClassName.isEmpty() ||
                    fNavigatorConstructorParams.isEmpty() ||
                    fNavigatorSessionType.isEmpty()
                ) {
                    failure(
                        "Required FragmentNavigator information is missing."
                    )
                    exitProcess(-1)
                }

                val sheetClass = smaliUtils
                    .getSmaliFilesByName(
                        "/instafel/app/utils/InstafelHomeSheet.smali"
                    )
                    .firstOrNull()

                if (sheetClass == null) {
                    failure("InstafelHomeSheet.smali not found.")
                    exitProcess(-1)
                }

                val newMethod = """
                    .method public openDeveloperOptions()V
                        .registers 7

                        invoke-static {}, Linstafel/app/utils/DevHolder;->getActivity()Lcom/instagram/mainactivity/InstagramMainActivity;

                        move-result-object v0

                        invoke-static {}, Linstafel/app/utils/DevHolder;->getSession()Lcom/instagram/common/session/UserSession;

                        move-result-object v1

                        check-cast v0, Landroidx/fragment/app/FragmentActivity;

                        check-cast v1, $fNavigatorSessionType

                        new-instance v2, Lcom/instagram/debug/quickexperiment/QuickExperimentCategoriesFragment;

                        invoke-direct {v2}, Lcom/instagram/debug/quickexperiment/QuickExperimentCategoriesFragment;-><init>()V

                        new-instance v3, LX/$fNavigatorClassName;

                        invoke-direct {v3, v0, v1}, LX/$fNavigatorClassName;-><init>($fNavigatorConstructorParams)V

                        invoke-virtual {v3, v2}, LX/$fNavigatorClassName;->$fNavigatorCreatorMethodName(Landroidx/fragment/app/Fragment;)V

                        invoke-virtual {v3}, LX/$fNavigatorClassName;->$fNavigatorTransitionMethodName()V

                        invoke-virtual {p0}, Linstafel/app/utils/InstafelHomeSheet;->dismissSheetDialog()V

                        return-void
                    .end method
                """.trimIndent()

                val content =
                    smaliUtils.getSmaliFileContent(
                        sheetClass.absolutePath
                    ).toMutableList()

                val updatedContent = smaliUtils
                    .removeMethodContent(
                        content,
                        "openDeveloperOptions",
                        "()V"
                    )
                    .toMutableList()

                updatedContent.addAll(newMethod.split("\n"))

                smaliUtils.writeContentIntoFile(
                    sheetClass.absolutePath,
                    updatedContent
                )

                success(
                    "openDeveloperOptions method successfully updated."
                )
            }
        }
    )

    private fun tokenizeDescriptor(
        descriptor: String
    ): List<String> {
        val result = mutableListOf<String>()
        var index = 0

        while (index < descriptor.length) {
            when (descriptor[index]) {
                'L' -> {
                    val end = descriptor.indexOf(';', index)
                    if (end == -1) break

                    result.add(
                        descriptor.substring(index, end + 1)
                    )

                    index = end + 1
                }

                '[' -> {
                    val start = index

                    while (
                        index < descriptor.length &&
                        descriptor[index] == '['
                    ) {
                        index++
                    }

                    if (
                        index < descriptor.length &&
                        descriptor[index] == 'L'
                    ) {
                        val end =
                            descriptor.indexOf(';', index)

                        if (end == -1) break

                        result.add(
                            descriptor.substring(start, end + 1)
                        )

                        index = end + 1
                    } else {
                        if (index < descriptor.length) {
                            result.add(
                                descriptor.substring(
                                    start,
                                    index + 1
                                )
                            )
                            index++
                        }
                    }
                }

                else -> {
                    result.add(
                        descriptor[index].toString()
                    )
                    index++
                }
            }
        }

        return result
    }
}