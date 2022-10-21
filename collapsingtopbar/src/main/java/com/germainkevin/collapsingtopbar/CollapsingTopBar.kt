package com.germainkevin.collapsingtopbar

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
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
 * @param modifier A modifier that is passed down to the main layer which is a [Surface].
 * @param navigationIcon The navigation icon displayed at the start of the [CollapsingTopBar].
 * @param title The title to be displayed inside the [CollapsingTopBar]
 * @param expandedTitle Optional, In case you want the [title] to look different when the
 * [CollapsingTopBar] is expanded
 * @param subtitle The subtitle to be displayed inside the [CollapsingTopBar]
 * @param mainAction A composable that is added before [actions] that animates as you collapse or
 * expand the [CollapsingTopBar]. This should typically be an [IconButton] or [IconToggleButton].
 * @param actions The actions displayed at the end of the [CollapsingTopBar]. This should typically
 * be [IconButton]s or [IconToggleButton]s. It will be laid out as a [Row], so icons inside will be
 * placed horizontally.
 * @param colors [CollapsingTopBarColors] that will be used to resolve the colors used for this
 * [CollapsingTopBar]. See [CollapsingTopBarDefaults.colors].
 * @param elevation The size of the shadow below the main layer which is a [Surface].
 * @param scrollBehavior determines the behavior of the [CollapsingTopBar]. If you want the
 * [CollapsingTopBar] to stay collapsed, you set it there, if you want the [CollapsingTopBar] to
 * have a different [collapsed height][CollapsingTopBarScrollBehavior.collapsedTopBarHeight] or
 * a different [expanded height][CollapsingTopBarScrollBehavior.expandedTopBarMaxHeight], you set it
 * there, if you want the [CollapsingTopBar] to detect when a scroll event has occurred in your UI
 * and you want the [CollapsingTopBar] to collapse or expand, you simply pass
 * [nestedScrollConnection][CollapsingTopBarScrollBehavior.nestedScrollConnection] to
 * your Layout's [Modifier.nestedScroll][androidx.compose.ui.input.nestedscroll.nestedScroll].
 * @author Germain Kevin
 * */
@Composable
fun CollapsingTopBar(
    modifier: Modifier = Modifier,
    scrollBehavior: CollapsingTopBarScrollBehavior,
    navigationIcon: @Composable () -> Unit = { },
    title: @Composable () -> Unit,
    expandedTitle: @Composable () -> Unit = title,
    subtitle: @Composable () -> Unit = {},
    mainAction: @Composable () -> Unit = { },
    actions: @Composable RowScope.() -> Unit = {},
    colors: CollapsingTopBarColors = CollapsingTopBarDefaults.colors(),
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
    navigationIcon: @Composable () -> Unit,
    mainAction: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    scrollBehavior: CollapsingTopBarScrollBehavior,
    colors: CollapsingTopBarColors,
    elevation: Dp,
) = with(scrollBehavior) {

    val surfaceColor by currentBackgroundColor(colors)
    colors.onBackgroundColorChange(surfaceColor)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(currentTopBarHeight)
            .verticalScroll(topBarVerticalScrollState.invoke()),
        color = surfaceColor,
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
                            start = if (navigationIcon != {}) 56.dp - TopBarHorizontalPadding
                            else TopBarTitleInset,
                            end = TopBarHorizontalPadding,
                        )
                    )
            }

            val horizontalAlignment =
                if (centeredTitleAndSubtitle) Alignment.CenterHorizontally else Alignment.Start

            SimpleColumnWithTitleSubtitle(
                modifier = expandedColumnModifier,
                horizontalAlignment = horizontalAlignment,
                expandedTitle = expandedTitle,
                subtitle = subtitle
            )

            val titleEnterAnimation = if (centeredTitleAndSubtitle) {
                fadeIn(initialAlpha = collapsedTitleAlpha.invoke().value) + expandVertically(
                    expandFrom = Alignment.Bottom
                )
            } else fadeIn(initialAlpha = collapsedTitleAlpha.invoke().value)

            val titleExitAnimation =
                if (centeredTitleAndSubtitle) slideOutVertically() + fadeOut()
                else fadeOut()

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
                        content = navigationIcon
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
    val measurePolicy = simpleColumnMeasurePolicy(verticalArrangement, horizontalAlignment)
    Layout(modifier = modifier, content = content, measurePolicy = measurePolicy)
}

