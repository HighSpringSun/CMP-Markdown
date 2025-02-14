package com.mywf.markdown.parser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
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
import com.mywf.VerticalScrollbar
import com.mywf.markdown.renderer.Table
import com.mywf.markdown.constant.MarkdownElementTypeNames
import com.mywf.model.markdown.exception.MarkdownParseTableException
import com.mywf.markdown.util.findChildByName
import com.mywf.markdown.util.getTableItemNumber
import com.mywf.markdown.util.hasImage
import com.mywf.markdown.util.isTable
import com.mywf.markdown.util.splitByImage
import com.mywf.markdown.util.splitList
import com.mywf.markdown.util.styleByATX
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

    //    private val logger = KotlinLogging.logger("markdown logger")
    private var index = 0


    fun parse(): @Composable () -> Unit {
        printAstTree(parsedTree, markdownContent)
        return {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                val state = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .verticalScroll(state),
                ) {
                    parsedTree.children.forEach { node ->
                        if (node.type == MarkdownElementTypes.PARAGRAPH) {
                            parsePARAGRAPH(node).invoke()
                        } else if (
                            node.type == MarkdownElementTypes.ATX_1 ||
                            node.type == MarkdownElementTypes.ATX_2 ||
                            node.type == MarkdownElementTypes.ATX_3 ||
                            node.type == MarkdownElementTypes.ATX_4 ||
                            node.type == MarkdownElementTypes.ATX_5
                        ) {
                            parseATX(node).invoke()
                        } else if (node.type.name == MarkdownElementTypeNames.EOL) {
                            parseEOL(node).invoke()
                        } else if (node.type == MarkdownElementTypes.UNORDERED_LIST) {
                            parseUNORDEREDLIST(node).invoke()
                        } else {
                            parseElse(node).invoke()
                        }
                    }
                }
                VerticalScrollbar(
                    state = state,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ).invoke()
            }
        }
    }

