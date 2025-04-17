package com.kmpstudy.markdown.parser

//import platform.WebKit.WKWebView

actual class HtmlBlockParser {
//    private val webView by lazy { WKWebView() }

    actual fun parseHtml(html: String): HtmlNode {
//        // 使用协程等待 JavaScript 执行完成
//        val result = CompletableDeferred<HtmlNode>()
//
//        val jsCode = """
//        const parser = new DOMParser();
//        const doc = parser.parseFromString(`$html`, "text/html");
//        doc.body.innerHTML;
//        """.trimIndent()
//        // 执行 JavaScript 并处理结果
//        webView.evaluateJavaScript(jsCode) { value, error ->
//            if (error != null) {
//                println("Error extracting HTML: $error")
//                result.complete(HtmlNode.Text("")) // 返回空文本节点
//            } else {
//                val rawHtml = value as? String ?: ""
////                val parsedHtml = parseHtmlToNode(rawHtml)
////                result.complete(parsedHtml) // 完成解析
//            }
//        }
//
//        // 等待结果并返回
//        return result.await()
        TODO("Not Support Yet")
    }
}
