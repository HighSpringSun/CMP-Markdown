package com.kmpstudy.markdown.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmpstudy.markdown.parser.MarkdownTableState

@Composable
fun Table(
    markdownTableState: MarkdownTableState,
    modifier: Modifier = Modifier
) {
    // 表格整体样式
    Column(
        modifier = modifier
            .padding(bottom = 24.dp)
            .fillMaxWidth()
            .border(1.dp, Color.Gray) // 表格边框
    ) {
        // 表头
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray), // 表头背景色
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            markdownTableState.headers.forEach { header ->
                Row(
                    modifier = Modifier
                        .weight(1f) // 单元格边框
                        .drawBehind {
                            drawLine(
                                color = Color.Gray,
                                start = Offset(size.width, 0f),
                                end = Offset(size.width, size.height)
                            )
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = header,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                    )
                }
            }
        }

        // 表体
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            markdownTableState.rows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(
                                color = Color.Gray,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height)
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { cell ->
                        Text(
                            text = cell,
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                                .drawBehind {
                                    drawLine(
                                        color = Color.Gray,
                                        start = Offset(size.width, 0f),
                                        end = Offset(size.width, size.height)
                                    )
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

