package com.kmpstudy.markdown.renderer.style

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * 自定义Shape，只在右侧绘制边框
 */
class RightBorderShape(
    private val borderWidth: Dp = 0.5.dp,
    private val showBorder: Boolean = true
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val borderWidthPx = with(density) { borderWidth.toPx() }

        return if (showBorder) {
            val path = Path().apply {
                // 绘制右边框
                moveTo(size.width - borderWidthPx, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(size.width - borderWidthPx, size.height)
                close()
            }
            Outline.Generic(path)
        } else {
            // 如果不显示边框，返回一个空的轮廓
            Outline.Rectangle(Rect(0f, 0f, size.width, size.height))
        }
    }
}