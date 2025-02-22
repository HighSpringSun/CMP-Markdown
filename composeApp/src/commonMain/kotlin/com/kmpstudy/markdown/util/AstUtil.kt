package com.kmpstudy.markdown.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.mywf.ui.page.documentspage.logger
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

fun ASTNode.findChildByName(name: String): ASTNode? {
    return children.firstOrNull { it.type.name == name }
}


fun List<ASTNode>.getTableItemNumber(markdownText: String): Int {
    if (size != 1 || first().type.name != "TEXT") {
        throw Exception("getTableItemNumber failed")
    }
    val content = first().getTextInNode(markdownText)
    if (content.any { it != '|' && it != '-' }) {
        throw Exception("not any '|' or '-' , content:$content")
    }
    val regex = Regex("\\|-+")
    val matches = regex.findAll(content)
    return matches.count()
}

fun <T> splitList(list: List<T>, delimiter: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<List<T>>()
    var current = mutableListOf<T>()
    for (item in list) {
        if (current.isEmpty() && delimiter(item)) {
            continue
        }
        if (delimiter(item)) {
            result.add(current)
            current = mutableListOf()
        } else {
            current.add(item)
        }
    }
    if (current.isNotEmpty()) {
        result.add(current)
    }
    return result
}


fun ASTNode.hasImage(): Boolean {
    return children.find { it.type == MarkdownElementTypes.IMAGE } != null
}

fun ASTNode.isTable(markdownText: String): Boolean {
    if (type != MarkdownElementTypes.PARAGRAPH ||
        children.isEmpty() ||
        children[0].getTextInNode(markdownText) != "|"
    ) {
        return false
    }
    var step = 0
    children.forEachIndexed { index, astNode ->
        if (index == 0 && astNode.type.name == "TEXT" && astNode.getTextInNode(markdownText) == "|") {
            step++
        } else if (index == 1 &&
            astNode.type.name == "WHITE_SPACE" &&
            astNode.getTextInNode(markdownText) == " "
        ) {
            step++
        } else if (astNode.type.name == "EOL") {
            if (index + 1 < children.size &&
                children[index + 1].getTextInNode(markdownText).startsWith("|-")
            ) {
                step++
                return@forEachIndexed
            }
        }
    }
    return step == 3
}


// 分割函数
fun splitByImage(nodes: List<ASTNode>): List<List<ASTNode>> {
    val result = mutableListOf<MutableList<ASTNode>>()
    var currentList = mutableListOf<ASTNode>()

    for (node in nodes) {
        if (node.type == MarkdownElementTypes.IMAGE) {
            // 如果当前列表不为空，将其添加到结果中
            if (currentList.isNotEmpty()) {
                result.add(currentList)
            }
            // 开始一个新的子列表，并包含当前的 Image 节点
            currentList = mutableListOf(node)
            result.add(currentList)
            currentList = mutableListOf() // 重置当前列表
        } else {
            // 将非 Image 节点添加到当前列表
            currentList.add(node)
        }
    }

    // 添加最后一个子列表（如果有内容）
    if (currentList.isNotEmpty()) {
        result.add(currentList)
    }

    return result
}


fun List<ASTNode>.getTableHeaders(markdownText: String): List<AnnotatedString> {
    val result = mutableListOf<AnnotatedString>()
    val separator = "|"
    var i = 0
    while (i < size) {
        val node = this[i]
        if (node.getTextInNode(markdownText) == separator) {
            val header = buildAnnotatedString {
                var j = i + 1
                while (j < size) {
                    val content = this@getTableHeaders[j].getTextInNode(markdownText)
                    if (content == separator) {
                        i = j
                    } else {
//                        logger.info { "content:${content}" }
                        println("content:${content}")
                        append(content)
                    }
                    j++
                }
            }
            result.add(header)
        }
        i++
    }
    return result
}


fun parseStyleString(input: CharSequence): Pair<String, String>? {
    val regex = Regex("""^\{\s*(\w+)\s*=\s*"([^"]*)"\s*\}$""")
    return regex.matchEntire(input)?.destructured?.let { (key, value) ->
        Pair(key, value)
    }
}


fun ASTNode.checkNext(markdownContent: String): Modifier {
    require(type == MarkdownElementTypes.PARAGRAPH) { type }
    val pair = parseStyleString(getTextInNode(markdownContent))
    println(pair)
    return if (pair == null) {
        Modifier
    } else {
        when (pair.first) {
            "style" -> {
                when (pair.second) {
                    "note" -> {
                        Modifier
                            .background(Color(225, 241, 225), RoundedCornerShape(6.dp))
                    }

                    else -> {
                        Modifier
                    }
                }

            }

            "width" -> {
                Modifier
                    .width(pair.second.toInt().dp)
            }

            else -> {
                Modifier
            }
        }
    }
}


fun ASTNode.styleByATX(): TextStyle {
    return TextStyle.Default.copy(
        fontWeight = FontWeight.Bold,
        fontSize = when (type) {
            MarkdownElementTypes.ATX_1 -> {
                16.sp * 2
            }

            MarkdownElementTypes.ATX_2 -> {
                16.sp * 1.5
            }

            MarkdownElementTypes.ATX_3 -> {
                16.sp * 1.17
            }

            MarkdownElementTypes.ATX_4 -> {
                16.sp
            }

            MarkdownElementTypes.ATX_5 -> {
                16.sp * 0.87
            }

            else -> {
                throw Exception("node is not ATX")
            }
        }
    )
}