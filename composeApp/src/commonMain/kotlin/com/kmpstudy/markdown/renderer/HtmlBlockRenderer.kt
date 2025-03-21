package com.kmpstudy.markdown.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.kmpstudy.markdown.localstate.LocalImageState
import com.kmpstudy.markdown.localstate.LocalInlineContent
import com.kmpstudy.markdown.parser.HtmlNode
import com.kmpstudy.markdown.renderer.style.RightBorderShape
import com.kmpstudy.markdown.util.isUrl
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


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
        modifier = Modifier
    ) {
        when (htmlNode) {
            is HtmlNode.Element -> {
                when (htmlNode.tagName) {
                    "tldr" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Color(209, 209, 210), RoundedCornerShape(6.dp))
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

                    "table" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .border(0.5.dp, Color.LightGray)
                        ) {
                            // 遍历表格的子节点
                            var hasHeader = false
                            var hasBody = false

                            // 先处理表头
                            htmlNode.children.forEach { childNode ->
                                if (childNode is HtmlNode.Element && childNode.tagName == "thead") {
                                    renderTableHeader(childNode)
                                    hasHeader = true
                                }
                            }

                            // 再处理表体
                            htmlNode.children.forEach { childNode ->
                                if (childNode is HtmlNode.Element) {
                                    when (childNode.tagName) {
                                        "tbody" -> {
                                            renderTableBody(childNode)
                                            hasBody = true
                                        }

                                        "tr" -> {
                                            // 如果直接有tr标签（没有thead和tbody包装）
                                            if (!hasHeader && !hasBody) {
                                                // 第一行作为表头
                                                renderTableHeader(
                                                    HtmlNode.Element(
                                                        "thead",
                                                        emptyMap(),
                                                        listOf(childNode)
                                                    )
                                                )
                                                hasHeader = true
                                            } else {
                                                // 其他行作为表体
                                                renderTableRow(childNode)
                                            }
                                        }
                                    }
                                }
                            }

                            // 如果没有找到任何表头或表体，尝试处理其他元素
                            if (!hasHeader && !hasBody) {
                                htmlNode.children.forEach { childNode ->
                                    if (childNode is HtmlNode.Element && childNode.tagName != "thead" && childNode.tagName != "tbody") {
                                        renderTableBody(
                                            HtmlNode.Element(
                                                "tbody",
                                                emptyMap(),
                                                listOf(childNode)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "list" -> {
                        // 将 <list> 标签当作 <ul> 标签处理
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
            BasicText(
                text = combinedText,
                style = MaterialTheme.typography.body1,
                inlineContent = LocalInlineContent.current
            )
        } else {
            // 块级元素，直接渲染
            HtmlBlockRenderer(group[0])
        }
    }
}


// 添加一个函数来渲染内联元素
@OptIn(ExperimentalUuidApi::class)
@Composable
private fun renderInlineElement(htmlNode: HtmlNode.Element): AnnotatedString {
    return buildAnnotatedString {
        when (htmlNode.tagName) {

            "img" -> {

                val src = htmlNode.attributes["src"] ?: ""
                val alt = htmlNode.attributes["alt"] ?: ""
                val width = htmlNode.attributes["width"]?.toIntOrNull()?.dp
                val height = htmlNode.attributes["height"]?.toIntOrNull()?.dp
                val baseUrl = LocalImageState.current.baseUrl

                val localInlineContent = LocalInlineContent.current
                val key = Uuid.random().toHexString()
                localInlineContent[key] = InlineTextContent(
                    placeholder = Placeholder(
                        width = 1.5.em,
                        height = 1.5.em,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    ),
                    children = {
                        SubcomposeAsyncImage(
                            model = "$baseUrl$src",
                            contentDescription = alt,
                            modifier = Modifier
                                .widthIn(min = 0.dp, max = width ?: Dp.Infinity)
                                .heightIn(min = 0.dp, max = height ?: Dp.Infinity),
                            error = {
                                Text(
                                    text = alt,
                                    color = Color.Red,
                                    modifier = Modifier
                                )
                            }
                        )
                    }
                )
                appendInlineContent(key, alt)
            }

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
                        background = Color(243, 243, 243),
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
                pushLink(
                    LinkAnnotation.Url(
                        href,
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = Color(48, 127, 255)
                            ),
                            hoveredStyle = SpanStyle(
                                color = Color(48, 127, 255),
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    )
                )
                htmlNode.children.forEach { child ->
                    if (child is HtmlNode.Text) {
                        append(child.content)
                    } else if (child is HtmlNode.Element && isInlineElement(child.tagName)) {
                        append(renderInlineElement(child))
                    }
                }
                if (href.isUrl()) {
                    append("\u2197")
                }
                pop()
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

// 渲染表头
@Composable
private fun renderTableHeader(theadNode: HtmlNode.Element) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(244, 244, 244))
    ) {
        // 遍历表头的行
        theadNode.children.forEach { trNode ->
            if (trNode is HtmlNode.Element && trNode.tagName == "tr") {
                renderTableRow(trNode, isHeader = true)
            }
        }
    }
}

// 渲染表体
@Composable
private fun renderTableBody(tbodyNode: HtmlNode.Element) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        tbodyNode.children.forEach { trNode ->
            if (trNode is HtmlNode.Element && trNode.tagName == "tr") {
                renderTableRow(trNode)
            }
        }
    }
}

// 渲染表格行
@Composable
private fun renderTableRow(trNode: HtmlNode.Element, isHeader: Boolean = false) {
    // 使用SubcomposeLayout来确保所有单元格高度一致
    SubcomposeLayout(
        modifier = Modifier.fillMaxWidth()
            .border(0.5.dp, Color.LightGray)
    ) { constraints ->
        // 过滤出所有单元格节点
        val cellNodes = trNode.children.filterIsInstance<HtmlNode.Element>()
            .filter { it.tagName == "td" || it.tagName == "th" }

        // 计算每个单元格的权重
        val weights = cellNodes.map {
            val colspan = it.attributes["colspan"]?.toIntOrNull() ?: 1
            colspan.toFloat()
        }
        val totalWeight = weights.sum()

        // 第一次测量：获取所有单元格的高度
        val placeables = cellNodes.mapIndexed { index, cellNode ->
            val cellWeight = weights[index]
            val cellWidth = ((constraints.maxWidth * cellWeight) / totalWeight).toInt()

            // 测量单元格内容
            subcompose(index) {
                CellContent(
                    cellNode = cellNode,
                    isHeader = isHeader,
                    isLastCell = index == cellNodes.size - 1
                )
            }.first().measure(
                constraints.copy(
                    minWidth = cellWidth,
                    maxWidth = cellWidth,
                    minHeight = 0
                )
            )
        }

        // 找出最大高度
        val maxHeight = placeables.maxOf { it.height }

        // 第二次测量：使用统一的高度
        val finalPlaceables = cellNodes.mapIndexed { index, cellNode ->
            val cellWeight = weights[index]
            val cellWidth = ((constraints.maxWidth * cellWeight) / totalWeight).toInt()

            subcompose(index + cellNodes.size) {
                CellContent(
                    cellNode = cellNode,
                    isHeader = isHeader,
                    isLastCell = index == cellNodes.size - 1,
                    fixedHeight = maxHeight
                )
            }.first().measure(
                constraints.copy(
                    minWidth = cellWidth,
                    maxWidth = cellWidth,
                    minHeight = maxHeight,
                    maxHeight = maxHeight
                )
            )
        }

        // 放置所有单元格
        layout(constraints.maxWidth, maxHeight) {
            var xPosition = 0
            finalPlaceables.forEachIndexed { index, placeable ->
                placeable.place(xPosition, 0)
                xPosition += placeable.width
            }
        }
    }
}

// 单元格内容组件
@Composable
private fun CellContent(
    cellNode: HtmlNode.Element,
    isHeader: Boolean,
    isLastCell: Boolean,
    fixedHeight: Int? = null
) {
    Box(
        modifier = Modifier
            .then(
                if (fixedHeight != null) {
                    Modifier.height(fixedHeight.dp)
                } else {
                    Modifier
                }
            )
            .then(
                if (!isLastCell && !(isHeader || cellNode.tagName == "th")) {
                    Modifier.border(
                        0.5.dp,
                        Color(209, 209, 210),
                        RightBorderShape(0.5.dp)
                    )
                } else {
                    Modifier
                }
            )
            .then(
                if (isHeader || cellNode.tagName == "th") {
                    Modifier
                        .border(0.5.dp, Color(209, 209, 210))
                        .background(Color(244, 244, 244))
                } else {
                    Modifier
                }
            )
            .padding(12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (isHeader || cellNode.tagName == "th") {
            // 表头单元格
            Text(
                text = buildAnnotatedString {
                    cellNode.children.forEach { child ->
                        when (child) {
                            is HtmlNode.Text -> append(child.content)
                            is HtmlNode.Element -> append(renderInlineElement(child))
                        }
                    }
                },
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(6.dp)
            )
        } else {
            // 普通单元格
            Column {
                renderChildrenWithTextMerging(cellNode.children)
            }
        }
    }
}