//    fun parseText(nodes: List<ASTNode>): @Composable () -> Unit {
//
//    }

    fun parsePARAGRAPH(node: ASTNode): @Composable () -> Unit = {
        if (node.isTable(markdownContent)) {
            Table(parseTable(node))
        } else if (node.hasImage()) {
            val nodeList = splitByImage(node.children)
            nodeList.forEach { nodes ->
                if (nodes.size == 1 && nodes.first().type == MarkdownElementTypes.IMAGE) {
                    val imgNode = nodes.first()
                    val imageState = parseImage(imgNode)
                    val baseUrl =
                        "https://resources.jetbrains.com/help/img/kotlin-multiplatform-dev/"
//                    println("$baseUrl${imageState.linkDestination}")
                    SubcomposeAsyncImage(
                        model = "$baseUrl${imageState.linkDestination}",
                        contentDescription = imageState.linkText,
                        modifier = Modifier
                            .padding(bottom = 24.dp),
                        error = {
                            Text(
                                text = imageState.linkText,
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(bottom = 24.dp)
                            )
                        }
                    )
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

    fun parseATX(node: ASTNode): @Composable () -> Unit {
        return {
            val annotatedString = buildAnnotatedString {
                node.findChildByName("ATX_CONTENT")!!.children.forEach {
                    if (it.type.name != "WHITE_SPACE") {
                        append(it.getTextInNode(markdownContent))
                    }
                }
            }
            Text(
                text = annotatedString,
                style = node.styleByATX()
            )
        }
    }

    fun parseEOL(node: ASTNode): @Composable () -> Unit {
        return {
            Box(
                modifier = Modifier
//                    .border(1.dp, Color.Black)
                    .fillMaxWidth()
                    .height(16.dp)
//                    .background(Color.LightGray)
            ) {
//                Text(node.type.toString())
            }
        }
    }

    fun parseUNORDEREDLIST(node: ASTNode): @Composable () -> Unit = {
        val list = parseUnorderedList(node)
        Column {
            list.forEach {
                Text(it)
            }
        }

    }

    fun parseElse(node: ASTNode): @Composable () -> Unit {
        return {
            Text(node.type.toString())
        }
    }


    fun parseTable(node: ASTNode): MarkdownTableState {
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

    fun parseImage(imgNode: ASTNode): MarkdownImageState {
        val inlineLink =
            imgNode.findChildOfType(MarkdownElementTypes.INLINE_LINK)!!
        val linkText =
            inlineLink.findChildOfType(MarkdownElementTypes.LINK_TEXT)!!
                .findChildByName(MarkdownElementTypeNames.TEXT)!!
                .getTextInNode(markdownContent)
        val linkDestination =
            inlineLink.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)!!
                .getTextInNode(markdownContent).toString()

        val extraAttrs =
            imgNode.findChildByName(MarkdownElementTypeNames.TEXT)?.getTextInNode(markdownContent)
        val widthModifier = if (extraAttrs != null) {
            val width =
                extraAttrs.toString().substringAfter('=').trimEnd { it == '}' }
                    .toInt()
            Modifier.width(width.dp)
        } else {
            Modifier
        }
        return MarkdownImageState(
            linkText.toString(),
            linkDestination,
            listOf()
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
                            append(parseSTRONG(parNode))
                        }
                    }

                    MarkdownElementTypes.IMAGE -> {
                        throw UnsupportedOperationException("IMAGE")
                    }

                    MarkdownElementTypes.INLINE_LINK -> {
                        val linkText =
                            parNode.findChildOfType(MarkdownElementTypes.LINK_TEXT)!!
                                .findChildByName(MarkdownElementTypeNames.TEXT)!!
                                .getTextInNode(markdownContent)
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
                        append(linkText)
                        append("\u2197")
                        pop()
                    }

                    MarkdownElementTypes.CODE_SPAN -> {
                        val code = parNode.findChildByName(MarkdownElementTypeNames.TEXT)!!
                            .getTextInNode(markdownContent)
                        withStyle(
                            style = SpanStyle(
                                background = Color(243, 243, 243)
                            )
                        ) {
                            append(code)
                        }
                    }

                    else -> {
                        val content = parNode.getTextInNode(markdownContent)
                        if ((i == 0 || i == 1) && (parNode.type.name == "EOL" || parNode.type.name == "WHITE_SPACE")) {
//                            continue
                        } else if (parNode.type.name == MarkdownElementTypeNames.EOL &&
                            i + 1 < nodes.size &&
                            nodes[i + 1].type.name == MarkdownElementTypeNames.EOL
                        ) {
                            append("\n\n")
                        } else {
                            append(content.replace(Regex("\\R"), ""))
                        }
//                        else if (parNode.type.name == MarkdownElementTypeNames.WHITE_SPACE &&
//                            content.first() == '\r' &&
//                            i + 1 < nodes.size &&
//                            nodes[i + 1].type.name == MarkdownElementTypeNames.EOL
//                        ) {
////                            continue
//                        }
                    }
                }
                i++
            }
        }
        return annotatedString
    }

    @Deprecated("not use yet")
    fun parseInlineLink(parNode: ASTNode): @Composable () -> Unit = {
        val annotatedString = buildAnnotatedString {
            val linkText =
                parNode.findChildOfType(MarkdownElementTypes.LINK_TEXT)!!
                    .findChildByName(MarkdownElementTypeNames.TEXT)!!
                    .getTextInNode(markdownContent)
            val linkDestination =
                parNode.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)!!
                    .getTextInNode(markdownContent).toString()
            pushLink(
                LinkAnnotation.Url(
                    linkDestination,
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue
                        ),
                        hoveredStyle = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                )
            )
            append(linkText)
            pop()
        }
        Row {
            Text(annotatedString)
            Text("\u2197")
        }
    }

    fun parseText(node: ASTNode): AnnotatedString {
        return parseText(node.children)
    }


    fun parseBlockQuote(node: ASTNode): List<AnnotatedString> {
        val result = mutableListOf<AnnotatedString>()
        node.children.forEach { itemNode ->
            when (itemNode.type) {
                MarkdownElementTypes.PARAGRAPH -> {
                    result.add(parseText(itemNode))
                }

                MarkdownElementTypes.UNORDERED_LIST -> {
                    result.addAll(parseUnorderedList(itemNode))
                }
            }
        }
        return result
    }

    private fun parseListItem(itemNode: ASTNode): AnnotatedString {
        return buildAnnotatedString {
            itemNode.children.forEach { node ->
                when (node.type) {
                    MarkdownElementTypes.PARAGRAPH -> {
                        append(
                            buildAnnotatedString {
                                withStyle(
                                    style = ParagraphStyle(
                                        textIndent = TextIndent(firstLine = 0.sp, restLine = 20.sp)
                                    )
                                ) {
                                    append(AnnotatedString("\u2022  ${parseText(node)}"))
                                }
                            }
                        )
                    }

                    MarkdownElementTypes.UNORDERED_LIST -> {
                        parseUnorderedList(node).forEach {
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

    private fun parseUnorderedList(node: ASTNode): List<AnnotatedString> {
        val result = mutableListOf<AnnotatedString>()
        println(node.children.map { it.type }.toList())
        node.children.forEach { itemNode ->
            if (itemNode.type == MarkdownElementTypes.LIST_ITEM) {
                result.add(parseListItem(itemNode))
            } else if (itemNode.type.name == MarkdownElementTypeNames.EOL) {
                result.add(AnnotatedString(""))
//                    append(itemNode.getTextInNode(markdownContent))
            } else {
//                    append(itemNode.getTextInNode(markdownContent))
//                println("unorderedList :${itemNode.type}")
            }
        }
        return result
    }

    private fun parseSTRONG(node: ASTNode): AnnotatedString = buildAnnotatedString {
        require(node.type == MarkdownElementTypes.STRONG)
//        println(node.children.map { it.type }.toList())
        node.children.forEach { childNode ->
            // todo  一个非常奇怪的问题，不能使用type比较因为一直不等于
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

//        if (index < 1000) {
//            println(nodeInfo)
////            logger.debug { nodeInfo }
//        }
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