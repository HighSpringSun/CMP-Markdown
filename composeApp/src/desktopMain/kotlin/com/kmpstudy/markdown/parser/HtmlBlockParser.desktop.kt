package com.kmpstudy.markdown.parser

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode

//actual class HtmlBlockParser {
//    actual fun parseHtml(html: String): HtmlNode {
//        val fragment = org.jsoup.Jsoup.parseBodyFragment(html)
//        return if (fragment.body().children().isNotEmpty()) {
//            convertJsoupToHtmlNode(fragment.body().child(0))
//        } else {
//            HtmlNode.Text("")
//        }
//    }
//
//    private fun convertJsoupToHtmlNode(element: org.jsoup.nodes.Element?): HtmlNode {
//        if (element == null) return HtmlNode.Text("")
//
//        val tagName = element.tagName()
//        val attributes = element.attributes().associate { it.key to it.value }
//        val children = element.childNodes().map { node ->
//            when (node) {
//                is org.jsoup.nodes.TextNode -> HtmlNode.Text(node.text())
//                is org.jsoup.nodes.Element -> convertJsoupToHtmlNode(node)
//                else -> HtmlNode.Text("")
//            }
//        }
//        return HtmlNode.Element(tagName, attributes, children)
//    }
//
//}

actual class HtmlBlockParser {
    actual fun parseHtml(html: String): HtmlNode {
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