package com.germainkevin.collapsingtopbar

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
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