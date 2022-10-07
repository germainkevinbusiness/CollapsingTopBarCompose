package com.germainkevin.collapsingtopbar

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

private val TopBarHorizontalPadding = 4.dp

// A title inset when the App-Bar is a Medium or Large one. Also used to size a spacer when the
// navigation icon is missing.
private val TopBarTitleInset = 16.dp - TopBarHorizontalPadding

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
 * @param contentPadding The padding of the content inside the [CollapsingTopBar]
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
    expandedTitle: @Composable (() -> Unit)? = null,
    subtitle: @Composable () -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null,
    mainAction: @Composable () -> Unit = { },
    actions: @Composable RowScope.() -> Unit = {},
    colors: CollapsingTopBarColors = CollapsingTopBarDefaults.colors(),
    contentPadding: PaddingValues = CollapsingTopBarDefaults.ContentPadding,
    scrollBehavior: CollapsingTopBarScrollBehavior,
    elevation: Dp = CollapsingTopBarDefaults.DefaultCollapsingTopBarElevation,
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
        contentPadding = contentPadding,
        expandedColumnAlphaValue = expandedColumnAlphaValue.invoke().value,
        collapsedTitleAlpha = collapsedTitleAlpha.invoke().value,
        currentTopBarHeight = currentTopBarHeight,
        collapsedTopBarHeight = collapsedTopBarHeight,
        elevation = elevation,
        currentBackgroundColor = currentBackgroundColor(colors).value,
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
    expandedTitle: @Composable (() -> Unit)?,
    subtitle: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)?,
    mainAction: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    centeredTitleWhenCollapsed: Boolean,
    centeredTitleAndSubtitle: Boolean,
    contentPadding: PaddingValues,
    currentBackgroundColor: Color,
    currentContentColor: Color,
    expandedColumnAlphaValue: Float,
    collapsedTitleAlpha: Float,
    currentTopBarHeight: Dp,
    collapsedTopBarHeight: Dp,
    scrollBehavior: CollapsingTopBarScrollBehavior,
    colors: CollapsingTopBarColors,
    elevation: Dp,
) = CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
    val scrollState = rememberScrollState()
    val contentPaddingStart = contentPadding.calculateStartPadding(LocalLayoutDirection.current)
    Surface(
        modifier = modifier.verticalScroll(scrollState),
        color = currentBackgroundColor,
        contentColor = currentContentColor,
        elevation = elevation,
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(currentTopBarHeight),
        ) {
            /**
             * Title and Subtitle section, shown when the [CollapsingTopBar] is expanded
             * */
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(if (centeredTitleAndSubtitle) Alignment.Center else Alignment.CenterStart)
                    .height(currentTopBarHeight)
                    .alpha(expandedColumnAlphaValue)
                    .padding(
                        if (centeredTitleAndSubtitle) emptyPaddingValues else {
                            PaddingValues(
                                start = if (navigationIcon != null) 56.dp - contentPaddingStart
                                else 16.dp - contentPaddingStart,
                                end = contentPaddingStart,
                                bottom = contentPadding.calculateBottomPadding()
                            )
                        }
                    ),
                horizontalAlignment =
                if (centeredTitleAndSubtitle) Alignment.CenterHorizontally else Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                if (expandedTitle == null) {
                    title()
                } else {
                    expandedTitle()
                }
                subtitle()
                Spacer(modifier = Modifier.height(16.dp))
            }

            /**
             * Collapsed Layout
             * Bottom of the [CollapsingTopBar] with navigation icon, title and actions icons
             * */

            SingleRowTopBar(
                modifier = Modifier.align(Alignment.BottomStart),
                title = title,
                navigationIcon = navigationIcon,
                mainAction = mainAction,
                actions = actions,
                collapsedTopBarHeight = collapsedTopBarHeight,
                centeredTitleWhenCollapsed = centeredTitleWhenCollapsed,
                centeredTitleAndSubtitle = centeredTitleAndSubtitle,
                collapsedTitleAlpha = collapsedTitleAlpha,
                colors = colors
            )

