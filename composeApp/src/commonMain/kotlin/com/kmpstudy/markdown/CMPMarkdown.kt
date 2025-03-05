package com.kmpstudy.markdown

import androidx.compose.runtime.Composable
import com.kmpstudy.markdown.parser.MarkdownParser


@Composable
fun CMPMarkdown(markdownContent: String) {
    val parser = MarkdownParser(markdownContent)
    parser.Markdown(enableASTInfo = true)
}

