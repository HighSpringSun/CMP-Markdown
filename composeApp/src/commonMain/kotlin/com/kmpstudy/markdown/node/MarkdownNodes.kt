package com.kmpstudy.markdown.node

// MarkdownNodes.kt
sealed class MarkdownNode {
    data class Header(val level: Int, val content: String) : MarkdownNode()
    data class Paragraph(val content: String) : MarkdownNode()
    data class OrderedList(val items: List<ListItem>) : MarkdownNode()
    data class UnorderedList(val items: List<ListItem>) : MarkdownNode()
    data class CodeBlock(val language: String?, val content: String) : MarkdownNode()
    data class Image(val src: String, val alt: String) : MarkdownNode()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : MarkdownNode()

    data class ListItem(
        val content: List<MarkdownNode>,
        val level: Int
    ) : MarkdownNode()
}