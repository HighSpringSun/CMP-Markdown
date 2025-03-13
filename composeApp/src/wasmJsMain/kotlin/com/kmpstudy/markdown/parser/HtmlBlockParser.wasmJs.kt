package com.kmpstudy.markdown.parser

import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.parsing.DOMParser

actual class HtmlBlockParser {
    actual fun parseHtml(html: String): HtmlNode {
        val parser = DOMParser()
        val document = parser.parseFromString(html, "text/html".toJsReference())
        return convertDomToHtmlNode(document.body!!.firstChild as HTMLElement)
    }


    private fun convertDomToHtmlNode(element: HTMLElement): HtmlNode.Element {
        val tagName = element.tagName.lowercase()
        val attributes = (0 until element.attributes.length).associate {
            val attr = element.attributes.item(it)
            attr!!.name to attr.value
        }
        val children = element.childNodes.asList().map { node ->
            when (node.nodeType) {
                Node.TEXT_NODE -> HtmlNode.Text(node.textContent ?: "")
                Node.ELEMENT_NODE -> convertDomToHtmlNode(node as HTMLElement)
                else -> HtmlNode.Text("")
            }
        }
        return HtmlNode.Element(tagName, attributes, children)
    }
}