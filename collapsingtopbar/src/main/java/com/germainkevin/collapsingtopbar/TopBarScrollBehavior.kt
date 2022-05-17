package com.germainkevin.collapsingtopbar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Dp


/**
 * A TopBarScrollBehavior defines how a [CollapsingTopBar] should behave when the content under
 * it is scrolled.
 * */
interface TopBarScrollBehavior {

    /**
     * This will make this [CollapsingTopBar] never expand */
    var isAlwaysCollapsed: Boolean

    /**
     * The height of the TopBar when it's collapsed in [Dp]
     * */
    var collapsedTopBarHeight: Dp

    /**
     * The height of the CollapsingTopBar when it's fully expanded
     * */
    var expandedTopBarMaxHeight: Dp

    /**
     * The live height of the [CollapsingTopBar]
     * */
    var currentTopBarHeight: Dp

    /**
     * The offset that changes the height of the [CollapsingTopBar]
     * */
    var topBarOffset: Float

    /**
     * Tracks how many times [topBarOffset]'s value is 0.0f. Useful when [isInitiallyCollapsed] is
     * set to true, because we want the [CollapsingTopBar] to start changing size only after the
     * first scrolling up event is detected through our [nestedScrollConnection], and because
     * the first scrolling up event tend to be the third time [topBarOffset] is equal to 0.0f,
     * all we gotta do is check when [topBarOffset] is equal to 0.0f 3 times, then let
     * [currentTopBarHeight] be equal to [expandedTopBarMaxHeight] + [topBarOffset]
     * */
    var trackOffSetIsZero: Int

    /**
     * When offsetting the [currentTopBarHeight], it subtracts its [expandedTopBarMaxHeight]
     * to the [topBarOffset] so it can decrease the height of the [CollapsingTopBar], but
     * to avoid the [currentTopBarHeight] decreasing below the height of [collapsedTopBarHeight]
     * we create this offset limit that will make sure that the [currentTopBarHeight] only
     * decreases down to the height of [collapsedTopBarHeight], meaning the [CollapsingTopBar]'s
     * height cannot go below [collapsedTopBarHeight]
     * */
    var offsetLimit: Float

    /**
     * Specifies whether the [CollapsingTopBar] should be displayed in a collapsed state when first
     * displayed on the UI. When set to true, [currentTopBarHeight] will be INITIALLY equal to
     * [collapsedTopBarHeight] and when false [currentTopBarHeight] will be INITIALLY equal to
     * [expandedTopBarMaxHeight]
     * */
    var isInitiallyCollapsed: Boolean

    /**
     * A [NestedScrollConnection] that should be attached to a [Modifier.nestedScroll] in order to
     * keep track of the scroll events.
     */
    var nestedScrollConnection: NestedScrollConnection
}

/**
 * @param isAlwaysCollapsed This will make this [CollapsingTopBar] never expand, it's false by default.
 * @param isInitiallyCollapsed Specifies whether the [CollapsingTopBar] should be displayed in a
 * collapsed state when first displayed on the UI. Set to true by default when set in
 * [CollapsingTopBarDefaults.collapsingTopBarScrollBehavior]
 * @see [CollapsingTopBarDefaults.collapsingTopBarScrollBehavior] where it's set to false by default
 * @param collapsedTopBarHeight The height of the [CollapsingTopBar] when it's collapsed, the
 * default value is [defaultMinimumTopBarHeight]
 * @param expandedTopBarMaxHeight The height of the [CollapsingTopBar] when it's expended, the
 * default value is [defaultMaximumTopBarHeight]
 * */
class CollapsingTopBarScrollBehavior(
    override var isAlwaysCollapsed: Boolean,
    override var isInitiallyCollapsed: Boolean,
    override var collapsedTopBarHeight: Dp,
    override var expandedTopBarMaxHeight: Dp,
) : TopBarScrollBehavior {

    override var topBarOffset: Float by mutableStateOf(0f)

    override var trackOffSetIsZero: Int by mutableStateOf(0)

    override var currentTopBarHeight: Dp by mutableStateOf(
        if (isInitiallyCollapsed || isAlwaysCollapsed) collapsedTopBarHeight
        else expandedTopBarMaxHeight
    )

    override var offsetLimit: Float = (expandedTopBarMaxHeight - collapsedTopBarHeight).value

    override var nestedScrollConnection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

            val newOffset = (topBarOffset + available.y)
            val coerced = newOffset.coerceIn(minimumValue = -offsetLimit, maximumValue = 0f)
            topBarOffset = coerced

            if (topBarOffset == 0.0f) {
                trackOffSetIsZero += 1
            }
            // Consume only the scroll on the Y axis.
            available.copy(x = 0f)

            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            return Offset.Zero
        }
    }
}