//            val collapsedRowLayoutSize = remember { mutableStateOf(IntSize.Zero) }
//
//            val collapsedRowLayoutModifier = if (mainAction != null) {
//                Modifier
//                    .fillMaxWidth()
//                    .onSizeChanged { collapsedRowLayoutSize.value = it }
//                    .height(currentTopBarHeight)
//                    .padding(contentPadding)
//                    .align(Alignment.BottomStart)
//            } else {
//                Modifier
//                    .fillMaxWidth()
//                    .height(currentTopBarHeight)
//                    .padding(contentPadding)
//                    .align(Alignment.BottomStart)
//            }
//
//            Row(
//                modifier = collapsedRowLayoutModifier,
//                horizontalArrangement = Arrangement.Start,
//                verticalAlignment = Alignment.Bottom,
//            ) {
//
//                /**
//                 * Navigation Icon Row
//                 * */
//                val noNavIconModifier = Modifier.width(16.dp - contentPaddingStart)
//                if (navigationIcon == null) Spacer(modifier = noNavIconModifier)
//                else if (centeredTitleWhenCollapsed) Row(
//                    modifier = Modifier.wrapContentWidth(),
//                    verticalAlignment = Alignment.Bottom,
//                    content = { navigationIcon() }
//                )
//                else Row(
//                    modifier = navigationIconModifier,
//                    verticalAlignment = Alignment.Bottom,
//                    content = { navigationIcon() }
//                )
//
//                /**
//                 * Title Row
//                 * */
//                Row(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .weight(1f),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement =
//                    if (centeredTitleWhenCollapsed) Arrangement.Center else Arrangement.Start,
//                    content = {
//                        collapsedTitle(
//                            centeredTitleAndSubtitle,
//                            collapsedTitleAlpha,
//                            title
//                        )
//                    }
//                )
//
//                /**
//                 * Main Action Row
//                 * */
//                if (mainAction != null) {
//
//                    var mainActionWidthSize by remember { mutableStateOf(0) }
//
//                    var mainActionPosInWndw by remember { mutableStateOf(Offset.Zero) }
//
//                    val centerPosOfCollapsedLyt = collapsedRowLayoutSize.value.width / 2
//
//                    val distanceTilCenter =
//                        (mainActionPosInWndw.x - (centerPosOfCollapsedLyt)) + contentPaddingStart.value
//
//                    val mainActionInCenterOfLyt = (distanceTilCenter - mainActionWidthSize).dp
//
//                    val endPaddingValue = if (scrollBehavior.isExpanded
//                        && mainActionInCenterOfLyt > currentTopBarHeight
//                    ) {
//                        mainActionInCenterOfLyt
//                    } else {
//                        currentTopBarHeight.coerceIn(0.dp, mainActionInCenterOfLyt)
//                    }
//
//                    val mainActionPadding = PaddingValues(
//                        end = if (!scrollBehavior.isCollapsed
//                            && mainActionPosInWndw.x > centerPosOfCollapsedLyt
//                        ) {
//                            endPaddingValue
//                        } else 0.dp
//                    )
//
//                    var onGlobalPositionSizeChange by remember { mutableStateOf(true) }
//                    Row(
//                        modifier = Modifier
//                            .fillMaxHeight()
//                            .onGloballyPositioned { coordinates ->
//                                if (onGlobalPositionSizeChange) {
//                                    mainActionWidthSize = coordinates.size.width
//                                    mainActionPosInWndw = coordinates.positionInWindow()
//                                    onGlobalPositionSizeChange = false
//                                }
//                            }
//                            .padding(
//                                if (centeredTitleAndSubtitle) mainActionPadding else emptyPaddingValues
//                            ),
//                        horizontalArrangement = Arrangement.End,
//                        verticalAlignment = Alignment.Bottom,
//                        content = { mainAction() }
//                    )
//                }
//
//                /**
//                 * Actions Row
//                 * */
//                actionsRow(actions)
//            }
        }
    }
}

