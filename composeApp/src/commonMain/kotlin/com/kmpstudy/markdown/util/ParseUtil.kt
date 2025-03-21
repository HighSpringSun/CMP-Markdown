package com.kmpstudy.markdown.util


fun String.isUrl(): Boolean {
    return startsWith("https://") || startsWith("http://") || startsWith("ftp://")
}