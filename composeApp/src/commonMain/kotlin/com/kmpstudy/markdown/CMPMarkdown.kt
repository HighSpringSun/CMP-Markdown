package com.kmpstudy.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.kmpstudy.markdown.parser.MarkdownParser


@Composable
fun CMPMarkdown(
    markdownContent: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    enableASTInfo: Boolean = false
) {
    val parser = MarkdownParser(markdownContent)
    parser.Markdown(
        modifier = modifier,
        textStyle = textStyle,
        enableASTInfo = enableASTInfo
    )
}

