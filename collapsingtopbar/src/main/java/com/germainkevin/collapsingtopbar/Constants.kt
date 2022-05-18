package com.germainkevin.collapsingtopbar

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val appBarHorizontalPadding = 4.dp

val defaultMinimumTopBarHeight = 56.dp

val defaultMaximumTopBarHeight = 156.dp

// Start inset for the title when there is no navigation icon provided
val noNavIconSpacerModifier = Modifier.width(16.dp - appBarHorizontalPadding)

val navigationIconModifier = Modifier
    .fillMaxHeight()
    .width(56.dp - appBarHorizontalPadding)

// MaterialTheme H6
val DefaultExpandedTitleTextStyle: TextStyle = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 20.sp,
    letterSpacing = 0.15.sp
)

// MaterialTheme Subtitle1
val DefaultExpandedSubtitleTextStyle: TextStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    letterSpacing = 0.15.sp
)