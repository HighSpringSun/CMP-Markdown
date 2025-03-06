package com.kmpstudy

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kmpstudy.markdown.CMPMarkdown
import java.io.File


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CMP-Markdown",
    ) {
        MaterialTheme {
            var markdownContent by remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
//                val filePath = "C:\\Users\\cygao\\Desktop\\supported-platforms.md"
//                val filePath = "C:\\Users\\cygao\\Desktop\\multiplatform-setup.md"
                val filePath = "C:\\Users\\cygao\\Desktop\\kotlin-multiplatform-roadmap.md"
                val markdown = File(filePath).readText().replace("\r\n", "\n")
                markdownContent = markdown
            }
            if (markdownContent.isNotEmpty()) {
//                val parser = remember { MarkdownParser(markdownContent) }
                SelectionContainer {
                    VerticalScrollBox(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                        ) {
                            CMPMarkdown(markdownContent)
//                            MarkdownDocument(
//                                markdown = markdownContent,
//                                textStyles = m3TextStyles(),
//                                textStyleModifiers = m3TextStyleModifiers(),
//                                blockQuoteStyle = m3BlockQuoteStyle(),
//                                codeBlockStyle = m3CodeBlockStyle(),
//                                ruleStyle = m3RuleStyle(),
//                                tableStyle = m3TableStyle(),
//                                onLinkClick = {}
//                            )
                        }
//                        parser.parse(enableASTInfo = true).invoke()
                    }
                }
            }
        }
    }
}

@Composable
fun VerticalScrollBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        val state = rememberScrollState()

        // 可滚动的容器
        Box(
            modifier = Modifier
                .verticalScroll(state) // 关联滚动状态
        ) {
            content() // 内容
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(state),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(6.dp)
        )
    }
}
