package com.germainkevin.collapsingtopbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Defines how a [CollapsingTopBar] should behave during a
 * [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll] event.
 * */
interface CollapsingTopBarScrollBehavior {

    /**
     * When set to true, it will make this [CollapsingTopBar] never expand and stay collapsed */
    var isAlwaysCollapsed: Boolean

    /**
     * The height of the CollapsingTopBar when it's collapsed in [Dp]
     * */
    var collapsedTopBarHeight: Dp

    /**
     * The height of the CollapsingTopBar when it's fully expanded in [Dp]
     * */
    var expandedTopBarMaxHeight: Dp

    /**
     * The live height of the [CollapsingTopBar] in [Dp]
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
        if (isAlwaysCollapsed && isExpandedWhenFirstDisplayed) collapsedTopBarHeight
        else if (isAlwaysCollapsed && !isExpandedWhenFirstDisplayed) collapsedTopBarHeight
        else if (!isAlwaysCollapsed && !isExpandedWhenFirstDisplayed) collapsedTopBarHeight
        else if (!isAlwaysCollapsed && isExpandedWhenFirstDisplayed) expandedTopBarMaxHeight
        else expandedTopBarMaxHeight
    )

    override var offsetLimit: Float = (expandedTopBarMaxHeight - collapsedTopBarHeight).value

    override val nestedScrollConnection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

            if (!isAlwaysCollapsed && !isExpandedWhenFirstDisplayed && trackOffSetIsZero >= 3) {
                // Just making sure trackOffSetIsZero doesn't store high numbers, koz it's unnecessary
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
    fun getExpandedColumnAlpha(margin: Dp = 20.dp): State<Float> {
        return animateFloatAsState(
            (currentTopBarHeight - (collapsedTopBarHeight + margin)) /
                    (expandedTopBarMaxHeight - (collapsedTopBarHeight + margin))
        )
    }

    /**
     * Sets the alpha value of the collapsed title section
     * @param visibleValue A value in [Dp] that if [currentTopBarHeight] reaches it, the
     * Collapsed Title should become visible
     * @param invisibleValue A value in [Dp] that if [currentTopBarHeight] reaches it, the
     * Collapsed Title section should become invisible
     * */
    @Composable
    fun getCollapsedTitleAlpha(
        visibleValue: Dp = collapsedTopBarHeight,
        invisibleValue: Dp = collapsedTopBarHeight + 6.dp
    ): State<Float> {
        return animateFloatAsState(
            if (currentTopBarHeight == visibleValue) 1f
            else (visibleValue - currentTopBarHeight) / (invisibleValue - visibleValue)
        )
    }
}