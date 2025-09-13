import org.gradle.api.Project
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.system.exitProcess

const val patchesPath = "instafel/patcher/core/patches"

fun filterPackageName(name: String): String = name.replace("instafel.patcher.core.", "")
fun filterPackageNameAndUtilsPatchPkg(name: String): String = name.replace("instafel.patcher.core.utils.patch.", "")
var patchesJSON = JSONObject()
var taskInfos = JSONObject()

fun generatePatchInfoObj(clazz: Class<*>): JSONObject {
    val infoObj = JSONObject()
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
                infoObj.put(m.name, value)
            }
        }

    val patchPath = filterPackageName(clazz.name)
    infoObj.put("path", patchPath)
    if (taskInfos.has(patchPath)) {
        infoObj.put("tasks", taskInfos.get(patchPath))
    } else {
        infoObj.put("tasks", JSONArray())
    }

    return infoObj;
}

fun processPatchGroup(clazz: Class<*>) {
    val groupObj = JSONObject()
    val infoAnnotation = clazz.annotations.firstOrNull {
        it.annotationClass.simpleName?.contains("PatchGroupInfo") == true
    }

    if (infoAnnotation == null) {
        println("Annotation of patch ${clazz.name} not found.")
        exitProcess(-1)
    }

    groupObj.put("path", filterPackageName(clazz.name))
    infoAnnotation.annotationClass.members
        .filter { it.parameters.size == 1 }
        .forEach { m ->
            val value = m.call(infoAnnotation)
            if (m.name != "hashCode" && m.name != "toString") {
                groupObj.put(m.name, value)
            }
        }

    groupObj.put("patches", JSONArray())

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
        val patchInfo = generatePatchInfoObj(patchClass.java)
        groupObj.getJSONArray("patches").put(patchInfo)
    }

    patchesJSON.getJSONArray("groups").put(groupObj)
}

fun processPatch(clazz: Class<*>) {
    val infoObj = generatePatchInfoObj(clazz)
    if (infoObj.getBoolean("isSingle")) {
        patchesJSON.getJSONArray("singles").put(infoObj)
    }
}

fun processTaskInfos(clazz: Class<*>) {
    val taskInfoAnnotation = clazz.annotations.firstOrNull {
        it.annotationClass.simpleName?.contains("TaskInfo") == true
    }

    if (taskInfoAnnotation == null) {
        println("Annotation of task ${clazz.name} not found.")
        exitProcess(-1)
    }

    taskInfoAnnotation.annotationClass.members
        .filter { it.parameters.size == 1 }
        .forEach { m ->
            val value = m.call(taskInfoAnnotation)
            if (m.name == "name") {
                val patchClassName = filterPackageName(clazz.name).substringBefore("$")
                if (!taskInfos.has(patchClassName)) taskInfos.put(patchClassName, JSONObject())
                taskInfos.getJSONObject(patchClassName).put(filterPackageName(clazz.name).substringAfter("initializeTasks$"), value)
            }
        }
}

fun Project.generatePatchesJSON(jarFile: File): File {
    patchesJSON.put("singles", JSONArray())
    patchesJSON.put("groups", JSONArray())

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
            patchClasses.forEach { clazz -> processPatch(clazz) }
            patchGroups.forEach { clazz -> processPatchGroup(clazz) }
            var tPatchSize = 0;

            tPatchSize += patchesJSON.getJSONArray("singles").count()
            val groups = patchesJSON.getJSONArray("groups").forEach { group ->
                group as JSONObject
                tPatchSize += group.getJSONArray("patches").count()
            }
            patchesJSON.put("total_patch_size", tPatchSize)
            patchesJSON.put("manifest_version", 1)
            patchesJSON.put("package", "instafel.patches.core.patches")

            println("Writing patches.json file...")
            val patchesFile = File("$buildDir/generated-build-infos/patches.json")
            patchesFile.parentFile.mkdirs()
            patchesFile.writeText(patchesJSON.toString(2))
            println("Patches JSON file saved into ${patchesFile.absolutePath} successfully.")

            return patchesFile;
        }
    }
}