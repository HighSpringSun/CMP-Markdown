package com.kmpstudy.markdown.localstate

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.compositionLocalOf


val LocalInlineContent = compositionLocalOf {
    mutableMapOf<String,InlineTextContent>()
}


