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
import androidx.compose.runtime.CompositionLocalProvider
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
import com.fleeksoft.ksoup.Ksoup
import com.kmpstudy.markdown.CMPMarkdown
import com.kmpstudy.markdown.localstate.ImageState
import com.kmpstudy.markdown.localstate.LocalImageState
import java.io.File

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CMP-Markdown",
    ) {
//        val markdownContent = "<!-- If you're interested in having this functionality expanded to a shared module, please vote for this issue in Youtrack and describe your use case. -->"
//        CMPMarkdown(markdownContent)
        val html = """
             <p>This is the second part of the <strong>Create a Kotlin Multiplatform app with shared logic and native UI</strong> tutorial. Before proceeding, make sure you've completed previous steps.</p>
    <p><img src="icon-1-done.svg" width="20" alt="First step"/> <a href="multiplatform-create-first-app.md">Create your Kotlin Multiplatform app</a><br/>
       <img src="icon-2.svg" width="20" alt="Second step"/> <strong>Update the user interface</strong><br/>
       <img src="icon-3-todo.svg" width="20" alt="Third step"/> Add dependencies<br/>       
       <img src="icon-4-todo.svg" width="20" alt="Fourth step"/> Share more logic<br/>
       <img src="icon-5-todo.svg" width="20" alt="Fifth step"/> Wrap up your project<br/>
    </p>
        """.trimIndent()
        val doc = Ksoup.parse(html)
//        println(doc.body().getAllElements()[0].childElementsList())
    }
}


//fun main() = application {
//    Window(
//        onCloseRequest = ::exitApplication,
//        title = "CMP-Markdown",
//    ) {
//        MaterialTheme {
//            var markdownContent by remember { mutableStateOf("") }
//            LaunchedEffect(Unit) {
////                val filePath= "/Users/apple/Desktop/markdown/en/multiplatform-onboard/multiplatform-dependencies.md"
////                val filePath = "C:\\Users\\cygao\\Desktop\\test.txt"
////                val filePath = "C:\\Users\\cygao\\Desktop\\markdown\\en\\faq.md"
//                val filePath = "C:\\Users\\cygao\\Desktop\\markdown\\en\\development\\multiplatform-ktor-sqldelight.md"
////                val filePath = "C:\\Users\\cygao\\Desktop\\supported-platforms-zh-cn.md"
////                val filePath = "C:\\Users\\cygao\\Desktop\\markdown\\en\\multiplatform-setup.md"
////                val filePath = "C:\\Users\\cygao\\Desktop\\kotlin-multiplatform-roadmap.md"
////                val filePath = "C:\\Users\\cygao\\Desktop\\multiplatform-upgrade-app.md"
////                val filePath = "C:\\Users\\cygao\\Desktop\\markdown\\en\\multiplatform-onboard\\multiplatform-dependencies.md"
////                val filePath = "C:\\Users\\cygao\\Desktop\\markdown\\en\\multiplatform-onboard\\multiplatform-wrap-up.md"
////                val filePath = "C:\\Users\\cygao\\Desktop\\markdown\\en\\multiplatform-onboard\\multiplatform-upgrade-app.md"
//                val markdown = File(filePath).readText().replace("\r\n", "\n")
//                markdownContent = markdown
//            }
//            if (markdownContent.isNotEmpty()) {
//                SelectionContainer {
//                    VerticalScrollBox(
//                        modifier = Modifier
//                            .fillMaxSize(),
//                        contentAlignment = Alignment.TopCenter
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth(0.8f)
//                        ) {
//                            CompositionLocalProvider(
//                                LocalImageState provides ImageState(
//                                    baseUrl = "https://resources.jetbrains.com/help/img/kotlin-multiplatform-dev/"
//                                )
//                            ) {
//                                CMPMarkdown(markdownContent)
//                            }
////                            MarkdownDocument(
////                                markdown = markdownContent,
////                                textStyles = m3TextStyles(),
////                                textStyleModifiers = m3TextStyleModifiers(),
////                                blockQuoteStyle = m3BlockQuoteStyle(),
////                                codeBlockStyle = m3CodeBlockStyle(),
////                                ruleStyle = m3RuleStyle(),
////                                tableStyle = m3TableStyle(),
////                                onLinkClick = {}
////                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

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
