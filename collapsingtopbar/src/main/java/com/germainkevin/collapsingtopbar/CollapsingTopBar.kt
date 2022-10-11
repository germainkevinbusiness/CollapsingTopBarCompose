package com.germainkevin.collapsingtopbar

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

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
 * have a different [collapsed height][CollapsingTopBarScrollBehavior.collapsedTopBarHeight] or
 * a different [expanded height][CollapsingTopBarScrollBehavior.expandedTopBarMaxHeight], you set it
 * there, if you want the [CollapsingTopBar] to detect when a scroll event has occurred in your UI
 * and you want the [CollapsingTopBar] to collapse or expand, you simply pass
 * [scrollBehavior.nestedScrollConnection][CollapsingTopBarScrollBehavior.nestedScrollConnection] to
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
) = with(scrollBehavior) {
    CollapsingTopBarLayout(
        modifier = modifier,
        title = title,
        expandedTitle = expandedTitle,
        subtitle = subtitle,
        navigationIcon = navigationIcon,
        mainAction = mainAction,
        actions = actions,
        centeredTitleWhenCollapsed = centeredTitleWhenCollapsed,
        centeredTitleAndSubtitle = centeredTitleAndSubtitle,
        expandedColumnAlphaValue = expandedColumnAlphaValue.invoke().value,
        collapsedTitleAlpha = collapsedTitleAlpha.invoke().value,
        currentTopBarHeight = currentTopBarHeight,
        collapsedTopBarHeight = collapsedTopBarHeight,
        expandedTopBarMaxHeight = expandedTopBarMaxHeight,
        elevation = elevation,
        currentBackgroundColor = currentBackgroundColor(colors),
        currentContentColor = colors.contentColor,
        scrollBehavior = scrollBehavior,
        colors = colors
    )
}

/**
 * @param currentTopBarHeight The current height of the [CollapsingTopBar]
 * @param centeredTitleWhenCollapsed Whether the [title] should be centered when the
 * [CollapsingTopBar] is collapsed
 * @param centeredTitleAndSubtitle Whether the [expandedTitle] and [subtitle] composables
 * should be centered when the [CollapsingTopBar] is expanded
 * @param expandedColumnAlphaValue The alpha visibility value of the [SimpleColumn] holding the
 * [expandedTitle] and [subtitle] composables
 * @param collapsedTitleAlpha The alpha visibility value of the [title] composable
 * */
