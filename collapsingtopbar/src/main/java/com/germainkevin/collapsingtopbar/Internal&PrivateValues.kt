package com.germainkevin.collapsingtopbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * The default collapsed height of the [CollapsingTopBar]
 * */
internal val defaultMinimumTopBarHeight = 56.dp

/**
 * The default expanded height of the [CollapsingTopBar]
 * */
internal val defaultMaximumTopBarHeight = 156.dp

internal val TopBarHorizontalPadding = 4.dp

internal val DefaultCollapsingTopBarElevation = 0.dp

// Used to add spacing when the title is next to the navigation icon.
internal val TopBarTitleInset = 16.dp

/**
 * A way to  remove any floating number from the [Dp] value, and just get the [Int] side of the [Dp]
 * */
internal fun Dp.toIntDp() = this.value.toInt().dp

/**
 * In order to know the alpha value between 0F and 1F of the "expandedTitle subtitle column",
 * we will use the height of the [CollapsingTopBar] which is the
 * [CollapsingTopBarScrollBehavior.currentTopBarHeight].
 *
 *
 * We want that whenever the
 * [CollapsingTopBarScrollBehavior.currentTopBarHeight] is exactly equal to the value of
 * [CollapsingTopBarScrollBehavior.collapsedTopBarHeight] + [margin]
 * that the "expandedTitle subtitle Column" become fully invisible or alpha = 0F
 *
 *
 * But, when the [CollapsingTopBarScrollBehavior.currentTopBarHeight] is exactly equal to the value
 * of [CollapsingTopBarScrollBehavior.expandedTopBarMaxHeight] that the
 * "expandedTitle subtitle Column" become fully visible or alpha = 1F.
 *
 *
 * So in this sense, the 0F and 1F alpha value of the "expandedTitle subtitle Column" are:
 *
 *
 * val fullyInvisibleValue: Dp = [CollapsingTopBarScrollBehavior.collapsedTopBarHeight] + [margin]
 *
 *
 * val fullyVisibleValue: Dp =
 * [CollapsingTopBarScrollBehavior.expandedTopBarMaxHeight] - (fullyInvisibleValue)
 *
 *
 * @param margin When [CollapsingTopBarScrollBehavior.collapsedTopBarHeight] + [margin] is the
 * current value of [CollapsingTopBarScrollBehavior.currentTopBarHeight], it will trigger the
 * "expandedTitle subtitle Column"'s alpha value to be equal to 0F.
 */
@Composable
internal fun CollapsingTopBarScrollBehavior.getExpandedColumnAlpha(margin: Dp = 20.dp): State<Float> {
    return animateFloatAsState(
        (currentTopBarHeight - (collapsedTopBarHeight + margin)) /
                (expandedTopBarMaxHeight - (collapsedTopBarHeight + margin))
    )
}

/**
 * Sets the alpha value of the collapsed title section
 * @param visibleValue A value in [Dp] that if [CollapsingTopBarScrollBehavior.currentTopBarHeight]
 * reaches it, the Collapsed Title should become visible.
 * @param invisibleValue The value at which the collapsed Title section should become invisible
 * when its [CollapsingTopBarScrollBehavior.currentTopBarHeight] reaches it
 * */
@Composable
internal fun CollapsingTopBarScrollBehavior.getCollapsedTitleAlpha(
    visibleValue: Dp = collapsedTopBarHeight.toIntDp(),
    invisibleValue: Dp = (collapsedTopBarHeight + 15.dp).toIntDp()
): State<Float> {
    return animateFloatAsState(
        if (currentTopBarHeight.toIntDp() == visibleValue) 1f
        else (visibleValue - currentTopBarHeight.toIntDp()) / (invisibleValue - visibleValue),
        label = "The alpha value of the Collapsed Topbar's title composable"
    )
}

/**
 * Will change the value of [CollapsingTopBarScrollBehavior.heightOffset]
 * depending on the vertical scroll events it detects from [available]
 * */
private fun CollapsingTopBarScrollBehavior.trackPreScrollData(available: Offset) {
    val availableY = available.y.toInt()
    val newOffset = (heightOffset + availableY)
    val coerced = newOffset.coerceIn(minimumValue = -heightOffsetLimit, maximumValue = 0f)
    heightOffset = coerced
}

private fun CollapsingTopBarScrollBehavior.countWhenHeightOffsetIsZero() {
    if (heightOffset == 0f) {
        countWhenHeightOffSetIsZero += 1
    }
}

