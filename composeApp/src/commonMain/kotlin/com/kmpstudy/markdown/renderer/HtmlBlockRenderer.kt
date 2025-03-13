package com.kmpstudy.markdown.renderer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kmpstudy.markdown.localstate.LocalImageState
import com.kmpstudy.markdown.parser.HtmlNode


@Composable
fun HtmlBlockRenderer(htmlNode: HtmlNode) {
    Column(
        modifier = Modifier
    ) {
        when (htmlNode) {
            is HtmlNode.Element -> {
                when (htmlNode.tagName) {
                    "tldr" -> {

                    }

                    "p" -> {

                    }

                    "img" -> {
                        val localImgState = LocalImageState.current
                        val src = "${localImgState.baseUrl}${htmlNode.attributes["src"]}"
                        val width = htmlNode.attributes["width"]?.toInt()
                        val alt = htmlNode.attributes["alt"]
                        val modifier = if (width != null) {
                            Modifier.width(width.dp)
                        } else {
                            Modifier
                        }
                        AsyncImage(
                            src,
                            contentDescription = alt,
                            modifier = modifier
                        )
                    }

                    "a" -> {

                    }

                    "strong" -> {

                    }
                }
                Column(
                    modifier = Modifier
                ) {
                    htmlNode.children.forEach { childNode ->
                        HtmlBlockRenderer(childNode)
                    }
                }
            }

            is HtmlNode.Text -> {
                Text(htmlNode.content)
            }
        }
    }
}