@Composable
private fun CollapsingTopBarLayout(
    modifier: Modifier,
    title: @Composable () -> Unit,
    expandedTitle: @Composable () -> Unit,
    subtitle: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)?,
    mainAction: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    centeredTitleWhenCollapsed: Boolean,
    centeredTitleAndSubtitle: Boolean,
    currentBackgroundColor: State<Color>,
    currentContentColor: Color,
    expandedColumnAlphaValue: Float,
    collapsedTitleAlpha: Float,
    currentTopBarHeight: Dp,
    collapsedTopBarHeight: Dp,
    expandedTopBarMaxHeight: Dp,
    scrollBehavior: CollapsingTopBarScrollBehavior,
    colors: CollapsingTopBarColors,
    elevation: Dp,
) {
    val scrollState = rememberScrollState()
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(currentTopBarHeight)
            .verticalScroll(scrollState),
        color = currentBackgroundColor.value,
        contentColor = currentContentColor,
        elevation = elevation,
    ) {
        Box {
            val expandedColumnModifier = if (centeredTitleAndSubtitle) {
                Modifier
                    .alpha(expandedColumnAlphaValue)
                    .fillMaxWidth()
                    .height(currentTopBarHeight)
                    .padding(horizontal = TopBarHorizontalPadding * 4)
            } else {
                Modifier
                    .alpha(expandedColumnAlphaValue)
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
            SimpleColumn(
                modifier = expandedColumnModifier,
                horizontalAlignment =
                if (centeredTitleAndSubtitle) Alignment.CenterHorizontally else Alignment.Start,
            ) {
                SimpleColumn(
                    modifier = Modifier.wrapContentSize(),
                    horizontalAlignment =
                    if (centeredTitleAndSubtitle) Alignment.CenterHorizontally else Alignment.Start,
                ) {
                    expandedTitle()
                    subtitle()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            SingleRowTopBar(
                modifier = Modifier.align(Alignment.BottomStart),
                title = title,
                navigationIcon = navigationIcon,
                mainAction = mainAction,
                actions = actions,
                currentTopBarHeight = currentTopBarHeight,
                collapsedTopBarHeight = collapsedTopBarHeight,
                expandedTopBarMaxHeight = expandedTopBarMaxHeight,
                centeredTitleWhenCollapsed = centeredTitleWhenCollapsed,
                centeredTitleAndSubtitle = centeredTitleAndSubtitle,
                collapsedTitleAlpha = collapsedTitleAlpha,
                colors = colors,
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
        content = content
    ) { measurables, constraints ->

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

/**
 * A [Layout] that draws a [navigationIcon], a [title], a [mainAction] and [actions]
 * @author Germain Kevin
 * */
@Composable
private fun SingleRowTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = {},
    mainAction: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    currentTopBarHeight: Dp,
    collapsedTopBarHeight: Dp,
    expandedTopBarMaxHeight: Dp,
    centeredTitleWhenCollapsed: Boolean,
    centeredTitleAndSubtitle: Boolean,
    collapsedTitleAlpha: Float,
    colors: CollapsingTopBarColors,
    scrollBehavior: CollapsingTopBarScrollBehavior,
) {

    val titleEnterAnimation = if (centeredTitleAndSubtitle) {
        fadeIn(initialAlpha = collapsedTitleAlpha) + expandVertically(
            expandFrom = Alignment.Bottom
        )
    } else fadeIn(initialAlpha = collapsedTitleAlpha)

    val titleExitAnimation = if (centeredTitleAndSubtitle)
        slideOutVertically() + fadeOut() else fadeOut()

    // Wrap the given actions in a Row.
    val actionsRow = @Composable {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }

    val height = LocalDensity.current.run {
        collapsedTopBarHeight.toPx()
    }
    TopBarLayout(
        modifier = modifier,
        heightPx = height,
        currentTopBarHeight = currentTopBarHeight,
        expandedTopBarMaxHeight = expandedTopBarMaxHeight,
        contentColor = colors.contentColor,
        title = title,
        centeredTitleWhenCollapsed = centeredTitleWhenCollapsed,
        navigationIcon = navigationIcon ?: {},
        mainAction = mainAction,
        actions = actionsRow,
        collapsedTitleAlpha = collapsedTitleAlpha,
        titleEnterAnimation = titleEnterAnimation,
        titleExitAnimation = titleExitAnimation,
        scrollBehavior = scrollBehavior,
    )
}

/**
 * A simpler version of a Row, created to better implement the addition of the [mainAction]
 * composable
 * @author Germain Kevin
 * */
@Composable
private fun TopBarLayout(
    currentTopBarHeight: Dp,
    expandedTopBarMaxHeight: Dp,
    heightPx: Float,
    contentColor: Color,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    mainAction: @Composable () -> Unit,
    actions: @Composable () -> Unit = {},
    centeredTitleWhenCollapsed: Boolean,
    collapsedTitleAlpha: Float,
    titleEnterAnimation: EnterTransition,
    titleExitAnimation: ExitTransition,
    scrollBehavior: CollapsingTopBarScrollBehavior,
) {
    Layout(
        {
            Box(
                Modifier
                    .layoutId("navigationIcon")
                    .padding(start = TopBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides contentColor,
                    content = navigationIcon
                )
            }
            Box(
                modifier = Modifier
                    .layoutId("title")
                    .padding(horizontal = TopBarHorizontalPadding),
            ) {
                AnimatedVisibility(
                    visible = collapsedTitleAlpha in 0F..1F,
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
                    LocalContentColor provides contentColor,
                    content = mainAction
                )
            }
            Box(
                Modifier
                    .layoutId("actionIcons")
                    .padding(end = TopBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides contentColor,
                    content = actions
                )
            }
        },
        modifier = modifier
    ) { measurables, constraints ->

        val navigationIconPlaceable =
            measurables.first { it.layoutId == "navigationIcon" }.measure(constraints)
        val mainActionIconPlaceable =
            measurables.first { it.layoutId == "mainAction" }.measure(constraints)
        val actionIconsPlaceable =
            measurables.first { it.layoutId == "actionIcons" }.measure(constraints)

        val maxTitleWidth =
            constraints.maxWidth - navigationIconPlaceable.width -
                    mainActionIconPlaceable.width - actionIconsPlaceable.width

        val titlePlaceable =
            measurables
                .first { it.layoutId == "title" }
                .measure(constraints.copy(maxWidth = maxTitleWidth))

        val layoutHeight = heightPx.roundToInt()

        layout(constraints.maxWidth, layoutHeight) {

            // Locate the title's baseline.
            val titleBaseline =
                if (titlePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
                    titlePlaceable[LastBaseline]
                } else {
                    0
                }

            val navIconYPosition = titleBaseline / 3

            // Navigation icon
            navigationIconPlaceable.placeRelative(x = 0, y = navIconYPosition)

            val mainActionWidth = mainActionIconPlaceable.width.toFloat()
            val actionsWidth = actionIconsPlaceable.width.toFloat()

            val horizPaddingPx = TopBarHorizontalPadding.toPx()

            val placeTitleAtStart = max(TopBarTitleInset.roundToPx(), navigationIconPlaceable.width)
            val placeTitleInCenter =
                if (mainActionWidth > horizPaddingPx && actionsWidth > horizPaddingPx) {
                    (constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width) / 2
                } else {
                    (constraints.maxWidth - titlePlaceable.width) / 2
                }

            // Title
            titlePlaceable.placeRelative(
                x = if (centeredTitleWhenCollapsed) placeTitleInCenter else placeTitleAtStart,
                y = (layoutHeight - titlePlaceable.height) / 2
            )

            val mainActionFixedXPosition =
                constraints.maxWidth - actionIconsPlaceable.width - mainActionIconPlaceable.width

            val currentHeight = currentTopBarHeight.toPx().toInt()
            val currentExpandedHeight = expandedTopBarMaxHeight.toPx().toInt()

            val mainActionInCenter = (constraints.maxWidth - actionIconsPlaceable.width) / 2

            val mainActionX: Int = if (!scrollBehavior.isCollapsed) {
                val x = mainActionFixedXPosition - currentHeight
                if (x > mainActionInCenter && currentHeight != currentExpandedHeight) x
                else {
                    mainActionInCenter
                }
            } else {
                mainActionFixedXPosition
            }

            // Main Action Icon
            mainActionIconPlaceable.placeRelative(
                x = mainActionX,
                y = (layoutHeight - mainActionIconPlaceable.height) / 2
            )

            // Action icons
            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = (layoutHeight - actionIconsPlaceable.height) / 2
            )
        }
    }
}