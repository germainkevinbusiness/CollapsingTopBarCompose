package com.germainkevin.collapsingtopbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.contentColorFor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/** Contains default values used for the [CollapsingTopBar] implementation. */
object CollapsingTopBarDefaults {

    val ContentPadding =
        PaddingValues(start = appBarHorizontalPadding, end = appBarHorizontalPadding)

    /**
     * Specifies how the [CollapsingTopBar] should behave when a [Modifier.nestedScroll]
     * is detected.
     *
     *  @param isAlwaysCollapsed This will make this [CollapsingTopBar] stay collapsed and stay with
     *  the [collapsedTopBarHeight] height. It's false by default
     * @param isExpandedWhenFirstDisplayed When true, Sets the [CollapsingTopBar] to an expanded
     * state when first displayed on the UI by setting the [CollapsingTopBar]'s height to
     * [expandedTopBarMaxHeight]
     * @param centeredTitleAndSubtitle Whether the title and subtitle should be centered when
     * Expanded
     * @param collapsedTopBarHeight The height of the [CollapsingTopBar] when it's collapsed, the
     * default value is [defaultMinimumTopBarHeight]
     * @param expandedTopBarMaxHeight The height of the [CollapsingTopBar] when it's expended,
     * the default value is [defaultMaximumTopBarHeight]
     * */
    fun scrollBehavior(
        isAlwaysCollapsed: Boolean = false,
        isExpandedWhenFirstDisplayed: Boolean = true,
        centeredTitleAndSubtitle: Boolean = true,
        collapsedTopBarHeight: Dp = defaultMinimumTopBarHeight,
        expandedTopBarMaxHeight: Dp = defaultMaximumTopBarHeight,
    ): TopBarScrollBehavior = DefaultBehaviorOnScroll(
        isAlwaysCollapsed = isAlwaysCollapsed,
        isExpandedWhenFirstDisplayed = isExpandedWhenFirstDisplayed,
        centeredTitleAndSubtitle = centeredTitleAndSubtitle,
        collapsedTopBarHeight = collapsedTopBarHeight,
        expandedTopBarMaxHeight = expandedTopBarMaxHeight
    )

    /**
     * Default colors used in the [CollapsingTopBar]
     * @param backgroundColor The background color of the [CollapsingTopBar]
     * @param contentColor The content color inside of the [CollapsingTopBar]
     * */
    @Composable
    fun colors(
        backgroundColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = contentColorFor(backgroundColor)
    ): CollapsingTopBarColors = DefaultCollapsingTopBarColors(backgroundColor, contentColor)
}


/**
 * Specifies how the [CollapsingTopBar] should behave when a [Modifier.nestedScroll]
 * is detected.
 *
 * @param isAlwaysCollapsed This will make this [CollapsingTopBar] stay collapsed and stay with
 * the [collapsedTopBarHeight] height.
 * @param isExpandedWhenFirstDisplayed When true, Sets the [CollapsingTopBar] to an expanded
 * state when first displayed on the UI by setting the [CollapsingTopBar]'s height to
 * [expandedTopBarMaxHeight]
 * @param centeredTitleAndSubtitle Whether the title and subtitle should be centered when Expanded
 * @param collapsedTopBarHeight The height of the [CollapsingTopBar] when it's collapsed, the
 * default value is [defaultMinimumTopBarHeight]
 * @param expandedTopBarMaxHeight The height of the [CollapsingTopBar] when it's expended, the
 * default value is [defaultMaximumTopBarHeight]
 * */
@Composable
fun rememberCollapsingTopBarScrollBehavior(
    isAlwaysCollapsed: Boolean = false,
    isExpandedWhenFirstDisplayed: Boolean = true,
    centeredTitleAndSubtitle: Boolean = true,
    collapsedTopBarHeight: Dp = defaultMinimumTopBarHeight,
    expandedTopBarMaxHeight: Dp = defaultMaximumTopBarHeight,
): TopBarScrollBehavior = remember {
    CollapsingTopBarDefaults.scrollBehavior(
        isAlwaysCollapsed = isAlwaysCollapsed,
        isExpandedWhenFirstDisplayed = isExpandedWhenFirstDisplayed,
        centeredTitleAndSubtitle = centeredTitleAndSubtitle,
        collapsedTopBarHeight = collapsedTopBarHeight,
        expandedTopBarMaxHeight = expandedTopBarMaxHeight
    )
}