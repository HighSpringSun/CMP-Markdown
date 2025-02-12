package com.mywf

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform