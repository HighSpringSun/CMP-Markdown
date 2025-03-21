package com.kmpstudy.markdown.parser

actual class HtmlBlockParser {
    actual fun parseHtml(html: String): HtmlNode {
        val fragment = org.jsoup.Jsoup.parseBodyFragment(html)
        return convertJsoupToHtmlNode(fragment.body().child(0))
    }

    private fun convertJsoupToHtmlNode(element: org.jsoup.nodes.Element?): HtmlNode {
        if (element == null) return HtmlNode.Text("")

        val tagName = element.tagName()
        val attributes = element.attributes().associate { it.key to it.value }
        val children = element.childNodes().map { node ->
            when (node) {
                is org.jsoup.nodes.TextNode -> HtmlNode.Text(node.text())
                is org.jsoup.nodes.Element -> convertJsoupToHtmlNode(node)
                else -> HtmlNode.Text("")
            }
        }
        return HtmlNode.Element(tagName, attributes, children)
    }

}