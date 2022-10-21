package com.germainkevin.collapsingtopbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp

/**
 * Defines how a [CollapsingTopBar] should behave, mainly during a
 * [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll] event.
 *
 * Have a class extend this interface if you want to create your own scroll behavior.
 * */
interface CollapsingTopBarScrollBehavior {

    /**
     * When set to true, it will make this [CollapsingTopBar] never expand and stay collapsed */
    var isAlwaysCollapsed: Boolean

    /**
     * The height of the [CollapsingTopBar] when it's collapsed in [Dp]
     * */
    var collapsedTopBarHeight: Dp

    /**
     * The height of the [CollapsingTopBar] when it's fully expanded in [Dp]
     * */
    var expandedTopBarMaxHeight: Dp

    /**
     * The current height of the [CollapsingTopBar] in [Dp]
     * */
    var currentTopBarHeight: Dp

    /**
     * Notifies about the current [CollapsingTopBarState]
     * */
    var currentState: CollapsingTopBarState

    /**
     * Checks whether the [currentState] is [CollapsingTopBarState.COLLAPSED]
     * */
    var isCollapsed: Boolean

    /**
     * Checks whether the [currentState] is [CollapsingTopBarState.MOVING]
     * */
    var isMoving: Boolean

    /**
     * Checks whether the [currentState] is [CollapsingTopBarState.EXPANDED]
     * */
    var isExpanded: Boolean

    /**
     * Is initially assigned the [NestedScrollConnection.onPreScroll]'s "available" [Offset.y].
     * It's a value that is added to the height of the [CollapsingTopBar] when there is a
     * [nestedScroll] event and [isAlwaysCollapsed] is false.
     * */
    var heightOffset: Float

    /**
     * When true, Sets the [CollapsingTopBar] to an expanded state when first displayed on the UI
     * by setting the [CollapsingTopBar]'s height to [expandedTopBarMaxHeight] when it's first
     * being displayed on the UI.
     * */
    var isExpandedWhenFirstDisplayed: Boolean

    /**
     * Should the title be centered when the [CollapsingTopBar] is collapsed
     * */
    var centeredTitleWhenCollapsed: Boolean

    /**
     * Whether the title and subtitle should be centered when Expanded
     * */
    var centeredTitleAndSubtitle: Boolean

    /**
     * Exists only to handle the case of [isExpandedWhenFirstDisplayed] == false.
     *
     *
     * Is incremented inside this [nestedScrollConnection]'s [NestedScrollConnection.onPreScroll]
     * event listener, everytime the [heightOffset] is equal to 0f.
     *
     *
     * When the [currentTopBarHeight] is equal to [collapsedTopBarHeight] the first time it's drawn
     * on the UI, we want to prevent a sudden change of the [currentTopBarHeight]'s value from
     * being [collapsedTopBarHeight] to now being equal to [expandedTopBarMaxHeight],
     *
     * instead
     * we give the [heightOffset] the time to resolve the exact number of dp we should add to
     * the [currentTopBarHeight] so that we add the right amount of dp to expand from its
     * size of [collapsedTopBarHeight] to whatever necessary size <= [expandedTopBarMaxHeight].
     *
     * In order for the [heightOffset] to have time to do that, we wait until the 3rd time the
     * user tries to scroll down from the absolute top of the layout where the scroll is detected,
     * then we make the [CollapsingTopBar] expandable, meaning we now start adding dp values to
     * [currentTopBarHeight] meanwhile it's equal to [collapsedTopBarHeight]
     *
     * */
    var countWhenHeightOffSetIsZero: Int

    /**
     * When offsetting the [currentTopBarHeight], it subtracts its [expandedTopBarMaxHeight]
     * to the [heightOffset] so it can decrease the height of the [CollapsingTopBar], but
     * to avoid the [currentTopBarHeight] decreasing below the height of [collapsedTopBarHeight]
     * we create this offset limit that will make sure that the [currentTopBarHeight] only
     * decreases down to the height of [collapsedTopBarHeight], meaning the [CollapsingTopBar]'s
     * height cannot go below [collapsedTopBarHeight]
     * */
    var heightOffsetLimit: Float

    /**
     * A [NestedScrollConnection] that should be attached to a
     * [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll] in order to
     * keep track of the scroll events.
     */
    val nestedScrollConnection: NestedScrollConnection

