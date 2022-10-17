package com.germainkevin.collapsingtopbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

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