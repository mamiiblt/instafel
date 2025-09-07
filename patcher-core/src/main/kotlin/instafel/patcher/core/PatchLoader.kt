package instafel.patcher.core

import io.github.classgraph.ClassGraph
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelPatchGroup
import instafel.patcher.core.utils.patch.PInfos.PatchGroupInfo
import instafel.patcher.core.utils.patch.PInfos.PatchInfo
import java.lang.reflect.Modifier

object PatchLoader {

    const val DEFAULT_PACKAGE_NAME = "instafel.patcher.core.patches"

    fun findPatchGroupByShortname(shortName: String): InstafelPatchGroup? {
        ClassGraph()
            .acceptPackages(DEFAULT_PACKAGE_NAME)
            .enableClassInfo()
            .enableAnnotationInfo()
            .scan().use { scanResult ->
                for (classInfo in scanResult.getSubclasses(InstafelPatchGroup::class.java.name)) {
                    val clazz = Class.forName(classInfo.name)

                    if (!Modifier.isAbstract(clazz.modifiers) && clazz.isAnnotationPresent(PatchGroupInfo::class.java)) {
                        val info = clazz.getAnnotation(PatchGroupInfo::class.java)
                        if (shortName == "all" || info.shortname == shortName) {
                            val constructor = clazz.getDeclaredConstructor()
                            return constructor.newInstance() as InstafelPatchGroup
                        }
                    }
                }
            }
        return null
    }

    fun findPatchByShortname(shortPatchName: String): InstafelPatch? {
        ClassGraph()
            .acceptPackages(DEFAULT_PACKAGE_NAME)
            .enableClassInfo()
            .enableAnnotationInfo()
            .scan().use { scanResult ->
                for (classInfo in scanResult.getSubclasses(InstafelPatch::class.java.name)) {
                    val clazz = Class.forName(classInfo.name)

                    if (!Modifier.isAbstract(clazz.modifiers) && clazz.isAnnotationPresent(PatchInfo::class.java)) {
                        val info = clazz.getAnnotation(PatchInfo::class.java)
                        if (shortPatchName == "all" || info.shortname == shortPatchName) {
                            val constructor = clazz.getDeclaredConstructor()
                            return constructor.newInstance() as InstafelPatch
                        }
                    }
                }
            }
        return null
    }

    fun getPatchInfos(): List<PatchInfo> {
        val patches = findPatchClassesInPackage()
        return patches.mapNotNull { it.getAnnotation(PatchInfo::class.java) }
    }

    fun getPatchGroupInfos(): List<PatchGroupInfo> {
        val groups = findPatchGroupClassesInPackage()
        return groups.mapNotNull { it.getAnnotation(PatchGroupInfo::class.java) }
    }

    @Suppress("UNCHECKED_CAST")
    fun findPatchGroupClassesInPackage(): Set<Class<out InstafelPatchGroup>> {
        val groups = mutableSetOf<Class<out InstafelPatchGroup>>()
        ClassGraph()
            .acceptPackages(DEFAULT_PACKAGE_NAME)
            .enableClassInfo()
            .enableAnnotationInfo()
            .scan().use { scanResult ->
                for (classInfo in scanResult.getSubclasses(InstafelPatchGroup::class.java.name)) {
                    val clazz = classInfo.loadClass()
                    if (InstafelPatchGroup::class.java.isAssignableFrom(clazz)) {
                        groups.add(clazz as Class<out InstafelPatchGroup>)
                    }
                }
            }
        return groups
    }

    @Suppress("UNCHECKED_CAST")
    fun findPatchClassesInPackage(): Set<Class<out InstafelPatch>> {
        val patches = mutableSetOf<Class<out InstafelPatch>>()
        ClassGraph()
            .acceptPackages(DEFAULT_PACKAGE_NAME)
            .enableClassInfo()
            .enableAnnotationInfo()
            .scan().use { scanResult ->
                for (classInfo in scanResult.getSubclasses(InstafelPatch::class.java.name)) {
                    val clazz = classInfo.loadClass()
                    if (InstafelPatch::class.java.isAssignableFrom(clazz)) {
                        patches.add(clazz as Class<out InstafelPatch>)
                    }
                }
            }
        return patches
    }
}
