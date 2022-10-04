package com.germainkevin.collapsingtopbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import timber.log.Timber
import kotlin.math.roundToInt
import androidx.compose.ui.input.nestedscroll.nestedScroll

/**
 * Defines how a [CollapsingTopBar] should behave, mainly during a
 * [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll] event.
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
     * Is initially assigned the [NestedScrollConnection.onPreScroll]'s "available" [Offset.y].
     * It's a value that is added to the height of the [CollapsingTopBar] when there is a
     * [nestedScroll] event and [isAlwaysCollapsed] is false.
     * */
    var topBarOffset: Float

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
     * Is incremented inside this [nestedScrollConnection]'s [NestedScrollConnection.onPreScroll]
     * event listener, everytime the [topBarOffset] is equal to 0f. Useful when
     * [isExpandedWhenFirstDisplayed] is set to false, because we want the [CollapsingTopBar] to
     * start changing height only after the first scrolling down event is detected through our
     * [nestedScrollConnection], and because the first scrolling down event tend to be the third
     * time [topBarOffset] is equal to 0f, all we gotta do is check when [topBarOffset] is equal
     * to 0f 3 times, then let [currentTopBarHeight] be equal to
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
     * A [NestedScrollConnection] that should be attached to a
     * [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll] in order to
     * keep track of the scroll events.
     */
    val nestedScrollConnection: NestedScrollConnection

    /**
     * The visibility alpha value of the Column which holds as children the Title and Subtitle
     * */
    val expandedColumnAlphaValue: @Composable () -> State<Float>

    /**
     * The visibility alpha value of the Title displayed and visible when the [CollapsingTopBar] is
     * collapsed
     * */
    val collapsedTitleAlpha: @Composable () -> State<Float>
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


    override var topBarOffset: Float by mutableStateOf(0f)

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
            else -> CollapsingTopBarState.IN_BETWEEN
        }
    )

    override var offsetLimit: Float = (expandedTopBarMaxHeight - collapsedTopBarHeight).value

    override val nestedScrollConnection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (!isAlwaysCollapsed) {
                if (!isExpandedWhenFirstDisplayed && trackOffSetIsZero >= 3) {
                    currentTopBarHeight = expandedTopBarMaxHeight + topBarOffset.roundToInt().dp
                } else if (isExpandedWhenFirstDisplayed) {
                    currentTopBarHeight = expandedTopBarMaxHeight + topBarOffset.roundToInt().dp
                }

                defineCurrentState()

                val availableY = available.y.toInt()
                val newOffset = (topBarOffset + availableY)
                val coerced = newOffset.coerceIn(minimumValue = -offsetLimit, maximumValue = 0f)
                topBarOffset = coerced

                if (topBarOffset == 0f) {
                    trackOffSetIsZero += 1
                }

                // Just keeping trackOffSetIsZero from storing high numbers that are above 3
                if (trackOffSetIsZero > 6) {
                    trackOffSetIsZero = 3
                }
            }

            return Offset.Zero
        }
    }

    override val expandedColumnAlphaValue: @Composable () -> State<Float> = {
        getExpandedColumnAlpha()
    }

    override val collapsedTitleAlpha: @Composable () -> State<Float> = {
        getCollapsedTitleAlpha()
    }

    /**
     * In order to know when the "title subtitle column" should have a 1f alpha visibility or 0f alpha
     * visibility or a alpha value between 0f and 1f, we need to base that alpha value on the
     * [currentTopBarHeight] of the [CollapsingTopBar].
     *
     * So in this sense when the [currentTopBarHeight] of the [CollapsingTopBar]
     * is [collapsedTopBarHeight] + [margin] then  the "title subtitle column" should be invisible
     * or alpha = 0f, and when the [CollapsingTopBar]'s [currentTopBarHeight] is
     * [expandedTopBarMaxHeight] then the "title subtitle column" should be visible or alpha = 1f.
     *
     * But we also want the "title subtitle column"'s alpha value to be between 0f and 1f when the
     * [CollapsingTopBar]'s [currentTopBarHeight] is between
     * [collapsedTopBarHeight] + [margin] and [expandedTopBarMaxHeight]
     *
     * We'll reference [collapsedTopBarHeight] as 56 (Dp) and [expandedTopBarMaxHeight] as 200 (Dp), and
     * the [margin] as 20 (Dp)
     *
     * 56 + 20  --------> 0f (alpha value meaning  when the title subtitle column is fully invisible)
     *
     * 200      --------> 1f (alpha value meaning when the title subtitle column is fully visible)
     *
     * The distance between [expandedTopBarMaxHeight] - ([collapsedTopBarHeight] + [margin])
     * << A distance which we will label as 124 (Dp) because (200 - (56+20) = 124) >>,
     * is going to be the 100% distance from making the "title subtitle column" fully visible (100%) or
     * alpha =1f and fully invisible (0%) or alpha = 0f, or in between (0%..100%) 0.0f to 1.0f.
     *
     * So what we do is:
     * 124                                ----------> 100%
     *
     * currentTopBarHeight's actual value -------------> alphaValue
     *
     * Whatever value alphaValue is, is considered the level of visibility the "title subtitle column"
     * should have
     *
     * @param margin Making sure that the 'title subtitle column" become visible once the
     * [currentTopBarHeight] reaches past [collapsedTopBarHeight] + [margin]
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
        visibleValue: Dp = collapsedTopBarHeight.value.toInt().dp,
        invisibleValue: Dp = collapsedTopBarHeight + 6.dp
    ): State<Float> {
        return animateFloatAsState(
            if (currentTopBarHeight.toIntDp() == visibleValue) 1f
            else (visibleValue - currentTopBarHeight) / (invisibleValue - visibleValue)
        )
    }
}