@Composable
fun SingleRowTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = {},
    mainAction: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    collapsedTopBarHeight: Dp,
    centeredTitleWhenCollapsed: Boolean,
    centeredTitleAndSubtitle: Boolean,
    collapsedTitleAlpha: Float,
    colors: CollapsingTopBarColors
) {

    val enterAnimation = if (centeredTitleAndSubtitle) {
        fadeIn(initialAlpha = collapsedTitleAlpha) + expandVertically(
            expandFrom = Alignment.Bottom
        )
    } else fadeIn(initialAlpha = collapsedTitleAlpha)

    val exitAnimation = if (centeredTitleAndSubtitle)
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
        navigationIconContentColor = colors.contentColor,
        titleContentColor = colors.contentColor,
        actionIconContentColor = colors.contentColor,
        title = title,
        titleTextStyle = LocalTextStyle.current,
        centeredTitleWhenCollapsed = centeredTitleWhenCollapsed,
        navigationIcon = navigationIcon ?: {},
        mainAction = mainAction,
        actions = actionsRow,
        collapsedTitleAlpha = collapsedTitleAlpha,
        titleEnterAnimation = enterAnimation,
        titleExitAnimation = exitAnimation
    )
}


@Composable
private fun TopBarLayout(
    heightPx: Float,
    navigationIconContentColor: Color,
    titleContentColor: Color,
    actionIconContentColor: Color,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    mainAction: @Composable () -> Unit,
    actions: @Composable () -> Unit = {},
    centeredTitleWhenCollapsed: Boolean,
    collapsedTitleAlpha: Float,
    titleEnterAnimation: EnterTransition,
    titleExitAnimation: ExitTransition
) {
    Layout(
        {
            Box(
                Modifier
                    .layoutId("navigationIcon")
                    .padding(start = TopBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides navigationIconContentColor,
                    content = navigationIcon
                )
            }
            Box(
                modifier = Modifier
                    .layoutId("title")
                    .padding(horizontal = TopBarHorizontalPadding),
            ) {
                ProvideTextStyle(value = titleTextStyle) {
                    CompositionLocalProvider(
                        LocalContentColor provides titleContentColor,
                        content = {
                            AnimatedVisibility(
                                visible = collapsedTitleAlpha in 0F..1F,
                                enter = titleEnterAnimation,
                                exit = titleExitAnimation,
                                content = { title() }
                            )
                        }
                    )
                }
            }
            Box(
                Modifier
                    .layoutId("mainAction")
                    .padding(start = TopBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides actionIconContentColor,
                    content = mainAction
                )
            }
            Box(
                Modifier
                    .layoutId("actionIcons")
                    .padding(end = TopBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides actionIconContentColor,
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

            // Have no clue why this positioning is working, but it works !
            val navIconYPosInCenter = titleBaseline / 3

            // Navigation icon
            navigationIconPlaceable.placeRelative(x = 0, y = navIconYPosInCenter)

            val mainActionWidth = mainActionIconPlaceable.width.toFloat()
            val actionsWidth = actionIconsPlaceable.width.toFloat()

            val horizPaddingPx = TopBarHorizontalPadding.toPx()

            val placeTitleNextToNavIcon =
                max(TopBarTitleInset.roundToPx(), navigationIconPlaceable.width)
            val placeTitleInCenter =
                if (mainActionWidth > horizPaddingPx && actionsWidth > horizPaddingPx) {
                    (constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width) / 2
                } else {
                    (constraints.maxWidth - titlePlaceable.width) / 2
                }

            // Title
            titlePlaceable.placeRelative(
                x = if (centeredTitleWhenCollapsed) placeTitleInCenter else placeTitleNextToNavIcon,
                y = (layoutHeight - titlePlaceable.height) / 2
            )

            // Main Action Icon
            mainActionIconPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width - mainActionIconPlaceable.width,
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