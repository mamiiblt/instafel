import com.google.gson.GsonBuilder
import modals.PatchGroupInfo
import modals.PatchInfo
import modals.PatchesInfo
import org.gradle.api.Project
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.system.exitProcess

const val patchesPath = "instafel/patcher/core/patches"

fun filterPackageName(name: String): String = name.replace("instafel.patcher.core.", "")
fun filterPackageNameAndUtilsPatchPkg(name: String): String = name.replace("instafel.patcher.core.utils.patch.", "")

val gson = GsonBuilder().setPrettyPrinting().create()
val singles = mutableListOf<PatchInfo>()
val groups = mutableListOf<PatchGroupInfo>()
val tasksInfo = mutableMapOf<String, MutableMap<Int, String>>()
var tasksInfoOrdered = mutableMapOf<String, List<String>>()

fun generatePatchInfoObj(clazz: Class<*>): PatchInfo {
    lateinit var name: String;
    lateinit var shortname: String;
    lateinit var path: String;
    lateinit var desc: String;
    var isSingle = false;
    var tasks: List<String>

    val infoAnnotation = clazz.annotations.firstOrNull {
        it.annotationClass.simpleName?.contains("PatchInfo") == true
    }

    if (infoAnnotation == null) {
        println("Annotation of patch ${clazz.name} not found.")
        exitProcess(-1)
    }

    infoAnnotation.annotationClass.members
        .filter { it.parameters.size == 1 }
        .forEach { m ->
            val value = m.call(infoAnnotation)
            if (m.name != "hashCode" && m.name != "toString") {
                when (m.name) {
                    "name" -> name = value as String
                    "shortname" -> shortname = value as String
                    "isSingle" -> isSingle = value as Boolean
                    "desc" -> desc = value as String
                }
            }
        }

    val patchPath = filterPackageName(clazz.name)
    path = patchPath
    tasks = tasksInfoOrdered.getOrPut(patchPath) { mutableListOf() }

    return PatchInfo(name, desc, shortname, path, isSingle, tasks);
}

fun processPatchGroup(clazz: Class<*>) {
    lateinit var name: String;
    lateinit var shortname: String;
    lateinit var path: String;
    lateinit var desc: String;

    val infoAnnotation = clazz.annotations.firstOrNull {
        it.annotationClass.simpleName?.contains("PatchGroupInfo") == true
    }

    if (infoAnnotation == null) {
        println("Annotation of patch ${clazz.name} not found.")
        exitProcess(-1)
    }

    path = filterPackageName(clazz.name)
    infoAnnotation.annotationClass.members
        .filter { it.parameters.size == 1 }
        .forEach { m ->
            val value = m.call(infoAnnotation)
            when (m.name) {
                "name" -> name = value as String
                "shortname" -> shortname = value as String
                "desc" -> desc = value as String
            }
        }

    val patches = mutableListOf<PatchInfo>()

    val constructor = clazz.getDeclaredConstructor()
    constructor.isAccessible = true
    val instance = constructor.newInstance()
    val loadMethod = clazz.superclass.getDeclaredMethod("loadPatches")
    loadMethod.isAccessible = true
    loadMethod.invoke(instance)
    val patchesField = clazz.superclass.getDeclaredField("patches")
    patchesField.isAccessible = true
    val patchesList = patchesField.get(instance) as List<*>

    patchesList.forEach { patchKClass ->
        val patchClass = patchKClass as KClass<*>
        patches.add(generatePatchInfoObj(patchClass.java))
    }

    groups.add(PatchGroupInfo(
        name, desc, shortname, path, patches
    ))
}

fun processPatch(clazz: Class<*>) {
    val patchInfo = generatePatchInfoObj(clazz)
    if (patchInfo.isSingle) singles.add(patchInfo)
}

fun processTaskInfos(clazz: Class<*>) {
    val taskInfoAnnotation = clazz.annotations.firstOrNull {
        it.annotationClass.simpleName?.contains("TaskInfo") == true
    }

    if (taskInfoAnnotation == null) {
        println("Annotation of task ${clazz.name} not found.")
        exitProcess(-1)
    }

    lateinit var taskName: String
    taskInfoAnnotation.annotationClass.members
        .filter { it.parameters.size == 1 }
        .forEach { m ->
            val value = m.call(taskInfoAnnotation)
            if (m.name == "name") {
                taskName = value as String
            }
        }

    val patchClassName = filterPackageName(clazz.name).substringBefore("$")
    val taskOrder = Integer.parseInt(filterPackageName(clazz.name).substringAfter("initializeTasks$"))

    if (tasksInfo.containsKey(patchClassName)) {
        tasksInfo[patchClassName]?.put(taskOrder, taskName)
    } else {
        tasksInfo[patchClassName] = mutableMapOf(taskOrder to taskName)
    }
}

fun Project.generatePatchesJSON(jarFile: File): File {
    singles.clear()
    groups.clear()
    tasksInfo.clear()
    tasksInfoOrdered.clear()

    println("Loading Core JAR file...")
    JarFile(jarFile).use { jar ->
        val entries = jar.entries()
        val urls = arrayOf(URL("jar:file:${jarFile.absolutePath}!/"))
        URLClassLoader.newInstance(urls, Thread.currentThread().contextClassLoader).use { cl ->
            val taskClasses = mutableListOf<Class<*>>()
            val patchClasses = mutableListOf<Class<*>>()
            val patchGroups = mutableListOf<Class<*>>()

            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (entry.name.startsWith(patchesPath) && entry.name.endsWith(".class")) {
                    val className = entry.name
                        .replace('/', '.')
                        .removeSuffix(".class")

                    val clazz = cl.loadClass(className)
                    when (filterPackageNameAndUtilsPatchPkg(clazz.superclass.name)) {
                        "InstafelTask" -> taskClasses.add(clazz)
                        "InstafelPatch" -> patchClasses.add(clazz)
                        "InstafelPatchGroup" -> patchGroups.add(clazz)
                        else -> println("${clazz.name} isn't extends correct superclasses, got ignored.")
                    }
                }
            }

            taskClasses.forEach { clazz -> processTaskInfos(clazz) }
            tasksInfoOrdered = tasksInfo.mapValues { (_, tasksMap) ->
                tasksMap.toSortedMap()
                    .values.toList()
            }.toMutableMap()

            patchClasses.forEach { clazz -> processPatch(clazz) }
            patchGroups.forEach { clazz -> processPatchGroup(clazz) }

            var totalPatchSize = 0;
            totalPatchSize += singles.count()
            groups.forEach { group -> totalPatchSize += group.patches.count() }

            val patchesInfo = PatchesInfo(2, "instafel.patcher.core", totalPatchSize, singles, groups)

            println("Writing patches.json file...")
            val patchesFile = File("$buildDir/generated-build-infos/patches.json")
            patchesFile.parentFile.mkdirs()
            patchesFile.writeText(gson.toJson(patchesInfo))
            println("Patches JSON file saved into ${patchesFile.absolutePath} successfully.")

            return patchesFile;
        }
    }
}