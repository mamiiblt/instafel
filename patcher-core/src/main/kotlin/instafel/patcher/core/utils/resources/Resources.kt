package instafel.patcher.core.utils.resources

import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File

class Resources<T : ResourceType> : Iterable<T> {

    val resources = mutableListOf<T>()
    var sourceFile: File? = null
    var resTypeName: String? = null
    private var resourcesTag: Element? = null

    var document: Document? = null
        set(value) {
            field = value
            resourcesTag = value?.getElementsByTagName("resources")?.item(0) as? Element
        }

    fun parse(tag: String, constructor: (Element) -> T) {
        document?.getElementsByTagName(tag)?.let { nodeList ->
            (0 until nodeList.length)
                .asSequence()
                .mapNotNull { nodeList.item(it) as? Element }
                .map(constructor)
                .forEach(resources::add)
        }
    }

    fun addExternalResource(item: T) {
        document?.importNode(item.element, true)?.let { imported ->
            (imported as? Element)?.also { element ->
                item.element = element
                resourcesTag?.appendChild(element)
                resources.add(item)
            }
        }
    }

    override fun iterator(): Iterator<T> = resources.iterator()
}