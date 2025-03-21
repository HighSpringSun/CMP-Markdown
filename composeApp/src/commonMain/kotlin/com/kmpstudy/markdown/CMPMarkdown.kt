package com.kmpstudy.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kmpstudy.markdown.parser.MarkdownParser


@Composable
fun CMPMarkdown(
    markdownContent: String,
    modifier: Modifier = Modifier,
    enableASTInfo: Boolean = false
) {
    val parser = MarkdownParser(markdownContent)
    parser.Markdown(
        modifier = modifier,
        enableASTInfo = enableASTInfo
    )
}

