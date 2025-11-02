package instafel.patcher.core.utils.resources

import instafel.patcher.core.utils.Env
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class IFLResData {

    class Parser(private val inputFile: File) {

        lateinit var resourcesAttr: Resources<RTypes.TAttr>
        lateinit var resourcesId: Resources<RTypes.TId>
        lateinit var resourcesColor: Resources<RTypes.TColor>
        lateinit var resourcesStyle: Resources<RTypes.TStyle>
        lateinit var resourcesPublic: Resources<RTypes.TPublic>
        lateinit var activities: List<Element>
        lateinit var providers: List<Element>
        val resourcesStrings = mutableMapOf<String, Resources<RTypes.TString>>()

        init {
            parseResData()
        }

        private fun parseResData() {
            resourcesColor = ResourceParser.parseResColor(inputFile)
            resourcesId = ResourceParser.parseResId(inputFile)
            resourcesStyle = ResourceParser.parseResStyle(inputFile)
            resourcesPublic = ResourceParser.parseResPublic(inputFile)
            resourcesAttr = ResourceParser.parseResAttr(inputFile)

            val document = ResourceParser.parseResourceDocument(inputFile)
            activities = ResourceParser.getElementsFromResFile(document, "activity")
            providers = ResourceParser.getElementsFromResFile(document, "provider")
            parseStringRes()
        }

        private fun parseStringRes() {
            val document = ResourceParser.parseResourceDocument(inputFile)
            val categories = ResourceParser.getElementsFromResFile(document, "vcategory")
                .filter { it.getAttribute("name").contains("strings") }

            categories.forEach { category ->
                val resourcesString = Resources<RTypes.TString>().apply {
                    sourceFile = inputFile
                    this.document = document
                }

                val categoryElements = category.childNodes
                for (i in 0 until categoryElements.length) {
                    val node = categoryElements.item(i)
                    if (node.nodeType == Node.ELEMENT_NODE) {
                        resourcesString.resources.add(RTypes.TString(node as Element))
                    }
                }

                resourcesStrings[category.getAttribute("name")] = resourcesString
            }
        }
    }

    class Builder(private val distFile: File) {

        val doc: Document
        private val elValues: Element
        private val categories = mutableMapOf<String, Element>()

        init {
            if (!distFile.exists()) distFile.createNewFile()

            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            doc = builder.newDocument()

            val elRoot = doc.createElement("instafel").apply {
                setAttribute("xmlns:android", "http://schemas.android.com/apk/res/android")
            }
            doc.appendChild(elRoot)

            elValues = doc.createElement("values").also { elRoot.appendChild(it) }

            val defaultCategories = listOf(
                "providers", "activities", "styles", "public",
                "ids", "attrs", "colors", "strings"
            )
            defaultCategories.forEach { createCategory(it) }
            Env.INSTAFEL_LOCALES.forEach { createCategory("strings-${it.androidLangCode}") }
        }

        private fun createCategory(categoryName: String) {
            val vcategoryEl = doc.createElement("vcategory").apply { setAttribute("name", categoryName) }
            categories[categoryName] = vcategoryEl
        }

        fun addElToCategory(categoryName: String, element: Element) {
            categories[categoryName]?.appendChild(doc.importNode(element, true))
        }

        fun buildXml() {
            categories.values.forEach { elValues.appendChild(it) }

            val transformerFactory = javax.xml.transform.TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer().apply {
                setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes")
                setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8")
                setOutputProperty(javax.xml.transform.OutputKeys.STANDALONE, "yes")
            }

            transformer.transform(
                DOMSource(doc),
                StreamResult(distFile)
            )
        }
    }
}
