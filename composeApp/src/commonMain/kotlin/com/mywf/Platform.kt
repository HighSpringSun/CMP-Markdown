package com.mywf

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect val supportVerticalBar: Boolean


//expect fun VerticalScrollbar(state: ScrollState, modifier: Modifier): @Composable () -> Unit

//VerticalScrollbar(
//adapter = rememberScrollbarAdapter(state),
//modifier = Modifier
//.align(Alignment.CenterEnd)
//.width(6.dp)
//)