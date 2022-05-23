package com.germainkevin.collapsingtopbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

/** Contains default values used for the [CollapsingTopBar] implementation. */
object CollapsingTopBarDefaults {

    val ContentPadding =
        PaddingValues(start = appBarHorizontalPadding, end = appBarHorizontalPadding)

    /**
     * Specifies how the [CollapsingTopBar] should behave when a [Modifier.nestedScroll] is detected.
     *
     *  @param isAlwaysCollapsed This will make this [CollapsingTopBar] never expand and stay with
     *  the [collapsedTopBarHeight] height, it's false by default
     * @param isInitiallyCollapsed Specifies whether the [CollapsingTopBar] should be displayed in a
     * collapsed state when first displayed on the UI.
     * @param collapsedTopBarHeight The height of the [CollapsingTopBar] when it's collapsed, the
     * default value is [defaultMinimumTopBarHeight]
     * @param expandedTopBarMaxHeight The height of the [CollapsingTopBar] when it's expended,
     * the default value is [defaultMaximumTopBarHeight]
     * */
    fun collapsingTopBarScrollBehavior(
        isAlwaysCollapsed: Boolean = false,
        isInitiallyCollapsed: Boolean = true,
        collapsedTopBarHeight: Dp = defaultMinimumTopBarHeight,
        expandedTopBarMaxHeight: Dp = defaultMaximumTopBarHeight,
    ): TopBarScrollBehavior = CollapsingTopBarScrollBehavior(
        isAlwaysCollapsed = isAlwaysCollapsed,
        isInitiallyCollapsed = isInitiallyCollapsed,
        collapsedTopBarHeight = collapsedTopBarHeight,
        expandedTopBarMaxHeight = expandedTopBarMaxHeight,
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