package com.germainkevin.collapsingtopbar

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * [CollapsingTopBar]s display is like a [TopAppBar] that can collapse and/or expand.
 *
 * This [CollapsingTopBar] has slots for a title, subtitle, navigation icon, and actions.
 *
 * @param modifier A modifier that is passed down to the main layer which is a [Surface]
 * @param title The title to be displayed inside the [CollapsingTopBar]
 * @param subtitle The subtitle to be displayed inside the [CollapsingTopBar], it's optional though
 * @param navigationIcon the navigation icon displayed at the start of the [CollapsingTopBar].
 * This should typically be an [IconButton] or [IconToggleButton].
 * @param actions the actions displayed at the end of the [CollapsingTopBar]. This should typically
 * be [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param colors [CollapsingTopBarColors] that will be used to resolve the colors used for this
 * [CollapsingTopBar] in different states. See [CollapsingTopBarDefaults.colors].
 * @param contentPadding The padding of the content inside the [CollapsingTopBar]
 * @param elevation The size of the shadow below the [Surface]
 * @param scrollBehavior  [CollapsingTopBarScrollBehavior] which holds certain values that will be applied by
 * this [CollapsingTopBar] to set up its height. A scroll behavior is designed to work in
 * conjunction with a scrolled content to change the [CollapsingTopBar] appearance as the content
 * scrolls. See [CollapsingTopBarScrollBehavior.nestedScrollConnection].
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
    elevation: Dp = 0.dp,
) = with(scrollBehavior) {

    val columnWithTitleSubtitleAlpha by getTitleAndSubtitleColumnAlpha(
        currentTopBarHeight = currentTopBarHeight,
        collapsedTopBarHeight = collapsedTopBarHeight,
        expandedTopBarMaxHeight = expandedTopBarMaxHeight,
        margin = 20.dp
    )

    val collapsedTitleAlpha by getCollapsedTitleAlpha(
        currentTopBarHeight = currentTopBarHeight,
        visibleValue = collapsedTopBarHeight,
        invisibleValue = collapsedTopBarHeight + 6.dp
    )

    CollapsingTopBarLayout(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        navigationIcon = navigationIcon,
        actions = actions,
        centeredTitleAndSubtitle = centeredTitleAndSubtitle,
        contentPadding = contentPadding,
        colors = colors,
        columnWithTitleSubtitleAlpha = columnWithTitleSubtitleAlpha,
        collapsedTitleAlpha = collapsedTitleAlpha,
        currentTopBarHeight = currentTopBarHeight,
        elevation = elevation
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
 * @param colors [CollapsingTopBarColors] that will be used to resolve the colors used for this
 * [CollapsingTopBar] in different states. See [CollapsingTopBarDefaults.colors].
 * @param contentPadding The padding of the content inside the [CollapsingTopBar]
 * @param elevation The size of the shadow below the [Surface]
 * @param currentTopBarHeight The current height of the [CollapsingTopBar]
 * @param elevation The size of the shadow below the [Surface]
 * */
@Composable
internal fun CollapsingTopBarLayout(
    modifier: Modifier,
    title: @Composable () -> Unit,
    subtitle: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
    centeredTitleAndSubtitle: Boolean,
    contentPadding: PaddingValues,
    colors: CollapsingTopBarColors,
    columnWithTitleSubtitleAlpha: Float,
    collapsedTitleAlpha: Float,
    currentTopBarHeight: Dp,
    elevation: Dp,
) = CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
    Surface(
        modifier = modifier,
        color = colors.backgroundColor,
        contentColor = colors.contentColor,
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
                            if (navigationIcon != null) 56.dp - appBarHorizontalPadding
                            else 16.dp - appBarHorizontalPadding,
                            end = appBarHorizontalPadding
                        )
                    )
                    .alpha(columnWithTitleSubtitleAlpha),
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

internal val navigationIconRow: @Composable (@Composable (() -> Unit)?) -> Unit =
    { navigationIcon ->
        if (navigationIcon == null) Spacer(modifier = noNavIconSpacerModifier)
        else {
            Row(
                modifier = navigationIconModifier,
                verticalAlignment = Alignment.Bottom,
                content = { navigationIcon() }
            )
        }
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
            visible = collapsedTitleAlpha in 0f..1f,
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
 * We'll reference [collapsedTopBarHeight] as 56 and [expandedTopBarMaxHeight] as 200
 *
 * We'll add a margin to make the Title Subtitle Column disappear before the CollapsingTopBar
 * reaches the height of 56.dp which is a margin of 20.dp
 *
 * 56 + 20  --------> 0f (alpha value meaning  when the title subtitle section is fully invisible)
 * 200 --------> 1f (alpha value meaning when the title subtitle section is fully visible)
 *
 * The distance between 56+20  and 200 is 124. This distance represents the 100% distance from the
 * collapsed state and expanded state of the [CollapsingTopBar]
 *
 * So what we do is:
 * 124                                ----------> 100%
 * currentTopBarHeight's actual value -------------> x
 *
 * Whatever value x is, is considered the level of visibility the Title Subtitle Column should have
 *
 * @param currentTopBarHeight The current height of the [CollapsingTopBar] in [Dp]
 * @param collapsedTopBarHeight The height of the [CollapsingTopBar] when it is collapsed
 * @param expandedTopBarMaxHeight The height of the [CollapsingTopBar] when it is expanded
 * @param margin A distance added to start making the TitleAndSubtitleColumn's visible only when
 * [currentTopBarHeight] reaches [collapsedTopBarHeight] + margin
 */
@Composable
internal fun getTitleAndSubtitleColumnAlpha(
    currentTopBarHeight: Dp,
    collapsedTopBarHeight: Dp,
    expandedTopBarMaxHeight: Dp,
    margin: Dp
): State<Float> {
    return animateFloatAsState(
        (currentTopBarHeight - (collapsedTopBarHeight + margin)) /
                (expandedTopBarMaxHeight - (collapsedTopBarHeight + margin))
    )
}

/**
 * @param currentTopBarHeight The current height of the [CollapsingTopBar] in [Dp]
 * @param visibleValue A value in [Dp] that if [currentTopBarHeight] reaches it, the
 * Collapsed Title should become visible
 * @param invisibleValue A value in [Dp] that if [currentTopBarHeight] reaches it, the
 * Collapsed Title section should become invisible
 * */
@Composable
internal fun getCollapsedTitleAlpha(
    currentTopBarHeight: Dp,
    visibleValue: Dp,
    invisibleValue: Dp
): State<Float> {
    return animateFloatAsState(
        if (currentTopBarHeight == visibleValue) 1f
        else (visibleValue - currentTopBarHeight) / (invisibleValue - visibleValue)
    )
}