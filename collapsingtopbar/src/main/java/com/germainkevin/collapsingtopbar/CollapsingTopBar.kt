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
 * [CollapsingTopBar] is like a [TopAppBar] that can collapse and/or expand.
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
    elevation: Dp = CollapsingTopBarDefaults.DefaultCollapsingTopBarElevation,
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
private fun CollapsingTopBarLayout(
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

private val navigationIconRow: @Composable (@Composable (() -> Unit)?) -> Unit =
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

private val collapsedTitle: @Composable (Boolean, Float, @Composable () -> Unit) -> Unit =
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
private val actionsRow: @Composable (@Composable RowScope.() -> Unit) -> Unit = {
    Row(
        modifier = Modifier.fillMaxHeight(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
        content = it
    )
}

/**
 * In order to know when the "title subtitle column" should have a 1f alpha visibility or 0f alpha
 * visibility or a alpha value between 0f and 1f, we need to base that alpha value on the
 * [currentTopBarHeight] of the [CollapsingTopBar].
 *
 * So in this sense when the [currentTopBarHeight] of the [CollapsingTopBar]
 * is [collapsedTopBarHeight] + [margin] then  the "title subtitle column" should be invisible
 * or alpha = 0f, and when the [CollapsingTopBar]'s [currentTopBarHeight] is
 * [expandedTopBarMaxHeight] then the "title subtitle column" should be visible or alpha = 1f.
 *
 * But we also want the "title subtitle column"'s alpha value to be between 0f and 1f when the
 * [CollapsingTopBar]'s [currentTopBarHeight] is between
 * [collapsedTopBarHeight] + [margin] and [expandedTopBarMaxHeight]
 *
 * We'll reference [collapsedTopBarHeight] as 56 (Dp) and [expandedTopBarMaxHeight] as 200 (Dp), and
 * the [margin] as 20 (Dp)
 *
 * 56 + 20  --------> 0f (alpha value meaning  when the title subtitle column is fully invisible)
 *
 * 200      --------> 1f (alpha value meaning when the title subtitle column is fully visible)
 *
 * The distance between [expandedTopBarMaxHeight] - ([collapsedTopBarHeight] + [margin])
 * << A distance which we will label as 124 (Dp) because (200 - (56+20) = 124) >>,
 * is going to be the 100% distance from making the "title subtitle column" fully visible (100%) or
 * alpha =1f and fully invisible (0%) or alpha = 0f, or in between (0%..100%) 0.0f to 1.0f.
 *
 * So what we do is:
 * 124                                ----------> 100%
 *
 * currentTopBarHeight's actual value -------------> alphaValue
 *
 * Whatever value alphaValue is, is considered the level of visibility the "title subtitle column"
 * should have
 *
 * @param currentTopBarHeight The current height of the [CollapsingTopBar] in [Dp]
 * @param collapsedTopBarHeight The height of the [CollapsingTopBar] when it is collapsed
 * @param expandedTopBarMaxHeight The height of the [CollapsingTopBar] when it is expanded
 * @param margin Making sure that the 'title subtitle column" become visible once the
 * [currentTopBarHeight] reaches past [collapsedTopBarHeight] + [margin]
 */
@Composable
private fun getTitleAndSubtitleColumnAlpha(
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
 * Sets the alpha value of the collapsed title section
 * @param currentTopBarHeight The current height of the [CollapsingTopBar] in [Dp]
 * @param visibleValue A value in [Dp] that if [currentTopBarHeight] reaches it, the
 * Collapsed Title should become visible
 * @param invisibleValue A value in [Dp] that if [currentTopBarHeight] reaches it, the
 * Collapsed Title section should become invisible
 * */
@Composable
private fun getCollapsedTitleAlpha(
    currentTopBarHeight: Dp,
    visibleValue: Dp,
    invisibleValue: Dp
): State<Float> {
    return animateFloatAsState(
        if (currentTopBarHeight == visibleValue) 1f
        else (visibleValue - currentTopBarHeight) / (invisibleValue - visibleValue)
    )
}