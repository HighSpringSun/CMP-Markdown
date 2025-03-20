package com.kmpstudy.markdown.parser

import com.kmpstudy.markdown.renderer.HtmlBlockRenderer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.kmpstudy.markdown.constant.MarkdownElementTypeNames
import com.kmpstudy.markdown.localstate.LocalImageState
import com.kmpstudy.markdown.renderer.Table
import com.kmpstudy.markdown.util.findChildByName
import com.kmpstudy.markdown.util.getTableItemNumber
import com.kmpstudy.markdown.util.hasImage
import com.kmpstudy.markdown.util.isTable
import com.kmpstudy.markdown.util.isUrl
import com.kmpstudy.markdown.util.splitByImage
import com.kmpstudy.markdown.util.splitList
import com.kmpstudy.markdown.util.styleByATX
import com.kmpstudy.markdown.exception.MarkdownParseTableException
import com.kmpstudy.markdown.renderer.debugHtmlNode
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser


data class MarkdownTableState(
    val colSize: Int,
    val headers: List<AnnotatedString>,
    val rows: List<List<AnnotatedString>>
)

data class MarkdownImageState(
    val linkText: String,
    val linkDestination: String,
    val extras: List<Pair<String, String>>
)

class MarkdownParser(private val markdownContent: String) {

