package com.kmpstudy.model.markdown.exception

data class MarkdownParseException(val name: String) : RuntimeException(name)

data class MarkdownParseTableException(override val message:String):RuntimeException(message)