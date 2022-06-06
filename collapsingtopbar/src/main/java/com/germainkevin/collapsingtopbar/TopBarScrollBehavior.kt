package com.germainkevin.collapsingtopbar

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Defines how a [CollapsingTopBar] should behave during a [Modifier.nestedScroll] event.
 * */
interface TopBarScrollBehavior {

    /**
     * When set to true, it will make this [CollapsingTopBar] never expand and stay collapsed */
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
     * The offset that is added to the height of the [CollapsingTopBar] based on scroll events
     * */
    var topBarOffset: Float

    /**
     * When true, Sets the [CollapsingTopBar] to an expanded state when first displayed on the UI
     * by setting the [CollapsingTopBar]'s height to [expandedTopBarMaxHeight]
     * */
    var isExpandedWhenFirstDisplayed: Boolean

    /**
     * Whether the title and subtitle should be centered when Expanded
     * */
    var centeredTitleAndSubtitle: Boolean

    /**
     * Tracks how many times [topBarOffset]'s value is 0.0f. Useful when
     * [isExpandedWhenFirstDisplayed] is set to false, because we want the [CollapsingTopBar] to
     * start changing size only after the first scrolling up event is detected through our
     * [nestedScrollConnection], and because the first scrolling up event tend to be the third time
     * [topBarOffset] is equal to 0.0f, all we gotta do is check when [topBarOffset] is equal to
     * 0.0f 3 times, then let [currentTopBarHeight] be equal to
     * [expandedTopBarMaxHeight] + [topBarOffset]
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
     * A [NestedScrollConnection] that should be attached to a [Modifier.nestedScroll] in order to
     * keep track of the scroll events.
     */
    var nestedScrollConnection: NestedScrollConnection
}

class DefaultBehaviorOnScroll(
    override var isAlwaysCollapsed: Boolean,
    override var isExpandedWhenFirstDisplayed: Boolean,
    override var centeredTitleAndSubtitle: Boolean,
    override var collapsedTopBarHeight: Dp,
    override var expandedTopBarMaxHeight: Dp,
) : TopBarScrollBehavior {

    init {
        require(expandedTopBarMaxHeight > collapsedTopBarHeight) {
            "expandedTopBarMaxHeight must be greater than collapsedTopBarHeight"
        }
    }

    override var topBarOffset: Float by mutableStateOf(0f)

    override var trackOffSetIsZero: Int by mutableStateOf(0)

    override var currentTopBarHeight: Dp by mutableStateOf(
        if (isAlwaysCollapsed && isExpandedWhenFirstDisplayed) collapsedTopBarHeight
        else if (isAlwaysCollapsed && !isExpandedWhenFirstDisplayed) collapsedTopBarHeight
        else if (!isAlwaysCollapsed && !isExpandedWhenFirstDisplayed) collapsedTopBarHeight
        else if (!isAlwaysCollapsed && isExpandedWhenFirstDisplayed) expandedTopBarMaxHeight
        else expandedTopBarMaxHeight
    )

    override var offsetLimit: Float = (expandedTopBarMaxHeight - collapsedTopBarHeight).value

    override var nestedScrollConnection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

            if (!isAlwaysCollapsed && !isExpandedWhenFirstDisplayed && trackOffSetIsZero >= 3) {
                // Just making sure trackOffSetIsZero doesn't store high numbers, it's unnecessary
                if (trackOffSetIsZero > 6) {
                    trackOffSetIsZero = 3
                }
                currentTopBarHeight = expandedTopBarMaxHeight + topBarOffset.dp
            } else if (isExpandedWhenFirstDisplayed && !isAlwaysCollapsed) {
                currentTopBarHeight = expandedTopBarMaxHeight + topBarOffset.dp
            }

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
    }
}