    private val flavour = CommonMarkFlavourDescriptor()
    private val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdownContent)

    private var index = 0


    @Composable
    fun Markdown(
        modifier: Modifier = Modifier,
        enableASTInfo: Boolean = false
    ) {
        if (enableASTInfo) {
            printAstTree(parsedTree, markdownContent)
        }
        Column(
            modifier = modifier,
        ) {
            parsedTree.children.forEach { node ->
                MarkdownNode(node)
            }
        }
    }


    @Composable
    private fun MarkdownNode(node: ASTNode) {
        if (node.type == MarkdownElementTypes.PARAGRAPH) {
            Paragraph(node)
        } else if (
            node.type == MarkdownElementTypes.ATX_1 ||
            node.type == MarkdownElementTypes.ATX_2 ||
            node.type == MarkdownElementTypes.ATX_3 ||
            node.type == MarkdownElementTypes.ATX_4 ||
            node.type == MarkdownElementTypes.ATX_5
        ) {
            Header(node)
        } else if (node.type.name == MarkdownElementTypeNames.EOL) {
            Eol(node)
        } else if (node.type == MarkdownElementTypes.UNORDERED_LIST) {
            UnorderedList(node)
        } else if (node.type == MarkdownElementTypes.ORDERED_LIST) {
            OrderedList(node)
        } else if (node.type == MarkdownElementTypes.BLOCK_QUOTE) {
            BlockQuote(node)
        } else if (node.type == MarkdownElementTypes.CODE_FENCE) {
            CodeFence(node)
        } else if (node.type == MarkdownElementTypes.LINK_DEFINITION) {
            LinkDefinition(node)
        } else if (node.type == MarkdownElementTypes.HTML_BLOCK) {
            HtmlBlock(node)
        } else if (node.type.name == MarkdownElementTypeNames.HORIZONTAL_RULE) {
            HorizontalRule(node)
        } else {
//            Else(node)
        }
    }


    // top level

    @Composable
    private fun HtmlBlock(node: ASTNode) {
        Box(
            modifier = Modifier
        ) {
            val html = node.getTextInNode(markdownContent).toString()
            val htmlNode = HtmlBlockParser().parseHtml(html)
            println(htmlNode)
            // 打印调试信息
            println("HTML Struct:\n${debugHtmlNode(htmlNode)}")
            HtmlBlockRenderer(htmlNode)
        }
    }

    @Composable
    private fun HorizontalRule(node: ASTNode) {
        require(node.type.name == MarkdownElementTypeNames.HORIZONTAL_RULE)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(221, 221, 221))

        )
    }

    @Composable
    private fun LinkDefinition(node: ASTNode) {
        val title = node.findChildOfType(MarkdownElementTypes.LINK_TITLE)
        if (title != null) {
            Text(title.getTextInNode(markdownContent).toString())
        }
    }

    @Composable
    private fun Paragraph(node: ASTNode) {
        if (node.isTable(markdownContent)) {
            Table(parseTable(node))
        } else if (node.hasImage()) {
            val nodeList = splitByImage(node.children)
            nodeList.forEach { nodes ->
                if (nodes.size == 1 && nodes.first().type == MarkdownElementTypes.IMAGE) {
                    Image(nodes.first())
                } else {
                    val annotatedString = parseText(nodes)
                    Text(
                        annotatedString,
                    )
                }
            }
        } else {
            Text(
                parseText(node)
            )
        }
    }

    @Composable
    private fun Header(node: ASTNode) {
        val annotatedString = buildAnnotatedString {
            node.findChildByName(MarkdownElementTypeNames.ATX_CONTENT)!!.children.forEach {
                if (it.type.name != MarkdownElementTypeNames.WHITE_SPACE) {
                    append(it.getTextInNode(markdownContent))
                }
            }
        }
        Text(
            text = annotatedString,
            style = node.styleByATX()
        )
    }

    @Composable
    private fun Eol(node: ASTNode) {
        require(node.type.name == MarkdownElementTypeNames.EOL)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )
    }

    @Composable
    private fun UnorderedList(node: ASTNode) {
        Column {
            node.children.forEach { itemNode ->
                if (itemNode.type == MarkdownElementTypes.LIST_ITEM) {
                    UnorderedListItem(itemNode)
                } else if (itemNode.type.name == MarkdownElementTypeNames.EOL) {
                    Eol(itemNode)
                }
            }
        }
    }

    @Composable
    private fun BlockQuote(node: ASTNode) {
//        val index = node.parent!!.children.indexOf(node) + 2
//        val modifier = if (index < parsedTree.children.size) {
//            val styleNode = parsedTree.children[index]
//            styleNode.checkNext(markdownContent)
//        } else {
//            Modifier
//        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(225, 241, 225), RoundedCornerShape(6.dp))
                .padding(24.dp)
        ) {
            Icon(
                Icons.Filled.Info,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 24.dp),
                tint = Color(108, 185, 105)
            )
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                node.children.forEach { childNode ->
                    MarkdownNode(childNode)
                }
            }
        }
    }

    @Composable
    private fun Else(node: ASTNode) {
        Text(node.type.toString())
    }

    private fun parseTable(node: ASTNode): MarkdownTableState {
        val tableList = splitList(node.children) { it.type.name == MarkdownElementTypeNames.EOL }
        if (tableList.size < 3) {
            throw MarkdownParseTableException("size less than 3")
        }
        val headers =
            splitList(tableList[0]) { it.getTextInNode(markdownContent) == MarkdownElementTypeNames.V_LINE }
                .map { textNode ->
                    buildAnnotatedString {
                        append(textNode.joinToString(separator = "") {
                            it.getTextInNode(
                                markdownContent
                            )
                        })
                    }
                }
        val colSize = tableList[1].getTableItemNumber(markdownContent)
        val rows = mutableListOf<List<AnnotatedString>>()
        for (i in tableList.indices) {
            if (i == 0 || i == 1) {
                continue
            } else {
                val astNodes = tableList[i]
                val result = splitList(astNodes) {
                    it.getTextInNode(markdownContent) == MarkdownElementTypeNames.V_LINE
                }.map { textNode ->
                    buildAnnotatedString {
                        append(
                            textNode.joinToString(separator = "") {
                                it.getTextInNode(
                                    markdownContent
                                )
                            }
                        )
                    }
                }
                rows.add(result)
            }
        }
        return MarkdownTableState(
            colSize, headers, rows
        )
    }

    private fun parseImage(imgNode: ASTNode): MarkdownImageState {
        val inlineLink =
            imgNode.findChildOfType(MarkdownElementTypes.INLINE_LINK)!!
        val linkText =
            inlineLink.findChildOfType(MarkdownElementTypes.LINK_TEXT)!!
                .findChildByName(MarkdownElementTypeNames.TEXT)!!
                .getTextInNode(markdownContent)
        val linkDestination =
            inlineLink.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)!!
                .getTextInNode(markdownContent).toString()
//        val extraAttrs =
//            imgNode.findChildByName(MarkdownElementTypeNames.TEXT)?.getTextInNode(markdownContent)

