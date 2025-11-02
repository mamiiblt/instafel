package instafel.patcher.core.utils.resources

import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.resources.RTypes.TAttr
import instafel.patcher.core.utils.resources.RTypes.TColor
import instafel.patcher.core.utils.resources.RTypes.TId
import instafel.patcher.core.utils.resources.RTypes.TPublic
import instafel.patcher.core.utils.resources.RTypes.TString
import instafel.patcher.core.utils.resources.RTypes.TStyle
import org.w3c.dom.*
import java.io.File
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import org.xml.sax.SAXException

object ResourceParser {

    @Throws(ParserConfigurationException::class, IOException::class, SAXException::class)
    private fun <T : ResourceType> parseResource(
        file: File,
        tag: String,
        constructor: (Element) -> T,
        typeName: String
    ): Resources<T> {
        val doc = parseResourceDocument(file)
        return Resources<T>().apply {
            sourceFile = file
            document = doc
            resTypeName = typeName
            parse(tag, constructor)
        }
    }

    @Throws(ParserConfigurationException::class, IOException::class, SAXException::class)
    fun parseResString(file: File): Resources<TString> {
        Env.INSTAFEL_LOCALES.forEach { localeInfo ->
            if (file.absolutePath.contains("values-${localeInfo.androidLangCode}")) {
                return parseResource(file, "string", ::TString, "strings-${localeInfo.androidLangCode}")
            }
        }
        return parseResource(file, "string", ::TString, "strings")
    }

    fun parseResColor(file: File) = parseResource(file, "color", ::TColor, "colors")

    fun parseResAttr(file: File) = parseResource(file, "attr", ::TAttr, "attrs")

    fun parseResId(file: File): Resources<TId> {
        val res = parseResource(file, "item", ::TId, "ids")
        res.resources.removeAll { !it.element.hasAttribute("type") }
        return res
    }

    fun parseResPublic(file: File) = parseResource(file, "public", ::TPublic, "publics")

    fun parseResStyle(file: File) = parseResource(file, "style", ::TStyle, "styles")

    fun getActivitiesFromManifest(inpFile: File): List<Element> {
        check(inpFile.name == "AndroidManifest.xml") { "Input file is not an AndroidManifest file" }
        return getElementsFromResFile(parseResourceDocument(inpFile), "activity")
    }

    fun getProvidersFromManifest(inpFile: File): List<Element> {
        check(inpFile.name == "AndroidManifest.xml") { "Input file is not an AndroidManifest file" }
        return getElementsFromResFile(parseResourceDocument(inpFile), "provider")
    }

    fun parseResourceDocument(inputFile: File): Document {
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        return if (inputFile.exists()) {
            documentBuilder.parse(inputFile).apply { normalize() }
        } else {
            documentBuilder.newDocument().apply {
                xmlStandalone = true
                val root = createElement("resources")
                appendChild(root)
            }
        }
    }

    fun buildXmlFile(doc: Document, distFile: File) {
        distFile.parentFile.mkdirs()

        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer(
            StreamSource(ResourceParser::class.java.getResourceAsStream("/styling.xslt"))
        )
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8")
        transformer.setOutputProperty(OutputKeys.METHOD, "xml")
        transformer.transform(DOMSource(doc), StreamResult(distFile))
    }

    fun getElementsFromResFile(document: Document, tagName: String): List<Element> {
        document.documentElement.normalize()
        val nodeList = document.getElementsByTagName(tagName)
        return (0 until nodeList.length)
            .mapNotNull { nodeList.item(it) as? Element }
    }

    fun getNodesFromResFile(document: Document, tagName: String): NodeList {
        document.normalize()
        return document.getElementsByTagName(tagName)
    }
}
