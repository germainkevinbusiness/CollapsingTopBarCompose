package com.germainkevin.collapsingtopbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/** Contains default values used for the [CollapsingTopBar] implementation. */
object CollapsingTopBarDefaults {

    val ContentPadding =
        PaddingValues(start = appBarHorizontalPadding, end = appBarHorizontalPadding)

    /**
     * @param isInitiallyCollapsed Specifies whether the [CollapsingTopBar] should be displayed in a
     * collapsed state when first displayed on the UI.
     * @param collapsedTopBarHeight The height of the [CollapsingTopBar] when it's collapsed, the
     * default value is [defaultMinimumTopBarHeight]
     * @param expandedTopBarHeight The height of the [CollapsingTopBar] when it's expended,
     * the default value is [defaultMaximumTopBarHeight]
     * */
    fun collapsingTopBarScrollBehavior(
        isInitiallyCollapsed: Boolean = true,
        collapsedTopBarHeight: Dp = defaultMinimumTopBarHeight,
        expandedTopBarHeight: Dp = defaultMaximumTopBarHeight
    ): TopBarScrollBehavior {
        return CollapsingTopBarScrollBehavior(
            isInitiallyCollapsed = isInitiallyCollapsed,
            collapsedHeight = collapsedTopBarHeight,
            expandedHeight = expandedTopBarHeight
        )
    }

    @Composable
    fun collapsingTopBarColors(
        backgroundColor: Color = MaterialTheme.colors.background,
        contentColor: Color = contentColorFor(backgroundColor)
    ): CollapsingTopBarColors = CollapsingTopBarColorsImpl(backgroundColor, contentColor)
}