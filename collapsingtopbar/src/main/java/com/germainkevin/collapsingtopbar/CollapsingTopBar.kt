package com.germainkevin.collapsingtopbar

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * [CollapsingTopBar] is like a [TopAppBar] that can collapse and/or expand.
 *
 * This [CollapsingTopBar] has slots to display a navigation icon, a title, a subtitle,
 * a mainAction and actions.
 *
 * @param modifier A modifier that is passed down to the main layer which is a [Surface]
 * @param navigationIcon The navigation icon displayed at the start of the [CollapsingTopBar].
 * @param title The title to be displayed inside the [CollapsingTopBar]
 * @param expandedTitle Optional, In case you want the [title] to look different when the
 * [CollapsingTopBar] is expanded
 * @param subtitle The subtitle to be displayed inside the [CollapsingTopBar]
 * This should typically be an [IconButton] or [IconToggleButton].
 * @param mainAction A composable that is added before [actions] that animates as you collapse or
 * expand the [CollapsingTopBar]
 * @param actions The actions displayed at the end of the [CollapsingTopBar]. This should typically
 * be [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param colors [CollapsingTopBarColors] that will be used to resolve the colors used for this
 * [CollapsingTopBar]. See [CollapsingTopBarDefaults.colors].
 * @param elevation The size of the shadow below the [Surface]
 * @param scrollBehavior determines the behavior of the [CollapsingTopBar]. If you want the
 * [CollapsingTopBar] to stay collapsed, you set it there, if you want the [CollapsingTopBar] to
 * have a different [collapsed height][CollapsingTopBarcollapsedTopBarHeight] or
 * a different [expanded height][CollapsingTopBarexpandedTopBarMaxHeight], you set it
 * there, if you want the [CollapsingTopBar] to detect when a scroll event has occurred in your UI
 * and you want the [CollapsingTopBar] to collapse or expand, you simply pass
 * [nestedScrollConnection][CollapsingTopBarnestedScrollConnection] to
 * your Layout's [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll].
 * @author Germain Kevin
 * */
@Composable
fun CollapsingTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    expandedTitle: @Composable () -> Unit = title,
    subtitle: @Composable () -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null,
    mainAction: @Composable () -> Unit = { },
    actions: @Composable RowScope.() -> Unit = {},
    colors: CollapsingTopBarColors = CollapsingTopBarDefaults.colors(),
    scrollBehavior: CollapsingTopBarScrollBehavior,
    elevation: Dp = DefaultCollapsingTopBarElevation,
) {
    CollapsingTopBarLayout(
        modifier = modifier,
        title = title,
        expandedTitle = expandedTitle,
        subtitle = subtitle,
        navigationIcon = navigationIcon,
        mainAction = mainAction,
        actions = actions,
        elevation = elevation,
        scrollBehavior = scrollBehavior,
        colors = colors
    )
}

