package com.germainkevin.collapsingtopbar

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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

internal val topBarHorizontalPadding = 4.dp

internal val emptyPaddingValues = PaddingValues()

internal val expandedColumnPaddingValues: @Composable (@Composable (() -> Unit)?, PaddingValues)
-> PaddingValues = { navigationIcon, contentPadding ->
    PaddingValues(
        start =
        if (navigationIcon != null) 56.dp - contentPadding.calculateStartPadding(
            if (LocalLayoutDirection.current == LayoutDirection.Ltr) LayoutDirection.Ltr
            else LayoutDirection.Rtl
        )
        else 16.dp - contentPadding.calculateStartPadding(
            if (LocalLayoutDirection.current == LayoutDirection.Ltr) LayoutDirection.Ltr
            else LayoutDirection.Rtl
        ),
        end = contentPadding.calculateStartPadding(
            if (LocalLayoutDirection.current == LayoutDirection.Ltr) LayoutDirection.Ltr
            else LayoutDirection.Rtl
        )
    )
}

/**
 * [Modifier] when there is a navigation icon provided
 * */
private val navigationIconModifier = Modifier
    .fillMaxHeight()
    .width(56.dp - topBarHorizontalPadding)

/**
 * A way to  remove any floating number from the [Dp] value, and just get the [Int] side of the [Dp]
 * */
internal fun Dp.toIntDp() = this.value.toInt().dp

internal val navigationIconRow: @Composable (
    @Composable (() -> Unit)?, PaddingValues, Boolean
) -> Unit = { navigationIcon, contentPadding, centeredTitleWhenCollapsed ->

    val noIconModifier = Modifier.width(
        16.dp - contentPadding.calculateStartPadding(
            if (LocalLayoutDirection.current == LayoutDirection.Ltr) LayoutDirection.Ltr
            else LayoutDirection.Rtl
        )
    )
    if (navigationIcon == null) Spacer(modifier = noIconModifier)
    else if (centeredTitleWhenCollapsed) Row(
        modifier = Modifier.wrapContentWidth(),
        verticalAlignment = Alignment.Bottom,
        content = { navigationIcon() }
    )
    else Row(
        modifier = navigationIconModifier,
        verticalAlignment = Alignment.Bottom,
        content = { navigationIcon() }
    )
}

internal val collapsedTitle: @Composable (Boolean, Float, @Composable () -> Unit) -> Unit =
    { centeredTitleAndSubtitle, collapsedTitleAlpha, title ->
        val enterAnimation = if (centeredTitleAndSubtitle)
            expandVertically(
                // Expands from bottom to top.
                expandFrom = Alignment.Top
            ) + fadeIn(initialAlpha = collapsedTitleAlpha)
        else fadeIn(initialAlpha = collapsedTitleAlpha)

        val exitAnimation = if (centeredTitleAndSubtitle)
            slideOutVertically() + fadeOut() else fadeOut()
        AnimatedVisibility(
            visible = collapsedTitleAlpha in 0F..1F,
            enter = enterAnimation,
            exit = exitAnimation
        ) { title() }
    }

/**
 * The Section where all the options menu items will be laid out on
 * */
internal val actionsRow: @Composable (@Composable RowScope.() -> Unit) -> Unit = {
    Row(
        modifier = Modifier.fillMaxHeight(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
        content = it
    )
}

/**
 * Will provide us with the current background color of the [CollapsingTopBar].
 * */
@Composable
internal fun CollapsingTopBarScrollBehavior.currentBackgroundColor(
    colors: CollapsingTopBarColors
): State<Color> = animateColorAsState(
    targetValue =
    if (currentTopBarHeight.toIntDp() == collapsedTopBarHeight ||
        currentTopBarHeight.toIntDp() == expandedTopBarMaxHeight
    ) {
        colors.onBackgroundColorChange(colors.backgroundColor)
        colors.backgroundColor
    } else {
        colors.onBackgroundColorChange(colors.backgroundColorWhenCollapsingOrExpanding)
        colors.backgroundColorWhenCollapsingOrExpanding
    }
)

/**
 * Assigns a [CollapsingTopBarState] to [CollapsingTopBarScrollBehavior.currentState]
 * */
internal fun CollapsingTopBarScrollBehavior.defineCurrentState() {
    currentState = when (currentTopBarHeight.toIntDp()) {
        collapsedTopBarHeight -> CollapsingTopBarState.COLLAPSED
        expandedTopBarMaxHeight -> CollapsingTopBarState.EXPANDED
        else -> CollapsingTopBarState.IN_BETWEEN
    }
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
                    currentTopBarHeight = collapsedTopBarHeight
                    ignorePreScrollDetection = false
                    // Making sure the [CollapsingTopBar] can smoothly change height size
                    // Check the [CollapsingTopBarScrollBehavior.nestedScrollConnection] implementation
                    // for better understanding
                    trackOffSetIsZero = 0
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
                    currentTopBarHeight = expandedTopBarMaxHeight
                    ignorePreScrollDetection = false
                    trackOffSetIsZero = 3
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