//        @Suppress("UNUSED")
//        val widthModifier = if (extraAttrs != null) {
//            val width =
//                extraAttrs.toString().substringAfter('=').trimEnd { it == '}' }
//                    .toInt()
//            Modifier.width(width.dp)
//        } else {
//            Modifier
//        }
        return MarkdownImageState(
            linkText.toString(),
            linkDestination,
            listOf()
        )
    }

    @Composable
    private fun Image(node: ASTNode) {
        val imageState = parseImage(node)
        val baseUrl = LocalImageState.current.baseUrl
        val modifier = Modifier

        SubcomposeAsyncImage(
            model = "$baseUrl${imageState.linkDestination}",
            contentDescription = imageState.linkText,
            modifier = modifier
                .padding(bottom = 24.dp),
            error = {
                Text(
                    text = imageState.linkText,
                    color = Color.Red,
                    modifier = modifier
                        .padding(bottom = 24.dp)
                )
            }
        )
    }

    private fun parseText(nodes: List<ASTNode>): AnnotatedString {
        val annotatedString = buildAnnotatedString {
            var i = 0
            while (i < nodes.size) {
                val parNode = nodes[i]

                when (parNode.type) {

                    MarkdownElementTypes.STRONG -> {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(parseText(parNode.children.filter { it.type.name != MarkdownElementTypes.EMPH.name }))
                        }
                    }

                    MarkdownElementTypes.IMAGE -> {
//                        throw UnsupportedOperationException("IMAGE")
                    }

                    MarkdownElementTypes.BLOCK_QUOTE -> {
                        // ignore block_quote  `>`
                    }

                    MarkdownElementTypes.INLINE_LINK -> {
//                        val linkText =
//                            parNode.findChildOfType(MarkdownElementTypes.LINK_TEXT)!!
//                                .getTextInNode(markdownContent)
//                                .trim { it == '[' || it == ']' }
                        val linkTextNode = parNode.findChildOfType(MarkdownElementTypes.LINK_TEXT)!!
                        val linkDestination =
                            parNode.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)!!
                                .getTextInNode(markdownContent).toString()
                        pushLink(
                            LinkAnnotation.Url(
                                linkDestination,
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
                        append(parseText(linkTextNode.children.filter { it.type.name != "[" && it.type.name != "]" }))
                        if (linkDestination.isUrl()) {
                            append("\u2197")
                        }
                        pop()
                    }

                    MarkdownElementTypes.CODE_SPAN -> {
                        withStyle(
                            style = SpanStyle(
                                background = Color(243, 243, 243),
                            )
                        ) {
                            append(parseText(parNode.children.filter { it.type.name != MarkdownElementTypeNames.BACKTICK }))
                        }
                    }

                    MarkdownElementTypes.CODE_FENCE -> {
                        withStyle(
                            style = SpanStyle(
                                background = Color(243, 243, 243)
                            )
                        ) {
                            val lang =
                                parNode.findChildByName(MarkdownElementTypeNames.FENCE_LANG)!!
                                    .getTextInNode(markdownContent)
                            val content =
                                parNode.findChildByName(MarkdownElementTypeNames.CODE_FENCE_CONTENT)!!
                                    .getTextInNode(markdownContent)
                            append(lang)
                            append("\n")
                            append(content)
                        }
                    }

                    else -> {
                        val content = parNode.getTextInNode(markdownContent)
                        if (parNode.type.name == MarkdownElementTypeNames.EOL &&
                            i + 1 < nodes.size &&
                            nodes[i + 1].type.name == MarkdownElementTypeNames.EOL
                        ) {
                            append("\n\n")
                        } else if (parNode.type.name == MarkdownElementTypeNames.WHITE_SPACE && content.length > 1) {
//                            append(" ")
                        } else {
                            append(content.replace(Regex("\\R"), " "))
                        }
                    }
                }
                i++
            }
        }
        return annotatedString
    }

    private fun parseText(node: ASTNode): AnnotatedString {
        return parseText(node.children)
    }

    @Composable
    private fun OrderedListItem(itemNode: ASTNode) {
        val index =
            itemNode.children.find { it.type.name == MarkdownElementTypeNames.LIST_NUMBER }!!
                .getTextInNode(markdownContent)
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(index.toString())
            Column {
                itemNode.children.forEach { node ->
                    if (node.type.name != MarkdownElementTypeNames.LIST_NUMBER) {
                        MarkdownNode(node)
                    }
                }
            }
        }
    }

    @Composable
    private fun OrderedList(node: ASTNode) {
        Column {
            node.children.forEach { itemNode ->
                if (itemNode.type == MarkdownElementTypes.LIST_ITEM) {
                    OrderedListItem(itemNode)
                } else if (itemNode.type.name == MarkdownElementTypeNames.EOL) {
                    Eol(itemNode)
                }
            }
        }
    }

    @Composable
    private fun UnorderedListItem(itemNode: ASTNode) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("\u2022")
            Column {
                itemNode.children.forEach { node ->
                    MarkdownNode(node)
                }
            }
        }
    }

    private fun parseListItem(itemNode: ASTNode, level: Int = 0): AnnotatedString {
        return buildAnnotatedString {
            itemNode.children.forEach { node ->
                when (node.type) {
                    MarkdownElementTypes.PARAGRAPH -> {
                        append(
                            buildAnnotatedString {
                                withStyle(
                                    style = ParagraphStyle(
                                        textIndent = TextIndent(
                                            firstLine = 0.sp,
                                            restLine = 20.sp
                                        )
                                    )
                                ) {
                                    append("   ".repeat(level))
                                    append("\u2022  ")
                                    append(parseText(node))
                                }
                            }
                        )
                    }

                    MarkdownElementTypes.UNORDERED_LIST -> {
                        parseUnorderedList(node, level + 1).forEach {
                            append(it)
                        }
                    }

                    MarkdownElementTypes.ORDERED_LIST -> {
                        parseOrderedList(node, level + 1).forEach {
                            append(it)
                        }
                    }

//                    else -> {
//                        append(
//                            node.getTextInNode(markdownContent)
//                                .replace(Regex("\n"), "")
//                        )
//                    }
                }
            }
        }
    }

    private fun parseUnorderedList(
        node: ASTNode,
        level: Int = 0,
    ): List<AnnotatedString> {
        val result = mutableListOf<AnnotatedString>()
        node.children.forEach { itemNode ->
            if (itemNode.type == MarkdownElementTypes.LIST_ITEM) {
                result.add(parseListItem(itemNode, level))
            } else if (itemNode.type.name == MarkdownElementTypeNames.EOL) {
                result.add(AnnotatedString(""))
            } else {
//              append(itemNode.getTextInNode(markdownContent))
            }
        }
        return result
    }


    private fun parseOrderedListItem(
        itemNode: ASTNode,
        level: Int = 0,
    ): AnnotatedString {
        return buildAnnotatedString {
            var index: CharSequence = ""
            var consumed = false
            itemNode.children.forEach { node ->
                if (node.type.name == MarkdownElementTypeNames.LIST_NUMBER) {
                    index = node.getTextInNode(markdownContent)
                } else if (node.type == MarkdownElementTypes.PARAGRAPH) {
                    append(
                        buildAnnotatedString {
                            withStyle(
                                style = ParagraphStyle(
                                    textIndent = TextIndent(firstLine = 0.sp, restLine = 20.sp)
                                )
                            ) {
                                append("   ".repeat(level))
                                if (!consumed) {
                                    append("$index ")
                                    consumed = true
                                } else {
                                    append("    ")
                                }
                                append(parseText(node))
                            }
                        }
                    )
                } else if (node.type == MarkdownElementTypes.UNORDERED_LIST) {
                    parseUnorderedList(node, level + 1).forEach {
                        append(it)
                    }
                } else if (node.type == MarkdownElementTypes.ORDERED_LIST) {
                    parseOrderedList(node, level + 1).forEach {
                        append(it)
                    }
                }
            }
        }
    }

    private fun parseOrderedList(
        node: ASTNode,
        level: Int = 0
    ): List<AnnotatedString> {
        val result = mutableListOf<AnnotatedString>()
        node.children.forEach { itemNode ->
            if (itemNode.type == MarkdownElementTypes.LIST_ITEM) {
                result.add(parseOrderedListItem(itemNode, level))
            } else if (itemNode.type.name == MarkdownElementTypeNames.EOL) {
                result.add(AnnotatedString(""))
            }
        }
        return result
    }


    @Composable
    private fun CodeFence(node: ASTNode) {
        val lang =
            node.findChildByName(MarkdownElementTypeNames.FENCE_LANG)!!
                .getTextInNode(markdownContent)
        val contentList =
            node.children.filter { it.type.name == MarkdownElementTypeNames.CODE_FENCE_CONTENT }
                .map { it.getTextInNode(markdownContent) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(25, 25, 28, 13), RoundedCornerShape(4.dp))
                .padding(12.dp, 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Icon(
//                    Icons.Default.ArrowDropDown,
//                    contentDescription = "expand"
//                )
//                Text(lang.toString())
//            }
            contentList.forEach { content ->
                Text(content.toString())
            }
        }
    }

    private fun parseSTRONG(node: ASTNode): AnnotatedString = buildAnnotatedString {
        require(node.type == MarkdownElementTypes.STRONG)
        node.children.forEach { childNode ->
            // todo  一个非常奇怪的问题，不能使用type比较因为一直不相等，所以只能用name先比较
            if (childNode.type.name != MarkdownElementTypes.EMPH.name) {
                append(childNode.getTextInNode(markdownContent))
            }
        }
    }

    private fun printAstTree(node: ASTNode, markdownText: String, indent: String = "") {
        val nodeInfo = buildString {
            append("${index++} -> ")
            append("${indent}├─ Type: ${node.type}")
            append(" | Range: [${node.startOffset}..${node.endOffset}]")

            val nodeText = node.getTextInNode(markdownText).takeIf { it.isNotEmpty() }
            nodeText?.let {
                append(" | Text: \"${it.replace(Regex("\\R"), "\\n")}")
            }
        }

        println(nodeInfo)

        val lastChildIndex = node.children.lastIndex
        node.children.forEachIndexed { index, child ->
            val isLast = index == lastChildIndex
            val newIndent = indent + if (isLast) "    " else "│   "
            val childPrefix = if (isLast) "└─ " else "├─ "

            printAstTree(child, markdownText, "$newIndent$childPrefix")
        }
    }
}