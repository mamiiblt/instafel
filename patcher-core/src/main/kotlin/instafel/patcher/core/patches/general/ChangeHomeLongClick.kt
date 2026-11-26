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
import instafel.patcher.core.utils.SearchUtils
import instafel.patcher.core.utils.Utils
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
                when (val result = runBlocking {
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
                            // .67 fix, layer 1: the target's Activity /
                            // UserSession fields are `public final synthetic`
                            // (`.field` declaration lines). The tab-controller
                            // class holding the same dependencies declares
                            // plain `public final` fields, so it fails
                            // these two groups and is excluded. (`iput`
                            // lines never contain `synthetic`, so the marker
                            // has to live on the `.field` lines.)
                            listOf(
                                ".field",
                                "public final synthetic",
                                ":Lcom/instagram/mainactivity/InstagramMainActivity;"
                            ),
                            listOf(
                                ".field",
                                "public final synthetic",
                                ":Lcom/instagram/common/session/UserSession;"
                            ),
                            listOf("\"click\""),
                            listOf("\"activity\"")
                        )
                    )
                }) {
                    is FileSearchResult.Success -> {
                        homeLongClickClass = result.file
                        success("Home long click class found successfully.")
                    }

                    is FileSearchResult.NotFound -> {
                        // .67 fix, layer 2: on .67 the coordinate search
                        // matches two files (the true handler + a sibling
                        // long-press handler whose fields are synthetic
                        // too). Tie-break on the synthetic field count:
                        // the true handler declares exactly 5
                        // `.field public final synthetic` lines, the
                        // sibling 6. Fail closed if the tie-break does
                        // not single out one file — never take the first
                        // match (order is thread-dependent).
                        val picked = pickHomeLongClickBySyntheticFieldCount(
                            result.matchedFiles
                        )
                        if (picked != null) {
                            homeLongClickClass = picked
                            success(
                                "Home long click class found successfully " +
                                "(tie-break: " +
                                Utils.makeSmaliPathShort(picked) +
                                " declares exactly 5 synthetic fields)."
                            )
                        } else {
                            failure(
                                "No matching home long click class found. " +
                                "Candidates: " +
                                result.matchedFiles.joinToString(", ") {
                                    Utils.makeSmaliPathShort(it)
                                } + "."
                            )
                            exitProcess(-1)
                        }
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
                    failure("Required Activity/UserSession fields not found.")
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
                    failure("Could not extract required field names.")
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
                    failure("FragmentNavigator call not found.")
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
                    failure("FragmentNavigator transition call not found.")
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
                        "FragmentNavigator constructor parameters not found."
                    )
                    exitProcess(-1)
                }

                val constructorTypes =
                    tokenizeDescriptor(fNavigatorConstructorParams)

                if (constructorTypes.size != 2) {
                    failure(
                        "Unsupported FragmentNavigator constructor."
                    )
                    exitProcess(-1)
                }

                if (
                    constructorTypes[0] !=
                    "Landroidx/fragment/app/FragmentActivity;"
                ) {
                    failure(
                        "FragmentNavigator does not use FragmentActivity as first parameter."
                    )
                    exitProcess(-1)
                }

                fNavigatorSessionType = constructorTypes[1]

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
                    smaliUtils
                        .getSmaliFileContent(sheetClass.absolutePath)
                        .toMutableList()

                val updatedContent = smaliUtils
                    .removeMethodContent(
                        content,
                        "openDeveloperOptions",
                        "()V"
                    )
                    .toMutableList()

                updatedContent.addAll(
                    newMethod.split("\n")
                )

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

    companion object {
        /**
         * .67 fix, layer 2 — the true home long-click handler declares
         * exactly this many `.field public final synthetic` lines (5:
         * UserSession, InstagramMainActivity, the tab-controller, 03dm,
         * Function1). The sibling long-press handler for another tab
         * declares 6 (one extra tab-component field).
         */
        private const val HOME_LONG_CLICK_SYNTHETIC_FIELD_COUNT = 5
    }

    /**
     * .67 fix, layer 2 — structural tie-break for the coordinate search.
     * Returns the single candidate declaring exactly
     * [HOME_LONG_CLICK_SYNTHETIC_FIELD_COUNT] synthetic fields, or null
     * when zero or more than one qualify (fail closed).
     */
    private fun pickHomeLongClickBySyntheticFieldCount(
        candidates: List<File>
    ): File? {
        if (candidates.isEmpty()) return null
        val qualified = candidates.filter { file ->
            countSyntheticFields(file) ==
                HOME_LONG_CLICK_SYNTHETIC_FIELD_COUNT
        }
        return if (qualified.size == 1) qualified[0] else null
    }

    private fun countSyntheticFields(file: File): Int {
        return try {
            smaliUtils.getSmaliFileContent(file.absolutePath).count { line ->
                line.contains(".field") &&
                    line.contains("public final synthetic")
            }
        } catch (e: Exception) {
            -1
        }
    }

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
                        result.add(
                            descriptor.substring(start, index + 1)
                        )
                        index++
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