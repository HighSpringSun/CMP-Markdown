package com.kmpstudy.markdown.parser

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode


// Common interface for HTML nodes
sealed class HtmlNode {
    data class Element(
        val tagName: String,
        val attributes: Map<String, String>,
        val children: List<HtmlNode>
    ) : HtmlNode()

    data class Text(val content: String) : HtmlNode()
}


expect class HtmlBlockParser_Deprecated() {

    fun parseHtml(html: String): HtmlNode
}


class HtmlBlockParser {
    fun parseHtml(html: String): HtmlNode {
        val document = Ksoup.parse(html)
        return if (document.childNodes().isNotEmpty()) {
            convertKsoupToHtmlNode(document.childNodes().first())
        } else {
            HtmlNode.Text("")
        }
    }

    private fun convertKsoupToHtmlNode(node: Node?): HtmlNode {
        if (node == null) return HtmlNode.Text("")

        return when (node) {
            is TextNode -> HtmlNode.Text(node.text())
            is Element -> {
                val tagName = node.tagName()
                val attributes = node.attributes().associate { it.key to it.value }
                val children = node.childNodes().map { childNode ->
                    convertKsoupToHtmlNode(childNode)
                }
                HtmlNode.Element(tagName, attributes, children)
            }

            else -> HtmlNode.Text("")
        }
    }
}