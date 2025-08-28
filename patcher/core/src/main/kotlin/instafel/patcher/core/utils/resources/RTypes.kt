package instafel.patcher.core.utils.resources

import org.w3c.dom.Element

class RTypes {
    class TAttr(element: Element) : ResourceType(element) {
        var name: String
            get() = element.getAttribute("name")
            set(value) { element.setAttribute("name", value) }

        var format: String
            get() = element.getAttribute("format")
            set(value) { element.setAttribute("format", value) }
    }

    class TColor(element: Element) : ResourceType(element) {
        var name: String
            get() = element.getAttribute("name")
            set(value) { element.setAttribute("name", value) }

        var value: String
            get() = element.textContent
            set(value) { element.textContent = value }
    }

    class TId(element: Element) : ResourceType(element) {
        var type: String
            get() = element.getAttribute("type")
            set(value) { element.setAttribute("type", value) }

        var name: String
            get() = element.getAttribute("name")
            set(value) { element.setAttribute("name", value) }
    }

    class TPublic(element: Element) : ResourceType(element) {
        private var convertedID: Long = 0

        var type: String
            get() = element.getAttribute("type")
            set(value) { element.setAttribute("type", value) }

        var name: String
            get() = element.getAttribute("name")
            set(value) { element.setAttribute("name", value) }

        var id: String
            get() = element.getAttribute("id")
            set(value) { element.setAttribute("id", value) }

        var convertedId: Long
            get() = convertedID
            set(value) { convertedID = value }
    }

    class TString(element: Element) : ResourceType(element) {
        var name: String
            get() = element.getAttribute("name")
            set(value) { element.setAttribute("name", value) }

        var value: String
            get() = element.textContent
            set(value) { element.textContent = value }
    }

    class TStyle(element: Element) : ResourceType(element) {
        val name: String
            get() = element.getAttribute("name")
    }
}