package com.germainkevin.collapsingtopbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * [CollapsingTopBar] is like a [TopAppBar] that can collapse and/or expand.
 *
 * This [CollapsingTopBar] has slots for a title, subtitle, navigation icon, and actions.
 *
 * @param modifier A modifier that is passed down to the main layer which is a [Surface]
 * @param title The title to be displayed inside the [CollapsingTopBar]
 * @param subtitle The subtitle to be displayed inside the [CollapsingTopBar]
 * @param navigationIcon the navigation icon displayed at the start of the [CollapsingTopBar].
 * This should typically be an [IconButton] or [IconToggleButton].
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
    subtitle: @Composable () -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    colors: CollapsingTopBarColors = CollapsingTopBarDefaults.colors(),
    contentPadding: PaddingValues = CollapsingTopBarDefaults.ContentPadding,
    scrollBehavior: CollapsingTopBarScrollBehavior,
    elevation: Dp = CollapsingTopBarDefaults.DefaultCollapsingTopBarElevation,
) = with(scrollBehavior) {
    CollapsingTopBarLayout(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        navigationIcon = navigationIcon,
        actions = actions,
        centeredTitleAndSubtitle = centeredTitleAndSubtitle,
        contentPadding = contentPadding,
        expandedColumnAlphaValue = expandedColumnAlphaValue.invoke().value,
        collapsedTitleAlpha = collapsedTitleAlpha.invoke().value,
        currentTopBarHeight = currentTopBarHeight,
        elevation = elevation,
        currentBackgroundColor = currentBackgroundColor(colors).value,
        currentContentColor = colors.contentColor
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
 * @param elevation The size of the shadow below the [Surface]
 * */
@Composable
private fun CollapsingTopBarLayout(
    modifier: Modifier,
    title: @Composable () -> Unit,
    subtitle: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
    centeredTitleAndSubtitle: Boolean,
    contentPadding: PaddingValues,
    currentBackgroundColor: Color,
    currentContentColor: Color,
    expandedColumnAlphaValue: Float,
    collapsedTitleAlpha: Float,
    currentTopBarHeight: Dp,
    elevation: Dp,
) = CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
    Surface(
        modifier = modifier,
        color = currentBackgroundColor,
        contentColor = currentContentColor,
        elevation = elevation,
    ) {
        Box(
            modifier = Modifier
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
                    .padding(
                        if (centeredTitleAndSubtitle) PaddingValues() else PaddingValues(
                            start =
                            if (navigationIcon != null) 56.dp - topBarHorizontalPadding
                            else 16.dp - topBarHorizontalPadding,
                            end = topBarHorizontalPadding
                        )
                    )
                    .alpha(expandedColumnAlphaValue),
                horizontalAlignment =
                if (centeredTitleAndSubtitle) Alignment.CenterHorizontally else Alignment.Start,
                verticalArrangement = Arrangement.Center,
                content = {
                    title()
                    subtitle()
                }
            )
            /**
             * Bottom of the [CollapsingTopBar] with navigation icon, title and actions icons
             * */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(currentTopBarHeight)
                    .padding(contentPadding)
                    .align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom,
                content = {
                    navigationIconRow(navigationIcon)

                    /**
                     * Title section, shown when the [CollapsingTopBar] is collapsed
                     * */
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            collapsedTitle(
                                centeredTitleAndSubtitle,
                                collapsedTitleAlpha,
                                title
                            )
                        }
                    )
                    /**
                     * More menu section where Options Menu icons are laid out
                     * */
                    actionsRow(actions)
                }
            )
        }
    }
}