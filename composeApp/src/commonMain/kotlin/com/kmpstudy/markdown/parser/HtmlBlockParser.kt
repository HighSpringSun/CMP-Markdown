package com.kmpstudy.markdown.parser


// Common interface for HTML nodes
sealed class HtmlNode {
    data class Element(
        val tagName: String,
        val attributes: Map<String, String>,
        val children: List<HtmlNode>
    ) : HtmlNode()

    data class Text(val content: String) : HtmlNode()
}


expect class HtmlBlockParser() {

    fun parseHtml(html: String): HtmlNode
}