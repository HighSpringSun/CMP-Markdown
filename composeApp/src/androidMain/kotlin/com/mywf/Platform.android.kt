package com.mywf

import android.os.Build
import androidx.compose.runtime.Composable

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual val supportVerticalBar: Boolean
    get() = TODO("Not yet implemented")


actual fun VerticalScrollbar(): () -> Unit {
    TODO("Not yet implemented")
}