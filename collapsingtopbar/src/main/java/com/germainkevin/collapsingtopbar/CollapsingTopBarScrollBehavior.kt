package com.germainkevin.collapsingtopbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import timber.log.Timber
import kotlin.math.roundToInt

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
    var trackOffSetIsZero: Int

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
}

class DefaultBehaviorOnScroll(
    override var isAlwaysCollapsed: Boolean,
    override var isExpandedWhenFirstDisplayed: Boolean,
    override var centeredTitleWhenCollapsed: Boolean,
    override var centeredTitleAndSubtitle: Boolean,
    override var collapsedTopBarHeight: Dp,
    override var expandedTopBarMaxHeight: Dp,
) : CollapsingTopBarScrollBehavior {

    init {
        require(expandedTopBarMaxHeight > collapsedTopBarHeight) {
            "expandedTopBarMaxHeight ($expandedTopBarMaxHeight) must be greater " +
                    "than collapsedTopBarHeight ($collapsedTopBarHeight)"
        }
    }

    override var heightOffset: Float by mutableStateOf(0f)

    override var trackOffSetIsZero: Int by mutableStateOf(0)

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

    override val nestedScrollConnection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

            Timber.d("Calculus: source: $source")

            if (!isAlwaysCollapsed && !ignorePreScrollDetection) {
                incrementTopBarOffset()
                plateauTopBarOffset()
                trackPreScrollData(available)
                if (!isExpandedWhenFirstDisplayed && trackOffSetIsZero >= 3) {
                    currentTopBarHeight = expandedTopBarMaxHeight + heightOffset.roundToInt().dp
                } else if (isExpandedWhenFirstDisplayed) {
                    currentTopBarHeight = expandedTopBarMaxHeight + heightOffset.roundToInt().dp
                }

                defineCurrentState()
            }

            return Offset.Zero
        }
    }

    private fun trackPreScrollData(available: Offset) {
        val availableY = available.y.toInt()
        val newOffset = (heightOffset + availableY)
        val coerced = newOffset.coerceIn(minimumValue = -heightOffsetLimit, maximumValue = 0f)
        heightOffset = coerced
    }

    private fun incrementTopBarOffset() {
        if (heightOffset == 0f) {
            trackOffSetIsZero += 1
        }
    }

    // Just keeping trackOffSetIsZero from storing high numbers that are above 3
    private fun plateauTopBarOffset() {
        if (trackOffSetIsZero > 6) {
            trackOffSetIsZero = 3
        }
    }

    override val expandedColumnAlpha: @Composable () -> State<Float> = {
        getExpandedColumnAlpha()
    }

    override val collapsedTitleAlpha: @Composable () -> State<Float> = {
        getCollapsedTitleAlpha()
    }

    /**
     * In order to know the alpha value between 0F and 1F of the "expandedTitle subtitle column",
     * we will use the height of the [CollapsingTopBar] which is the [currentTopBarHeight].
     *
     *
     * We want that whenever the
     * [currentTopBarHeight] is exactly equal to the value of [collapsedTopBarHeight] + [margin]
     * that the "expandedTitle subtitle Column" become fully invisible or alpha = 0F
     *
     *
     * But, when the [currentTopBarHeight] is exactly equal to the value of
     * [expandedTopBarMaxHeight] that the "expandedTitle subtitle Column"
     * become fully visible or alpha = 1F.
     *
     *
     * So in this sense, the 0F and 1F alpha value of the "expandedTitle subtitle Column" are:
     *
     *
     * val fullyInvisibleValue: Dp = [collapsedTopBarHeight] + [margin]
     *
     *
     * val fullyVisibleValue: Dp = [expandedTopBarMaxHeight] - (fullyInvisibleValue)
     *
     *
     * @param margin When [collapsedTopBarHeight] + [margin] is the current value of
     * [currentTopBarHeight], it will trigger the "expandedTitle subtitle Column"'s alpha value
     * to be equal to 0F.
     */
    @Composable
    private fun getExpandedColumnAlpha(margin: Dp = 20.dp): State<Float> {
        return animateFloatAsState(
            (currentTopBarHeight - (collapsedTopBarHeight + margin)) /
                    (expandedTopBarMaxHeight - (collapsedTopBarHeight + margin))
        )
    }

    /**
     * Sets the alpha value of the collapsed title section
     * @param visibleValue A value in [Dp] that if [currentTopBarHeight] reaches it, the
     * Collapsed Title should become visible.
     * Collapsed Title section should become invisible
     * */
    @Composable
    private fun getCollapsedTitleAlpha(
        visibleValue: Dp = collapsedTopBarHeight.toIntDp(),
        invisibleValue: Dp = (collapsedTopBarHeight + 15.dp).toIntDp()
    ): State<Float> {
        return animateFloatAsState(
            if (currentTopBarHeight.toIntDp() == visibleValue) 1f
            else (visibleValue - currentTopBarHeight.toIntDp()) / (invisibleValue - visibleValue)
        )
    }
}