/**
 * We only need to update the
 * [currentTopBarHeight][CollapsingTopBarScrollBehavior.currentTopBarHeight] when
 * [countWhenHeightOffSetIsZero][CollapsingTopBarScrollBehavior.countWhenHeightOffSetIsZero]
 * is below, equal or above 3, in that sense, once it surpasses 3, no need to track over that
 * number, so we reinitialize it to 3 once its value is 6
 * */
private fun CollapsingTopBarScrollBehavior.limitCountBelow6() {
    if (countWhenHeightOffSetIsZero > 6) {
        countWhenHeightOffSetIsZero = 3
    }
}

/**
 * @param available The scroll activity that we get from
 * [CollapsingTopBarScrollBehavior.nestedScrollConnection]'s onPreScroll
 *
 *
 * Will collapse or expand depending on the vertical scroll events it detects from [available]
 * */
internal fun CollapsingTopBarScrollBehavior.onPreScrollDefaultBehavior(available: Offset) {
    if (!isAlwaysCollapsed && !ignorePreScrollDetection) {
        countWhenHeightOffsetIsZero()
        limitCountBelow6()
        trackPreScrollData(available)
        val newHeight = expandedTopBarMaxHeight + heightOffset.roundToInt().dp
        if (!isExpandedWhenFirstDisplayed && countWhenHeightOffSetIsZero >= 3) {
            currentTopBarHeight = newHeight
        } else if (isExpandedWhenFirstDisplayed) {
            currentTopBarHeight = newHeight
        }
    }
}

/**
 * @param firstVisibleItemScrollOffset Based on the value passed, we'll be able to determine
 * whether the [CollapsingTopBar] should expand or stay collapsed.
 * @param available The scroll coordinates that we get from
 * [CollapsingTopBarScrollBehavior.nestedScrollConnection]'s onPreScroll
 * */
internal fun CollapsingTopBarScrollBehavior.onPreScrollAppBarScrollingViewBehavior(
    available: Offset,
    firstVisibleItemScrollOffset: Int
) {
    if (!isAlwaysCollapsed && !ignorePreScrollDetection) {
        countWhenHeightOffsetIsZero()
        limitCountBelow6()
        if (!isExpandedWhenFirstDisplayed && countWhenHeightOffSetIsZero >= 3) {
            updateHeight(available, firstVisibleItemScrollOffset)
        } else if (isExpandedWhenFirstDisplayed) {
            updateHeight(available, firstVisibleItemScrollOffset)
        }
    }
}

/**
 * The logic for changing the height of the [CollapsingTopBar] depending on the value of the
 * [firstVisibleItemScrollOffset] and the [CollapsingTopBarState] of the [CollapsingTopBar]
 * @param available The scroll coordinates that we get from
 * [CollapsingTopBarScrollBehavior.nestedScrollConnection]'s onPreScroll
 * */
private fun CollapsingTopBarScrollBehavior.updateHeight(
    available: Offset,
    firstVisibleItemScrollOffset: Int
) {
    if (firstVisibleItemScrollOffset == 0 && isCollapsed) {
        val newHeight = expandedTopBarMaxHeight + heightOffset.roundToInt().dp
        if (newHeight == collapsedTopBarHeight) {
            trackPreScrollData(available)
            currentTopBarHeight = newHeight
        } else {
            expand()
        }
    } else if (!isCollapsed) {
        trackPreScrollData(available)
        currentTopBarHeight = expandedTopBarMaxHeight + heightOffset.roundToInt().dp
    }
}

/**
 * Will provide us the current background color of the [CollapsingTopBar].
 * */
@Composable
internal fun CollapsingTopBarScrollBehavior.currentBackgroundColor(
    colors: CollapsingTopBarColors
): State<Color> = animateColorAsState(
    targetValue = if (currentTopBarHeight.toIntDp() == collapsedTopBarHeight ||
        currentTopBarHeight.toIntDp() == expandedTopBarMaxHeight
    ) {
        colors.backgroundColor
    } else {
        colors.backgroundColorWhenCollapsingOrExpanding
    }, label = "current background color of CTB"
)

/**
 * Assigns a [CollapsingTopBarState] to [CollapsingTopBarScrollBehavior.currentState]
 * */
internal fun CollapsingTopBarScrollBehavior.defineCurrentState() {
    currentState = when (currentTopBarHeight.toIntDp()) {
        collapsedTopBarHeight -> {
            CollapsingTopBarState.COLLAPSED
        }

        expandedTopBarMaxHeight -> {
            CollapsingTopBarState.EXPANDED
        }

        else -> {
            CollapsingTopBarState.MOVING
        }
    }
    isCollapsed = currentState == CollapsingTopBarState.COLLAPSED
    isMoving = currentState == CollapsingTopBarState.MOVING
    isExpanded = currentState == CollapsingTopBarState.EXPANDED
}