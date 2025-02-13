package com.mywf

import androidx.compose.runtime.Composable

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()
actual val supportVerticalBar: Boolean
    get() = TODO("Not yet implemented")


actual fun VerticalScrollbar(): () -> Unit {
    TODO("Not yet implemented")
}