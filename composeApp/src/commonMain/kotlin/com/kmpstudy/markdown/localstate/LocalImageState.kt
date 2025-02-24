package com.kmpstudy.markdown.localstate

import androidx.compose.runtime.compositionLocalOf


data class ImageState(
    val baseUrl: String = "/"
)

val LocalImageState = compositionLocalOf {
    ImageState()
}

