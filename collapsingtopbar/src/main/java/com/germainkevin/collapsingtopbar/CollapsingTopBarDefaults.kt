package com.germainkevin.collapsingtopbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/** Contains default values used for the [CollapsingTopBar] implementation. */
object CollapsingTopBarDefaults {

    val ContentPadding =
        PaddingValues(start = appBarHorizontalPadding, end = appBarHorizontalPadding)

    /**
     * Specifies how the [CollapsingTopBar] should behave when a [Modifier.nestedScroll] is detected.
     *
     *  @param isAlwaysCollapsed This will make this [CollapsingTopBar] stay collapsed and stay with
     *  the [collapsedTopBarHeight] height. It's false by default
     * @param isExpandedWhenFirstDisplayed When true, Sets the [CollapsingTopBar] to an expanded
     * state when first displayed on the UI by setting the [CollapsingTopBar]'s height to
     * [expandedTopBarMaxHeight]
     * @param collapsedTopBarHeight The height of the [CollapsingTopBar] when it's collapsed, the
     * default value is [defaultMinimumTopBarHeight]
     * @param expandedTopBarMaxHeight The height of the [CollapsingTopBar] when it's expended,
     * the default value is [defaultMaximumTopBarHeight]
     * */
    fun behaviorOnScroll(
        isAlwaysCollapsed: Boolean = false,
        isExpandedWhenFirstDisplayed: Boolean = true,
        collapsedTopBarHeight: Dp = defaultMinimumTopBarHeight,
        expandedTopBarMaxHeight: Dp = defaultMaximumTopBarHeight,
    ): TopBarScrollBehavior = DefaultBehaviorOnScroll(
        isAlwaysCollapsed,
        isExpandedWhenFirstDisplayed,
        collapsedTopBarHeight,
        expandedTopBarMaxHeight
    )

    /**
     * Default colors used in the [CollapsingTopBar]
     * */
    @Composable
    fun collapsingTopBarColors(
        backgroundColor: Color = MaterialTheme.colors.background,
        contentColor: Color = contentColorFor(backgroundColor)
    ): CollapsingTopBarColors = CollapsingTopBarColorsImpl(backgroundColor, contentColor)
}