/**
 * The layout that holds and displays the [expandedTitle] and [subtitle] slots
 * @author Germain Kevin
 * */
@Composable
private inline fun SimpleColumnWithTitleSubtitle(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal,
    expandedTitle: @Composable () -> Unit,
    subtitle: @Composable () -> Unit
) {
    SimpleColumn(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
    ) {
        SimpleColumn(
            modifier = Modifier
                .wrapContentSize(),
            horizontalAlignment = horizontalAlignment,
        ) {
            expandedTitle()
            subtitle()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * A [Layout] that draws horizontally a navigationIcon, a title, a mainAction and actions
 * @author Germain Kevin
 * */
@Composable
private inline fun CollapsedTopBar(
    modifier: Modifier = Modifier,
    collapsedTopBarContent: @Composable () -> Unit,
    scrollBehavior: CollapsingTopBarScrollBehavior,
) {
    val collapsedTopBarHeight = LocalDensity.current.run {
        scrollBehavior.collapsedTopBarHeight.toPx().toInt()
    }
    val measurePolicy = collapsedTopBarMeasurePolicy(collapsedTopBarHeight, scrollBehavior)
    Layout(content = collapsedTopBarContent, modifier = modifier, measurePolicy = measurePolicy)
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

        val currentHeight = scrollBehavior.currentTopBarHeight.roundToPx()
        val currentExpandedHeight = scrollBehavior.expandedTopBarMaxHeight.roundToPx()
        val horizontalPaddingPx = TopBarHorizontalPadding.toPx()

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

        val navIconYPosition = (collapsedTopBarHeight - navigationIconPlaceable.height) / 2
        val titleYPosition = (collapsedTopBarHeight - titlePlaceable.height) / 2
        val mainActionYPosition = (collapsedTopBarHeight - mainActionIconPlaceable.height) / 2
        val actionsYPosition = (collapsedTopBarHeight - actionIconsPlaceable.height) / 2

        val placeTitleAtStart = max(TopBarTitleInset.roundToPx(), navigationIconPlaceable.width)
        val placeTitleInCenter = if (mainActionIconPlaceable.width > horizontalPaddingPx
            && actionIconsPlaceable.width > horizontalPaddingPx
        ) {
            (constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width) / 2
        } else {
            (constraints.maxWidth - titlePlaceable.width) / 2
        }

        val titleXPosition =
            if (scrollBehavior.centeredTitleWhenCollapsed) placeTitleInCenter else placeTitleAtStart

        val mainActionFixedXPosition =
            constraints.maxWidth - actionIconsPlaceable.width - mainActionIconPlaceable.width

        val mainActionInCenter = (constraints.maxWidth - actionIconsPlaceable.width) / 2

        val mainActionX: Int = if (!scrollBehavior.isCollapsed) {
            val xOffset = mainActionFixedXPosition - currentHeight
            if (xOffset > mainActionInCenter && currentHeight != currentExpandedHeight) xOffset
            else mainActionInCenter
        } else {
            mainActionFixedXPosition
        }

        val actionsXPosition = constraints.maxWidth - actionIconsPlaceable.width

        layout(constraints.maxWidth, collapsedTopBarHeight) {

            navigationIconPlaceable.placeRelative(x = 0, y = navIconYPosition)

            titlePlaceable.placeRelative(x = titleXPosition, y = titleYPosition)

            mainActionIconPlaceable.placeRelative(x = mainActionX, y = mainActionYPosition)

            actionIconsPlaceable.placeRelative(x = actionsXPosition, y = actionsYPosition)
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
