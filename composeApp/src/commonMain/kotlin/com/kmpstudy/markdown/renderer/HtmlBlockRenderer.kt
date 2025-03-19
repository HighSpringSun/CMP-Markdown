package com.kmpstudy.markdown.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmpstudy.markdown.parser.HtmlNode


// 添加一个辅助函数来判断元素是否为内联元素
private fun isInlineElement(tagName: String): Boolean {
    return tagName in setOf(
        "a", "abbr", "acronym", "b", "bdi", "bdo", "big", "br", "button", "cite",
        "code", "data", "datalist", "del", "dfn", "em", "i", "img", "input",
        "kbd", "label", "map", "mark", "meter", "output", "picture", "progress",
        "q", "ruby", "s", "samp", "script", "select", "small", "span", "strong",
        "sub", "sup", "svg", "template", "textarea", "time", "u", "tt", "var", "wbr"
    )
}

// 添加一个辅助函数来判断元素是否为块级元素
private fun isBlockElement(tagName: String): Boolean {
    return tagName in setOf(
        "p", "div", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote", "pre",
        "ul", "ol", "li", "table", "thead", "tbody", "tr", "th", "td", "hr"
    )
}

// 修改主渲染函数，应用文本合并
@Composable
fun HtmlBlockRenderer(htmlNode: HtmlNode) {
    Column(
        modifier = Modifier,
    ) {
        when (htmlNode) {
            is HtmlNode.Element -> {
                when (htmlNode.tagName) {
                    "tldr" -> {
                        Column(
                            modifier = Modifier
                                .border(1.dp, Color(209, 209, 210), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            renderChildrenWithTextMerging(htmlNode.children)
                        }
                    }

                    "p" -> {
                        Column {
                            renderChildrenWithTextMerging(htmlNode.children)
                        }
                    }

                    // ... 其他代码保持不变 ...

                    "blockquote" -> {
                        Box(
                            modifier = Modifier
//                                .padding(vertical = 8.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color.LightGray,
                                    shape = MaterialTheme.shapes.small
                                )
                                .background(
                                    color = Color.LightGray.copy(alpha = 0.2f),
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(8.dp)
                        ) {
                            Column {
                                renderChildrenWithTextMerging(htmlNode.children)
                            }
                        }
                    }

                    // ... 其他代码保持不变 ...

                    "ul" -> {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            htmlNode.children.forEach { childNode ->
                                if (childNode is HtmlNode.Element && childNode.tagName == "li") {
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Text("• ", fontWeight = FontWeight.Bold)
                                        Column {
                                            renderChildrenWithTextMerging(childNode.children)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "ol" -> {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            htmlNode.children.forEachIndexed { index, childNode ->
                                if (childNode is HtmlNode.Element && childNode.tagName == "li") {
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Text("${index + 1}. ", fontWeight = FontWeight.Bold)
                                        Column {
                                            renderChildrenWithTextMerging(childNode.children)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ... 其他代码保持不变 ...

                    else -> {
                        if (isInlineElement(htmlNode.tagName)) {
                            // 内联元素应该由父元素处理，这里不应该单独渲染
                            // 但为了防止错误，仍然提供一个基本渲染
                            Text(
                                text = buildAnnotatedString {
                                    append(renderInlineElement(htmlNode))
                                }
                            )
                        } else {
                            // 未知的块级元素，按默认方式处理
                            Column(
                                modifier = Modifier
                            ) {
                                renderChildrenWithTextMerging(htmlNode.children)
                            }
                        }
                    }
                }
            }

            is HtmlNode.Text -> {
                Text(htmlNode.content)
            }
        }
    }
}


// 修改渲染子节点的函数
@Composable
private fun renderChildrenWithTextMerging(children: List<HtmlNode>) {
    // 将子节点分为内联组和块级组
    val groups = mutableListOf<Pair<Boolean, List<HtmlNode>>>() // Boolean表示是否为内联组
    var currentInlineGroup = mutableListOf<HtmlNode>()

    val filteredChildren = children.filter { node ->
        !(node is HtmlNode.Text && node.content.isBlank())
    }
    for (child in filteredChildren) {
        when (child) {
            is HtmlNode.Text -> {
                // 文本节点总是内联的
                currentInlineGroup.add(child)
            }

            is HtmlNode.Element -> {
                if (isInlineElement(child.tagName)) {
                    // 内联元素添加到当前内联组
                    currentInlineGroup.add(child)
                } else {
                    // 如果之前有内联组，先保存
                    if (currentInlineGroup.isNotEmpty()) {
                        groups.add(Pair(true, currentInlineGroup.toList()))
                        currentInlineGroup = mutableListOf()
                    }
                    // 添加块级元素作为单独的组
                    groups.add(Pair(false, listOf(child)))
                }
            }
        }
    }

    // 处理最后一组内联节点
    if (currentInlineGroup.isNotEmpty()) {
        groups.add(Pair(true, currentInlineGroup.toList()))
    }

    // 渲染每个组
    for ((isInline, group) in groups) {
        if (isInline) {
            // 内联组，合并渲染
            val combinedText = buildAnnotatedString {
                for (node in group) {
                    when (node) {
                        is HtmlNode.Text -> {
                            append(node.content)
                        }

                        is HtmlNode.Element -> {
                            append(renderInlineElement(node))
                        }
                    }
                }
            }
            Text(text = combinedText)
        } else {
            // 块级元素，直接渲染
            HtmlBlockRenderer(group[0])
        }
    }
}


// 添加一个函数来渲染内联元素
@Composable
private fun renderInlineElement(htmlNode: HtmlNode.Element): AnnotatedString {
    return buildAnnotatedString {
        when (htmlNode.tagName) {

            "br" -> {
                append('\n')
            }

            "strong", "b" -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
                pop()
            }

            "em", "i" -> {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
                pop()
            }

            "code" -> {
                pushStyle(
                    SpanStyle(
                        fontFamily = MaterialTheme.typography.body2.fontFamily,
                        background = MaterialTheme.colors.surface
                    )
                )
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
                pop()
            }

            "mark" -> {
                pushStyle(SpanStyle(background = Color.Yellow.copy(alpha = 0.5f)))
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
                pop()
            }

            "del", "s" -> {
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
                pop()
            }

            "u" -> {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
                pop()
            }

            "sub" -> {
                pushStyle(
                    SpanStyle(
                        baselineShift = BaselineShift.Subscript,
                        fontSize = 12.sp
                    )
                )
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
                pop()
            }

            "sup" -> {
                pushStyle(
                    SpanStyle(
                        baselineShift = BaselineShift.Superscript,
                        fontSize = 12.sp
                    )
                )
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
                pop()
            }

            "span" -> {
                val style = SpanStyle()
                val textDecoration = if (htmlNode.attributes.containsKey("text-decoration")) {
                    TextDecoration.Underline
                } else null

                pushStyle(style.copy(textDecoration = textDecoration))
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
                pop()
            }

            "a" -> {
                val href = htmlNode.attributes["href"] ?: ""
                pushStyle(
                    SpanStyle(
                        color = MaterialTheme.colors.primary,
                        textDecoration = TextDecoration.Underline
                    )
                )
                pushStringAnnotation(tag = "URL", annotation = href)
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
                pop() // 弹出URL注释
                pop() // 弹出样式
            }

            else -> {
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
            }
        }
    }
}


// 添加一个调试函数，打印HTML节点结构
fun debugHtmlNode(node: HtmlNode, indent: String = ""): String {
    return when (node) {
        is HtmlNode.Element -> {
            val sb = StringBuilder()
            sb.append("${indent}Element(tagName=${node.tagName}, attributes=${node.attributes})\n")
            node.children.forEach { child ->
                sb.append(debugHtmlNode(child, "$indent  "))
            }
            sb.toString()
        }

        is HtmlNode.Text -> {
            "${indent}Text(content=\"${node.content}\")\n"
        }
    }
}