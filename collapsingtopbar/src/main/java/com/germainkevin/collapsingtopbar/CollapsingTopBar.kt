package com.germainkevin.collapsingtopbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import timber.log.Timber

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
    mainAction: @Composable (() -> Unit)? = null,
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
        elevation = elevation,
        currentBackgroundColor = currentBackgroundColor(colors).value,
        currentContentColor = colors.contentColor,
        scrollBehavior = scrollBehavior
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
    mainAction: @Composable (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
    centeredTitleWhenCollapsed: Boolean,
    centeredTitleAndSubtitle: Boolean,
    contentPadding: PaddingValues,
    currentBackgroundColor: Color,
    currentContentColor: Color,
    expandedColumnAlphaValue: Float,
    collapsedTitleAlpha: Float,
    currentTopBarHeight: Dp,
    scrollBehavior: CollapsingTopBarScrollBehavior,
    elevation: Dp,
) = CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
    Surface(
        modifier = modifier,
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
                    .fillMaxWidth()
                    .height(currentTopBarHeight)
                    .alpha(expandedColumnAlphaValue)
                    .padding(
                        if (centeredTitleAndSubtitle) emptyPaddingValues else {
                            PaddingValues(
                                bottom = contentPadding.calculateBottomPadding(),
                                start = if (navigationIcon != null) {
                                    56.dp - contentPadding.calculateStartPadding(
                                        LocalLayoutDirection.current
                                    )
                                } else {
                                    16.dp - contentPadding.calculateStartPadding(
                                        LocalLayoutDirection.current
                                    )
                                },
                                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
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
            }
            /**
             * Collapsed Layout
             * Bottom of the [CollapsingTopBar] with navigation icon, title and actions icons
             * */

            val collapsedLayoutSize = remember { mutableStateOf(IntSize.Zero) }

            val collapsedRowLayoutModifier = if (mainAction != null) {
                Modifier
                    .fillMaxWidth()
                    .onSizeChanged { collapsedLayoutSize.value = it }
                    .height(currentTopBarHeight)
                    .padding(contentPadding)
                    .align(Alignment.BottomStart)
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(currentTopBarHeight)
                    .padding(contentPadding)
                    .align(Alignment.BottomStart)
            }

            Row(
                modifier = collapsedRowLayoutModifier,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom,
            ) {

                /**
                 * Navigation Icon Row
                 * */
                val noNavIconModifier = Modifier.width(
                    16.dp - contentPadding.calculateStartPadding(LocalLayoutDirection.current)
                )
                if (navigationIcon == null) Spacer(modifier = noNavIconModifier)
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

                /**
                 * Title Row
                 * */
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =
                    if (centeredTitleWhenCollapsed) Arrangement.Center else Arrangement.Start,
                    content = {
                        collapsedTitle(
                            centeredTitleAndSubtitle,
                            collapsedTitleAlpha,
                            title
                        )
                    }
                )

                /**
                 * Main Action Row
                 * */
                if (mainAction != null) {

                    var mainActionWidthSize by remember { mutableStateOf(0) }

                    var mainActionPosInWndw by remember { mutableStateOf(Offset.Zero) }

                    val centerPosOfCollapsedLyt = collapsedLayoutSize.value.width / 2

                    val distanceTilCenter = mainActionPosInWndw.x - centerPosOfCollapsedLyt

                    val mainActionInCenterOfLyt = (distanceTilCenter - mainActionWidthSize).dp

                    val endPaddingValue = if (scrollBehavior.isExpanded
                        && mainActionInCenterOfLyt > currentTopBarHeight
                    ) {
                        mainActionInCenterOfLyt
                    } else {
                        currentTopBarHeight.coerceIn(0.dp, mainActionInCenterOfLyt)
                    }

                    val mainActionPadding = PaddingValues(
                        end = if (!scrollBehavior.isCollapsed
                            && mainActionPosInWndw.x > centerPosOfCollapsedLyt
                        ) {
                            endPaddingValue
                        } else 0.dp
                    )

                    var onGlobalPositionSizeChange by remember { mutableStateOf(true) }
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .onGloballyPositioned { coordinates ->
                                if (onGlobalPositionSizeChange) {
                                    mainActionWidthSize = coordinates.size.width
                                    mainActionPosInWndw = coordinates.positionInWindow()
                                    onGlobalPositionSizeChange = false
                                }
                                Timber.d("currentTopBarHeight:$currentTopBarHeight")
                                Timber.d("mainActionWidthSize:$mainActionWidthSize")
                                Timber.d("mainActionPosInWndw:$mainActionPosInWndw")
                                Timber.d("centerPosOfCollapsedLyt:$centerPosOfCollapsedLyt")
                                Timber.d("distanceTilCenter:$distanceTilCenter")
                                Timber.d("mainActionInCenterOfLyt:$mainActionInCenterOfLyt")
                                Timber.d("endPaddingValue:$endPaddingValue")
                            }
                            .padding(
                                if (centeredTitleAndSubtitle) mainActionPadding else emptyPaddingValues
                            ),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom,
                        content = { mainAction() }
                    )
                }

                /**
                 * Actions Row
                 * */
                actionsRow(actions)
            }
        }
    }
}