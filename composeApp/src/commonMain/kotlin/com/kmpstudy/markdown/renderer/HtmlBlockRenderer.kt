package com.kmpstudy.markdown.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.kmpstudy.markdown.localstate.LocalImageState
import com.kmpstudy.markdown.parser.HtmlNode


@Composable
fun HtmlBlockRenderer(htmlNode: HtmlNode) {
    Column(
        modifier = Modifier
    ) {
        when (htmlNode) {
            is HtmlNode.Element -> {
                when (htmlNode.tagName) {
                    "tldr" -> {
                        Box(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    "TL;DR",
                                    style = MaterialTheme.typography.subtitle1.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                htmlNode.children.forEach { childNode ->
                                    HtmlBlockRenderer(childNode)
                                }
                            }
                        }
                    }

                    "p" -> {
                        Box(
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Column {
                                htmlNode.children.forEach { childNode ->
                                    HtmlBlockRenderer(childNode)
                                }
                            }
                        }
                    }

                    "img" -> {
                        val localImgState = LocalImageState.current
                        val src = "${localImgState.baseUrl}${htmlNode.attributes["src"]}"
                        val width = htmlNode.attributes["width"]?.toInt()
                        val alt = htmlNode.attributes["alt"]
                        val modifier = if (width != null) {
                            Modifier.width(width.dp)
                        } else {
                            Modifier
                        }
                        AsyncImage(
                            src,
                            contentDescription = alt,
                            modifier = modifier
                        )
                    }

                    "a" -> {
                        val href = htmlNode.attributes["href"] ?: ""
                        val uriHandler = LocalUriHandler.current
                        val annotatedString = buildAnnotatedString {
                            htmlNode.children.forEach { child ->
                                if (child is HtmlNode.Text) {
                                    pushStringAnnotation(tag = "URL", annotation = href)
                                    append(child.content)
                                    pop()
                                }
                            }
                        }
                        
                        ClickableText(
                            text = annotatedString,
                            style = TextStyle(
                                color = MaterialTheme.colors.primary,
                                textDecoration = TextDecoration.Underline
                            ),
                            onClick = { offset ->
                                annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        uriHandler.openUri(annotation.item)
                                    }
                            }
                        )
                    }

                    "strong", "b" -> {
                        Text(
                            text = buildAnnotatedString {
                                htmlNode.children.forEach { child ->
                                    if (child is HtmlNode.Text) {
                                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                                        append(child.content)
                                        pop()
                                    }
                                }
                            }
                        )
                    }
                    
                    "em", "i" -> {
                        Text(
                            text = buildAnnotatedString {
                                htmlNode.children.forEach { child ->
                                    if (child is HtmlNode.Text) {
                                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                                        append(child.content)
                                        pop()
                                    }
                                }
                            }
                        )
                    }
                    
                    "code" -> {
                        Text(
                            text = buildAnnotatedString {
                                htmlNode.children.forEach { child ->
                                    if (child is HtmlNode.Text) {
                                        pushStyle(SpanStyle(
                                            fontFamily = MaterialTheme.typography.body2.fontFamily,
                                            background = MaterialTheme.colors.surface
                                        ))
                                        append(child.content)
                                        pop()
                                    }
                                }
                            },
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                    
                    // 新增标签处理
                    "h1", "h2", "h3", "h4", "h5", "h6" -> {
                        val level = htmlNode.tagName.substring(1).toInt()
                        val fontSize = when(level) {
                            1 -> 24.sp
                            2 -> 22.sp
                            3 -> 20.sp
                            4 -> 18.sp
                            5 -> 16.sp
                            else -> 14.sp
                        }
                        
                        Text(
                            text = buildAnnotatedString {
                                htmlNode.children.forEach { child ->
                                    if (child is HtmlNode.Text) {
                                        append(child.content)
                                    }
                                }
                            },
                            style = TextStyle(
                                fontSize = fontSize,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    "br" -> {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    "hr" -> {
                        Divider(
                            color = Color.Gray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    "blockquote" -> {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
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
                                htmlNode.children.forEach { childNode ->
                                    HtmlBlockRenderer(childNode)
                                }
                            }
                        }
                    }
                    
                    "pre" -> {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .background(
                                    color = MaterialTheme.colors.surface,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Column {
                                htmlNode.children.forEach { childNode ->
                                    HtmlBlockRenderer(childNode)
                                }
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
                                            childNode.children.forEach { liChild ->
                                                HtmlBlockRenderer(liChild)
                                            }
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
                                            childNode.children.forEach { liChild ->
                                                HtmlBlockRenderer(liChild)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    "table" -> {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .border(1.dp, Color.LightGray)
                                .padding(4.dp)
                        ) {
                            htmlNode.children.forEach { child ->
                                if (child is HtmlNode.Element) {
                                    when (child.tagName) {
                                        "thead" -> {
                                            child.children.forEach { theadChild ->
                                                if (theadChild is HtmlNode.Element && theadChild.tagName == "tr") {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color.LightGray.copy(alpha = 0.3f))
                                                            .padding(4.dp)
                                                    ) {
                                                        theadChild.children.forEach { thChild ->
                                                            if (thChild is HtmlNode.Element && (thChild.tagName == "th" || thChild.tagName == "td")) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .padding(4.dp)
                                                                ) {
                                                                    Text(
                                                                        text = buildAnnotatedString {
                                                                            thChild.children.forEach { textChild ->
                                                                                if (textChild is HtmlNode.Text) {
                                                                                    append(textChild.content)
                                                                                }
                                                                            }
                                                                        },
                                                                        fontWeight = FontWeight.Bold,
                                                                        textAlign = TextAlign.Center
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        "tbody" -> {
                                            child.children.forEach { tbodyChild ->
                                                if (tbodyChild is HtmlNode.Element && tbodyChild.tagName == "tr") {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(4.dp)
                                                    ) {
                                                        tbodyChild.children.forEach { tdChild ->
                                                            if (tdChild is HtmlNode.Element && tdChild.tagName == "td") {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .padding(4.dp)
                                                                ) {
                                                                    Column {
                                                                        tdChild.children.forEach { textChild ->
                                                                            HtmlBlockRenderer(textChild)
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    "span" -> {
                        val style = SpanStyle()
//                        val color = htmlNode.attributes["color"]?.let { Color(android.graphics.Color.parseColor(it)) }
                        val textDecoration = if (htmlNode.attributes.containsKey("text-decoration")) {
                            TextDecoration.Underline
                        } else null
                        
                        Text(
                            text = buildAnnotatedString {
                                htmlNode.children.forEach { child ->
                                    if (child is HtmlNode.Text) {
                                        pushStyle(style.copy(
//                                            color = color,
                                            textDecoration = textDecoration
                                        ))
                                        append(child.content)
                                        pop()
                                    }
                                }
                            }
                        )
                    }
                    
                    "mark" -> {
                        Text(
                            text = buildAnnotatedString {
                                htmlNode.children.forEach { child ->
                                    if (child is HtmlNode.Text) {
                                        pushStyle(SpanStyle(background = Color.Yellow.copy(alpha = 0.5f)))
                                        append(child.content)
                                        pop()
                                    }
                                }
                            }
                        )
                    }
                    
                    "del", "s" -> {
                        Text(
                            text = buildAnnotatedString {
                                htmlNode.children.forEach { child ->
                                    if (child is HtmlNode.Text) {
                                        pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                                        append(child.content)
                                        pop()
                                    }
                                }
                            }
                        )
                    }
                    
                    "u" -> {
                        Text(
                            text = buildAnnotatedString {
                                htmlNode.children.forEach { child ->
                                    if (child is HtmlNode.Text) {
                                        pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                                        append(child.content)
                                        pop()
                                    }
                                }
                            }
                        )
                    }
                    
                    "sub" -> {
                        Text(
                            text = buildAnnotatedString {
                                htmlNode.children.forEach { child ->
                                    if (child is HtmlNode.Text) {
                                        pushStyle(SpanStyle(
                                            baselineShift = BaselineShift.Subscript,
                                            fontSize = 12.sp
                                        ))
                                        append(child.content)
                                        pop()
                                    }
                                }
                            }
                        )
                    }
                    
                    "sup" -> {
                        Text(
                            text = buildAnnotatedString {
                                htmlNode.children.forEach { child ->
                                    if (child is HtmlNode.Text) {
                                        pushStyle(SpanStyle(
                                            baselineShift = BaselineShift.Superscript,
                                            fontSize = 12.sp
                                        ))
                                        append(child.content)
                                        pop()
                                    }
                                }
                            }
                        )
                    }
                    
                    else -> {
                        Column(
                            modifier = Modifier
                        ) {
                            htmlNode.children.forEach { childNode ->
                                HtmlBlockRenderer(childNode)
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


// 添加一个辅助函数来判断元素是否为内联元素
private fun isInlineElement(tagName: String): Boolean {
    return tagName in setOf(
        "a", "strong", "b", "em", "i", "code", "mark", "del", "s", "u", "sub", "sup", "span"
    )
}

// 添加一个函数来渲染内联元素
@Composable
private fun renderInlineElement(htmlNode: HtmlNode.Element): AnnotatedString {
    return buildAnnotatedString {
        when (htmlNode.tagName) {
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
            
            // ... 其他内联元素的处理 ...
            
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

// 修改渲染子节点的函数
@Composable
private fun renderChildrenWithTextMerging(children: List<HtmlNode>) {
    // 将子节点分为内联组和块级组
    val groups = mutableListOf<Pair<Boolean, List<HtmlNode>>>() // Boolean表示是否为内联组
    var currentInlineGroup = mutableListOf<HtmlNode>()
    
    for (child in children) {
        when (child) {
            is HtmlNode.Text -> {
                currentInlineGroup.add(child)
            }
            is HtmlNode.Element -> {
                if (isInlineElement(child.tagName)) {
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