package com.germainkevin.collapsingtopbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import timber.log.Timber
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
internal val TopBarTitleInset = 16.dp - TopBarHorizontalPadding

/**
 * A way to  remove any floating number from the [Dp] value, and just get the [Int] side of the [Dp]
 * */
internal fun Dp.toIntDp() = this.value.toInt().dp


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
internal fun CollapsingTopBarScrollBehavior.getExpandedColumnAlpha(margin: Dp = 20.dp): State<Float> {
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
internal fun CollapsingTopBarScrollBehavior.getCollapsedTitleAlpha(
    visibleValue: Dp = collapsedTopBarHeight.toIntDp(),
    invisibleValue: Dp = (collapsedTopBarHeight + 15.dp).toIntDp()
): State<Float> {
    return animateFloatAsState(
        if (currentTopBarHeight.toIntDp() == visibleValue) 1f
        else (visibleValue - currentTopBarHeight.toIntDp()) / (invisibleValue - visibleValue)
    )
}

internal fun CollapsingTopBarScrollBehavior.trackPreScrollDataAltBehavior(available: Offset) {
    val availableY = available.y.toInt()
    val newOffset = ((heightOffset + availableY) * 0.2).toFloat()
    val coerced = newOffset.coerceIn(minimumValue = -heightOffsetLimit, maximumValue = 0f)
    heightOffset = coerced
}

internal fun CollapsingTopBarScrollBehavior.trackPreScrollDataDefaultBehavior(available: Offset) {
    val availableY = available.y.toInt()
    val newOffset = (heightOffset + availableY)
    val coerced = newOffset.coerceIn(minimumValue = -heightOffsetLimit, maximumValue = 0f)
    heightOffset = coerced
}

internal fun CollapsingTopBarScrollBehavior.incrementTopBarOffset() {
    if (heightOffset == 0f) {
        countWhenHeightOffSetIsZero += 1
    }
}

// Just keeping countWhenHeightOffSetIsZero from storing high numbers that are above 3
internal fun CollapsingTopBarScrollBehavior.plateauTopBarOffset() {
    if (countWhenHeightOffSetIsZero > 6) {
        countWhenHeightOffSetIsZero = 3
    }
}

internal fun CollapsingTopBarScrollBehavior.onPreScrollDefaultBehavior(available: Offset) {
    if (!isAlwaysCollapsed && !ignorePreScrollDetection) {
        incrementTopBarOffset()
        plateauTopBarOffset()
        trackPreScrollDataDefaultBehavior(available)
        val newHeight = expandedTopBarMaxHeight + heightOffset.roundToInt().dp
        if (!isExpandedWhenFirstDisplayed && countWhenHeightOffSetIsZero >= 3) {
            currentTopBarHeight = newHeight
        } else if (isExpandedWhenFirstDisplayed) {
            currentTopBarHeight = newHeight
        }
        defineCurrentState()
    }
}

internal fun CollapsingTopBarScrollBehavior.onPreScrollLazyColumnUnderTopBarBehavior(
    available: Offset,
    mUserLazyListState: LazyListState
) {
    if (!isAlwaysCollapsed && !ignorePreScrollDetection) {
        incrementTopBarOffset()
        plateauTopBarOffset()
        if (!isExpandedWhenFirstDisplayed && countWhenHeightOffSetIsZero >= 3) {
            updateToBarHeightForLazyColumnBehavior(available, mUserLazyListState)
        } else if (isExpandedWhenFirstDisplayed) {
            updateToBarHeightForLazyColumnBehavior(available, mUserLazyListState)
        }
        defineCurrentState()
    }
}

/**
 * The logic for changing the height of the [CollapsingTopBar] when there is a LazyColumn under
 * the [CollapsingTopBar]
 * */
internal fun CollapsingTopBarScrollBehavior.updateToBarHeightForLazyColumnBehavior(
    available: Offset,
    mUserLazyListState: LazyListState
) {
    if (mUserLazyListState.firstVisibleItemScrollOffset == 0 && isCollapsed) {
        val newHeight = expandedTopBarMaxHeight + heightOffset.roundToInt().dp
        if (newHeight == collapsedTopBarHeight) {
            trackPreScrollDataDefaultBehavior(available)
            currentTopBarHeight = newHeight
        } else {
            expand()
        }
    } else if (!isCollapsed) {
        trackPreScrollDataDefaultBehavior(available)
        currentTopBarHeight = expandedTopBarMaxHeight + heightOffset.roundToInt().dp
    }
}

/**
 * Will provide us with the current background color of the [CollapsingTopBar].
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
    }
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

private var collapseJob: Job? = null
private var expandJob: Job? = null

/**
 * Expands the [CollapsingTopBar]
 * @param delay How often in milliseconds should the
 * [currentTopBarHeight][CollapsingTopBarScrollBehavior.currentTopBarHeight] subtract [steps] to
 * itself until it reaches the size of
 * [collapsedTopBarHeight][CollapsingTopBarScrollBehavior.collapsedTopBarHeight]
 * @param steps How many [Dp]s should the
 * [currentTopBarHeight][CollapsingTopBarScrollBehavior.currentTopBarHeight] subtract to itself
 * every [delay] until it reaches the size of
 * [collapsedTopBarHeight][CollapsingTopBarScrollBehavior.collapsedTopBarHeight]
 * @param coroutineScope The [CoroutineScope] on which the calculations will occur
 * @param onFinishedCollapsing Called when the
 * [currentTopBarHeight][CollapsingTopBarScrollBehavior.currentTopBarHeight]
 * has reached the value of
 * [collapsedTopBarHeight][CollapsingTopBarScrollBehavior.collapsedTopBarHeight]
 * */
fun CollapsingTopBarScrollBehavior.collapse(
    delay: Long = 10L,
    steps: Dp = 5.dp,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    onFinishedCollapsing: () -> Unit = {}
) {
    if (!isAlwaysCollapsed) {
        ignorePreScrollDetection = true
        collapseJob?.cancel()
        collapseJob = coroutineScope.launch {
            val ascendingDistance: IntRange =
                collapsedTopBarHeight.value.toInt()..expandedTopBarMaxHeight.value.toInt()
            val descendingDistance = ascendingDistance.sortedDescending()

            for (currentHeight in descendingDistance) {
                val valueDecreasedTo = currentTopBarHeight - steps
                if (valueDecreasedTo <= collapsedTopBarHeight) {
                    heightOffset = -heightOffsetLimit
                    currentTopBarHeight = expandedTopBarMaxHeight + heightOffset.dp
                    ignorePreScrollDetection = false
                    // Making sure the [CollapsingTopBar] can smoothly change height size
                    // Check the [CollapsingTopBarScrollBehavior.nestedScrollConnection]
                    // implementation for better understanding
                    countWhenHeightOffSetIsZero = 0
                    defineCurrentState()
                    onFinishedCollapsing()
                }
                if (currentTopBarHeight - steps > collapsedTopBarHeight) {
                    currentTopBarHeight = valueDecreasedTo
                    defineCurrentState()
                    delay(delay)
                }
            }
        }
    }
}

/**
 * Expands the [CollapsingTopBar]
 * @param delay How often in milliseconds should the
 * [currentTopBarHeight][CollapsingTopBarScrollBehavior.currentTopBarHeight] add [steps] to itself
 * until it reaches the size of
 * [expandedTopBarMaxHeight][CollapsingTopBarScrollBehavior.expandedTopBarMaxHeight]
 * @param steps How many [Dp]s should the
 * [currentTopBarHeight][CollapsingTopBarScrollBehavior.currentTopBarHeight] add to itself every
 * [delay] until it reaches the size of
 * [expandedTopBarMaxHeight][CollapsingTopBarScrollBehavior.expandedTopBarMaxHeight]
 * @param coroutineScope The [CoroutineScope] on which the calculations will occur
 * @param onFinishedExpanding Called when the
 * [currentTopBarHeight][CollapsingTopBarScrollBehavior.currentTopBarHeight]
 * has reached the value of
 * [expandedTopBarMaxHeight][CollapsingTopBarScrollBehavior.expandedTopBarMaxHeight]
 * */
fun CollapsingTopBarScrollBehavior.expand(
    delay: Long = 10L,
    steps: Dp = 5.dp,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    onFinishedExpanding: () -> Unit = {}
) {
    if (!isAlwaysCollapsed) {
        ignorePreScrollDetection = true
        expandJob?.cancel()
        expandJob = coroutineScope.launch {
            val ascendingDistance: IntRange =
                collapsedTopBarHeight.value.toInt()..expandedTopBarMaxHeight.value.toInt()
            for (currentHeight in ascendingDistance) {
                val valueIncreasedTo = currentTopBarHeight + steps
                if (valueIncreasedTo >= expandedTopBarMaxHeight) {
                    heightOffset = expandedTopBarMaxHeight.value
                    currentTopBarHeight = heightOffset.dp
                    ignorePreScrollDetection = false
                    countWhenHeightOffSetIsZero = 3
                    defineCurrentState()
                    onFinishedExpanding()
                }
                if (valueIncreasedTo < expandedTopBarMaxHeight) {
                    currentTopBarHeight = valueIncreasedTo
                    defineCurrentState()
                    delay(delay)
                }
            }
        }
    }
}

private fun CollapsingTopBarScrollBehavior.expandWithoutNotifyingState(
    delay: Long = 10L,
    steps: Dp = 5.dp,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    onFinishedExpanding: () -> Unit = {}
) {
    if (!isAlwaysCollapsed) {
        ignorePreScrollDetection = true
        expandJob?.cancel()
        expandJob = coroutineScope.launch {
            val ascendingDistance: IntRange =
                collapsedTopBarHeight.value.toInt()..expandedTopBarMaxHeight.value.toInt()
            for (currentHeight in ascendingDistance) {
                val valueIncreasedTo = currentTopBarHeight + steps
                if (valueIncreasedTo >= expandedTopBarMaxHeight) {
                    heightOffset = expandedTopBarMaxHeight.value
                    currentTopBarHeight = heightOffset.dp
                    ignorePreScrollDetection = false
                    countWhenHeightOffSetIsZero = 3
                    onFinishedExpanding()
                }
                if (valueIncreasedTo < expandedTopBarMaxHeight) {
                    currentTopBarHeight = valueIncreasedTo
                    delay(delay)
                }
            }
        }
    }
}