@Composable
private fun CollapsingTopBarLayout(
    modifier: Modifier,
    title: @Composable () -> Unit,
    expandedTitle: @Composable () -> Unit,
    subtitle: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)?,
    mainAction: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    scrollBehavior: CollapsingTopBarScrollBehavior,
    colors: CollapsingTopBarColors,
    elevation: Dp,
) = with(scrollBehavior) {
    val scrollState = rememberScrollState()
    val surfaceColor = scrollBehavior.currentBackgroundColor(colors = colors)
    colors.onBackgroundColorChange(surfaceColor.value)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(currentTopBarHeight)
            .verticalScroll(scrollState),
        color = surfaceColor.value,
        contentColor = colors.contentColor,
        elevation = elevation,
    ) {
        Box {
            val expandedColumnModifier = if (centeredTitleAndSubtitle) {
                Modifier
                    .alpha(expandedColumnAlpha.invoke().value)
                    .fillMaxWidth()
                    .height(currentTopBarHeight)
                    .padding(horizontal = TopBarHorizontalPadding * 4)
            } else {
                Modifier
                    .alpha(expandedColumnAlpha.invoke().value)
                    .wrapContentWidth()
                    .height(currentTopBarHeight)
                    .align(Alignment.TopStart)
                    .padding(
                        PaddingValues(
                            start = if (navigationIcon != null) 56.dp - TopBarHorizontalPadding
                            else TopBarTitleInset,
                            end = TopBarHorizontalPadding,
                        )
                    )
            }

            val horizontalAlignment =
                if (centeredTitleAndSubtitle) Alignment.CenterHorizontally else Alignment.Start

            SimpleColumn(
                modifier = expandedColumnModifier,
                horizontalAlignment = horizontalAlignment,
            ) {
                SimpleColumn(
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment = horizontalAlignment,
                ) {
                    expandedTitle()
                    subtitle()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            val titleEnterAnimation = if (centeredTitleAndSubtitle) {
                fadeIn(initialAlpha = collapsedTitleAlpha.invoke().value) + expandVertically(
                    expandFrom = Alignment.Bottom
                )
            } else fadeIn(initialAlpha = collapsedTitleAlpha.invoke().value)

            val titleExitAnimation = if (centeredTitleAndSubtitle)
                slideOutVertically() + fadeOut() else fadeOut()

            // Wrap the given actions icons in a Row.
            val actionsRow = @Composable {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }

            val collapsedTopBarContent = @Composable {
                Box(
                    Modifier
                        .layoutId("navigationIcon")
                        .padding(start = TopBarHorizontalPadding)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.contentColor,
                        content = navigationIcon ?: {}
                    )
                }
                Box(
                    modifier = Modifier
                        .layoutId("title")
                        .padding(horizontal = TopBarHorizontalPadding),
                ) {
                    AnimatedVisibility(
                        visible = collapsedTitleAlpha.invoke().value in 0F..1F,
                        enter = titleEnterAnimation,
                        exit = titleExitAnimation,
                        content = { title() }
                    )
                }
                Box(
                    Modifier
                        .layoutId("mainAction")
                        .padding(start = TopBarHorizontalPadding)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.contentColor,
                        content = mainAction
                    )
                }
                Box(
                    Modifier
                        .layoutId("actionIcons")
                        .padding(end = TopBarHorizontalPadding)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides colors.contentColor,
                        content = actionsRow
                    )
                }
            }

            CollapsedTopBar(
                modifier = Modifier.align(Alignment.BottomStart),
                collapsedTopBarContent = collapsedTopBarContent,
                scrollBehavior = scrollBehavior,
            )
        }
    }
}

/**
 * A simple version of [Column] created to hold the expanded title and subtitle
 * @param modifier A modifier that is passed down to mainly to help determine the
 * max width and height of the Layout
 * @param content The content that is passed to the layout to be linearly and vertically positioned
 * @param verticalArrangement How to arrange the [content]s vertically inside the [Layout]
 * @param horizontalAlignment How to arrange to [content]s horizontally inside the [Layout]
 * @author Germain Kevin
 * */
@Composable
private inline fun SimpleColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = simpleColumnMeasurePolicy(
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        )
    )
}

/**
 * A [Layout] that draws horizontally a [navigationIcon], a [title], a [mainAction] and [actions]
 * @author Germain Kevin
 * */
@Composable
private fun CollapsedTopBar(
    modifier: Modifier = Modifier,
    collapsedTopBarContent: @Composable () -> Unit,
    scrollBehavior: CollapsingTopBarScrollBehavior,
) {
    val collapsedTopBarHeight = LocalDensity.current.run {
        scrollBehavior.collapsedTopBarHeight.toPx().toInt()
    }
    Layout(
        content = collapsedTopBarContent,
        modifier = modifier,
        measurePolicy = collapsedTopBarMeasurePolicy(
            collapsedTopBarHeight = collapsedTopBarHeight,
            scrollBehavior = scrollBehavior
        )
    )
}

/**
 * The measuring logic for the [CollapsedTopBar]. This lays out its contents in a horizontal way,
 * one after the other, quite similarly the same way the Compose Row layout does.
 * @author Germain Kevin
 * */
