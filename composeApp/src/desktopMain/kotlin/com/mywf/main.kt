package com.mywf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastJoinToString
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cmp_markdown.composeapp.generated.resources.Res
import com.mywf.markdown.parser.MarkdownParser
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CMP-Markdown",
    ) {
//        App()
        MaterialTheme(
//            typography = Typography(
//                body1 = TextStyle().copy(
//                    fontWeight = FontWeight.Normal,
//                    fontSize = 18.sp,
//                    lineHeight = 22.sp,
//                    letterSpacing = 0.5.sp
//                )
//            )
        ) {
            var markdownContent by remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                val file = "supported-platforms-zh-cn" //-zh-cn
//                val file = "faq"
                val markdown =
                    Res.readBytes("files/$file.md").decodeToString().replace("\r\n", "\n")
//                println(markdown)
                markdownContent = markdown
            }
            if (markdownContent.isNotEmpty()) {
                val parser = remember { MarkdownParser(markdownContent) }
                Box(
                    contentAlignment = Alignment.TopCenter
                ) {
                    SelectionContainer {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            parser.parse().invoke()
                        }
                    }
//                    androidx.compose.foundation.VerticalScrollbar(
//                        adapter = rememberScrollbarAdapter()
//                    )
                }
            }
        }
    }
}