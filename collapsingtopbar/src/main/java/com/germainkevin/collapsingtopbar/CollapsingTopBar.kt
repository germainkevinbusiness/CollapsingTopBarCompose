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
 * This [CollapsingTopBar] has slots for a title, subtitle, navigation icon, and actions.
 *
 * @param modifier A modifier that is passed down to the main layer which is a [Surface]
 * @param title The title to be displayed inside the [CollapsingTopBar]
 * @param expandedTitle Optional, In case you want the [title] to look different when the
 * [CollapsingTopBar] is expanded
 * @param subtitle The subtitle to be displayed inside the [CollapsingTopBar]
 * @param navigationIcon the navigation icon displayed at the start of the [CollapsingTopBar].
 * This should typically be an [IconButton] or [IconToggleButton].
 * @param mainAction A composable that is added before [actions] that can be animated if
 * [scrollBehavior]'s [CollapsingTopBarScrollBehavior.centeredTitleAndSubtitle] is set to true
 * @param actions the actions displayed at the end of the [CollapsingTopBar]. This should typically
 * be [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param colors [CollapsingTopBarColors] that will be used to resolve the colors used for this
 * [CollapsingTopBar] in different states. See [CollapsingTopBarDefaults.colors].
 * @param elevation The size of the shadow below the [Surface]
 * @param scrollBehavior determines the behavior of the [CollapsingTopBar]. If you want the
 * [CollapsingTopBar] to stay collapsed, you set it there, if you want the [CollapsingTopBar] to
 * have a different [collapsed height][CollapsingTopBarScrollBehavior.collapsedTopBarHeight] or
 * a different [expanded height][CollapsingTopBarScrollBehavior.expandedTopBarMaxHeight], you set it
 * there, if you want the [CollapsingTopBar] to detect when a scroll event has occured in your UI
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
 * @param modifier A modifier that is passed down to the main layer which is a [Surface]
 * @param title The title to be displayed inside the [CollapsingTopBar]
 * @param subtitle The subtitle to be displayed inside the [CollapsingTopBar], it's optional though
 * @param navigationIcon the navigation icon displayed at the start of the [CollapsingTopBar].
 * This should typically be an [IconButton] or [IconToggleButton].
 * @param actions the actions displayed at the end of the [CollapsingTopBar]. This should typically
 * be [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param contentPadding The padding of the content inside the [CollapsingTopBar]
 * @param elevation The size of the shadow below the [Surface]
 * @param currentTopBarHeight The current height of the [CollapsingTopBar]
 * @param centeredTitleWhenCollapsed Whether the [title] should be centered when the
 * [CollapsingTopBar] is collapsed
 * @param elevation The size of the shadow below the [Surface]
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
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(currentTopBarHeight)
            .verticalScroll(rememberScrollState()),
        color = currentBackgroundColor.value,
        contentColor = currentContentColor,
        elevation = elevation,
    ) {
        Box {
            /**
             * Title and Subtitle section, shown when the [CollapsingTopBar] is expanded
             * */
            val modifierWhenCentered = Modifier
                .wrapContentWidth()
                .align(Alignment.Center)
                .height(currentTopBarHeight)
                .alpha(expandedColumnAlphaValue)
                .padding(emptyPaddingValues)

            val modifierWhenAtStart = Modifier
                .wrapContentWidth()
                .align(Alignment.CenterStart)
                .height(currentTopBarHeight)
                .alpha(expandedColumnAlphaValue)
                .padding(
                    PaddingValues(
                        start = if (navigationIcon != null) 56.dp - TopBarHorizontalPadding
                        else TopBarTitleInset,
                        end = TopBarHorizontalPadding,
                    )
                )
            Column(
                modifier = if (centeredTitleAndSubtitle) modifierWhenCentered else modifierWhenAtStart,
                horizontalAlignment =
                if (centeredTitleAndSubtitle) Alignment.CenterHorizontally else Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                expandedTitle()
                subtitle()
                Spacer(modifier = Modifier.height(16.dp))
            }

            /**
             * Collapsed Layout
             * Bottom of the [CollapsingTopBar]
             * with navigation icon, title, mainAction and actions icons
             * */

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
                    visible = collapsedTitleAlpha == 1F,
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