    /**
     * The visibility alpha value of the Column which holds as children the Title and Subtitle
     * */
    val expandedColumnAlpha: @Composable () -> State<Float>

    /**
     * The visibility alpha value of the Title displayed and visible when the [CollapsingTopBar] is
     * collapsed
     * */
    val collapsedTitleAlpha: @Composable () -> State<Float>

    /**
     * Useful to ignore the [NestedScrollConnection.onPreScroll] data when the methods
     * [collapse] or [expand] are active
     * */
    var ignorePreScrollDetection: Boolean

    /**
     * This is the [ScrollState] that is registering the vertical scroll state of the
     * [CollapsingTopBar]
     * */
    var topBarVerticalScrollState: @Composable () -> ScrollState

    /**
     * Assign a [ScrollableState] to this variable that you will pass inside a LazyColumn, so
     * that the [CollapsingTopBar] can only expand when this LazyColumn's
     * firstVisibleItemScrollOffset is == 0.
     * */
    val scrollableState: ScrollableState?

    /**
     * The instructions that will run on the call of [NestedScrollConnection.onPreScroll].
     * */
    val onPreScrollExecutable: (Offset) -> Unit

}

/**
 * Behavior affected by the PreScroll events from the [NestedScrollConnection]
 * */
class DefaultBehaviorOnScroll(
    override var isAlwaysCollapsed: Boolean,
    override var isExpandedWhenFirstDisplayed: Boolean,
    override var centeredTitleWhenCollapsed: Boolean,
    override var centeredTitleAndSubtitle: Boolean,
    override var collapsedTopBarHeight: Dp,
    override var expandedTopBarMaxHeight: Dp,
    override val scrollableState: ScrollableState?,
) :
    CollapsingTopBarScrollBehavior {

    init {
        require(expandedTopBarMaxHeight > collapsedTopBarHeight) {
            "expandedTopBarMaxHeight ($expandedTopBarMaxHeight) must be greater " +
                    "than collapsedTopBarHeight ($collapsedTopBarHeight)"
        }
    }

    override var heightOffset: Float by mutableStateOf(0f)

    override var countWhenHeightOffSetIsZero: Int by mutableStateOf(0)

    override var currentTopBarHeight: Dp by mutableStateOf(
        if (isAlwaysCollapsed) collapsedTopBarHeight else {
            if (isExpandedWhenFirstDisplayed) expandedTopBarMaxHeight
            if (!isExpandedWhenFirstDisplayed) collapsedTopBarHeight
            else expandedTopBarMaxHeight
        }
    )

    override var currentState: CollapsingTopBarState by mutableStateOf(
        when (currentTopBarHeight) {
            collapsedTopBarHeight -> CollapsingTopBarState.COLLAPSED
            expandedTopBarMaxHeight -> CollapsingTopBarState.EXPANDED
            else -> CollapsingTopBarState.MOVING
        }
    )

    override var isCollapsed: Boolean by mutableStateOf(
        currentState == CollapsingTopBarState.COLLAPSED
    )

    override var isMoving: Boolean by mutableStateOf(
        currentState == CollapsingTopBarState.MOVING
    )

    override var isExpanded: Boolean by mutableStateOf(
        currentState == CollapsingTopBarState.EXPANDED
    )

    override var heightOffsetLimit: Float = (expandedTopBarMaxHeight - collapsedTopBarHeight).value

    override var ignorePreScrollDetection: Boolean by mutableStateOf(false)

    override var topBarVerticalScrollState: @Composable () -> ScrollState = {
        rememberScrollState()
    }

    override val onPreScrollExecutable: (Offset) -> Unit
        get() = { available ->
            scrollableState?.let {
                when (it) {
                    is LazyListState -> {
                        onPreScrollWithLazyListState(available, it)
                    }
                    is LazyGridState -> {
                        onPreScrollWithLazyGridState(available, it)
                    }
                    else -> onPreScrollDefaultBehavior(available)
                }
            }
                ?: onPreScrollDefaultBehavior(available)
        }

    override val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            onPreScrollExecutable.invoke(available)
            defineCurrentState()
            return Offset.Zero
        }
    }

    override val expandedColumnAlpha: @Composable () -> State<Float> = {
        getExpandedColumnAlpha()
    }

    override val collapsedTitleAlpha: @Composable () -> State<Float> = {
        getCollapsedTitleAlpha()
    }
}