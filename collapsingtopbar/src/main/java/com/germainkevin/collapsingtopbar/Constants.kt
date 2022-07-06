package com.germainkevin.collapsingtopbar

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * The default collapsed height of the [CollapsingTopBar]
 * */
internal val defaultMinimumTopBarHeight = 56.dp

/**
 * The default expanded height of the [CollapsingTopBar]
 * */
internal val defaultMaximumTopBarHeight = 156.dp

internal val topBarHorizontalPadding = 4.dp

/**
 * [Modifier] when there isn't a navigation icon provided. Start inset for the title slot inside
 * the [CollapsingTopBar]
 * */
private val noNavIconSpacerModifier = Modifier.width(16.dp - topBarHorizontalPadding)

/**
 * [Modifier] when there is a navigation icon provided
 * */
private val navigationIconModifier = Modifier
    .fillMaxHeight()
    .width(56.dp - topBarHorizontalPadding)


internal val navigationIconRow: @Composable (@Composable (() -> Unit)?) -> Unit =
    { navigationIcon ->
        if (navigationIcon == null) Spacer(modifier = noNavIconSpacerModifier)
        else {
            Row(
                modifier = navigationIconModifier,
                verticalAlignment = Alignment.Bottom,
                content = { navigationIcon() }
            )
        }
    }

internal val collapsedTitle: @Composable (Boolean, Float, @Composable () -> Unit) -> Unit =
    { centeredTitleAndSubtitle, collapsedTitleAlpha, title ->
        val enterAnimation = if (centeredTitleAndSubtitle)
            expandVertically(
                // Expands from bottom to top.
                expandFrom = Alignment.Top
            ) + fadeIn(initialAlpha = collapsedTitleAlpha)
        else fadeIn(initialAlpha = collapsedTitleAlpha)

        val exitAnimation = if (centeredTitleAndSubtitle)
            slideOutVertically() + fadeOut() else fadeOut()
        AnimatedVisibility(
            visible = collapsedTitleAlpha in 0f..1f,
            enter = enterAnimation,
            exit = exitAnimation
        ) { title() }
    }

/**
 * The Section where all the options menu items will be laid out on
 * */
internal val actionsRow: @Composable (@Composable RowScope.() -> Unit) -> Unit = {
    Row(
        modifier = Modifier.fillMaxHeight(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
        content = it
    )
}

/**
 * Will provide us with the current background color of the [CollapsingTopBar].
 * */
@Composable
internal fun CollapsingTopBarScrollBehavior.currentBackgroundColor(
    colors: CollapsingTopBarColors
): State<Color> = animateColorAsState(
    targetValue =
    if (currentTopBarHeight == collapsedTopBarHeight ||
        currentTopBarHeight == expandedTopBarMaxHeight
    ) {
        colors.onBackgroundColorChange(colors.backgroundColor)
        colors.backgroundColor
    } else {
        colors.onBackgroundColorChange(colors.backgroundColorWhenCollapsingOrExpanding)
        colors.backgroundColorWhenCollapsingOrExpanding
    }
)