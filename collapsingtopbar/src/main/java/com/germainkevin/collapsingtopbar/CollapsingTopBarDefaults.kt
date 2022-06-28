package com.germainkevin.collapsingtopbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Contains default values used for the [CollapsingTopBar] implementation. */
object CollapsingTopBarDefaults {

    val DefaultCollapsingTopBarElevation = 0.dp

    val ContentPadding =
        PaddingValues(start = topBarHorizontalPadding, end = topBarHorizontalPadding)

    /**
     * Specifies how the [CollapsingTopBar] should behave when a
     * [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll]
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
    ): CollapsingTopBarScrollBehavior = DefaultBehaviorOnScroll(
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
        contentColor: Color = contentColorFor(backgroundColor = backgroundColor)
    ): CollapsingTopBarColors = CollapsingTopBarColors(backgroundColor, contentColor)
}

/**
 * Default colors used in the [CollapsingTopBar]
 * @param backgroundColor The background color of the [CollapsingTopBar]
 * @param contentColor The color for the content inside [CollapsingTopBar]
 * */
class CollapsingTopBarColors(
    var backgroundColor: Color,
    var contentColor: Color
)

/**
 * Specifies how the [CollapsingTopBar] should behave when a
 * [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll] is detected.
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
): CollapsingTopBarScrollBehavior {
    return remember(
        isAlwaysCollapsed,
        isExpandedWhenFirstDisplayed,
        centeredTitleAndSubtitle,
        collapsedTopBarHeight,
        collapsedTopBarHeight,
        expandedTopBarMaxHeight
    ) {
        CollapsingTopBarDefaults.scrollBehavior(
            isAlwaysCollapsed = isAlwaysCollapsed,
            isExpandedWhenFirstDisplayed = isExpandedWhenFirstDisplayed,
            centeredTitleAndSubtitle = centeredTitleAndSubtitle,
            collapsedTopBarHeight = collapsedTopBarHeight,
            expandedTopBarMaxHeight = expandedTopBarMaxHeight
        )
    }
}