private fun collapsedTopBarMeasurePolicy(
    collapsedTopBarHeight: Int,
    scrollBehavior: CollapsingTopBarScrollBehavior,
): MeasurePolicy {
    return MeasurePolicy { measurables, constraints ->
        val navigationIconPlaceable =
            measurables.first { it.layoutId == "navigationIcon" }.measure(constraints)
        val mainActionIconPlaceable =
            measurables.first { it.layoutId == "mainAction" }.measure(constraints)
        val actionIconsPlaceable =
            measurables.first { it.layoutId == "actionIcons" }.measure(constraints)

        val maxTitleWidth = if (constraints.maxWidth == Constraints.Infinity) {
            constraints.maxWidth
        } else {
            (constraints.maxWidth - navigationIconPlaceable.width -
                    mainActionIconPlaceable.width - actionIconsPlaceable.width)
                .coerceAtLeast(0)
        }

        val titlePlaceable = measurables.first { it.layoutId == "title" }.measure(
            constraints.copy(minWidth = 0, maxWidth = maxTitleWidth)
        )

        val navIconYPosition = collapsedTopBarHeight - navigationIconPlaceable.height
        val titleYPosition = collapsedTopBarHeight - titlePlaceable.height
        val mainActionYPosition = collapsedTopBarHeight - mainActionIconPlaceable.height
        val actionsYPosition = collapsedTopBarHeight - actionIconsPlaceable.height

        val horizontalPaddingPx = TopBarHorizontalPadding.toPx()

        val placeTitleAtStart = max(TopBarTitleInset.roundToPx(), navigationIconPlaceable.width)
        val placeTitleInCenter = if (mainActionIconPlaceable.width > horizontalPaddingPx
            && actionIconsPlaceable.width > horizontalPaddingPx
        ) {
            (constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width) / 2
        } else {
            (constraints.maxWidth - titlePlaceable.width) / 2
        }

        val mainActionFixedXPosition =
            constraints.maxWidth - actionIconsPlaceable.width - mainActionIconPlaceable.width

        val currentHeight = scrollBehavior.currentTopBarHeight.roundToPx()
        val currentExpandedHeight = scrollBehavior.expandedTopBarMaxHeight.roundToPx()

        val mainActionInCenter = (constraints.maxWidth - actionIconsPlaceable.width) / 2

        val mainActionX: Int = if (!scrollBehavior.isCollapsed) {
            val xOffset = mainActionFixedXPosition - currentHeight
            if (xOffset > mainActionInCenter && currentHeight != currentExpandedHeight) xOffset
            else mainActionInCenter
        } else {
            mainActionFixedXPosition
        }

        layout(constraints.maxWidth, collapsedTopBarHeight) {

            navigationIconPlaceable.placeRelative(x = 0, y = navIconYPosition)

            titlePlaceable.placeRelative(
                x = if (scrollBehavior.centeredTitleWhenCollapsed) placeTitleInCenter else placeTitleAtStart,
                y = titleYPosition / 2
            )

            mainActionIconPlaceable.placeRelative(x = mainActionX, y = mainActionYPosition)

            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = actionsYPosition
            )
        }
    }
}

/**
 * The measuring logic for the [SimpleColumn]. This lays out its content in a vertical way, one
 * after the other, quite similarly the same way the Compose Column layout does.
 * @author Germain Kevin
 * */
private fun simpleColumnMeasurePolicy(
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
): MeasurePolicy {

    return MeasurePolicy { measurables, constraints ->

        val placeables = measurables.map { measurable -> measurable.measure(constraints) }

        val largestPlaceable = placeables.maxOf { it.width }

        val layoutMaxWidth =
            if (largestPlaceable > constraints.maxWidth) constraints.maxWidth else largestPlaceable

        val layoutMaxLength = placeables.sumOf { it.height }

        layout(layoutMaxWidth, layoutMaxLength) {
            var yPosition = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(
                    x = when (horizontalAlignment) {
                        Alignment.Start -> 0
                        else -> (largestPlaceable - placeable.width) / 2
                    },
                    y = when (verticalArrangement) {
                        Arrangement.Center -> {
                            (constraints.maxHeight - layoutMaxLength) / 2
                        }
                        else -> yPosition
                    }
                )
                yPosition += placeable.height
            }
        }
    }
}
