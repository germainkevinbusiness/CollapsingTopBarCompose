package com.germainkevin.collapsingtopbarcompose.ui

import android.view.Window
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.germainkevin.collapsingtopbar.*
import com.germainkevin.collapsingtopbarcompose.R


/**
 * Text that appears in the title slot of the
 * [CollapsingTopBar][com.germainkevin.collapsingtopbar.CollapsingTopBar]
 * */
val TitleText: @Composable () -> Unit = {
    Text(
        text = stringResource(id = R.string.contacts),
        style = LocalTextStyle.current.copy(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onPrimary
        )
    )
}


/**
 * Content that appears when the [CollapsingTopBar] is expanded
 * */
val ExpandedTitleText: @Composable () -> Unit = {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(46.dp),
            painter = painterResource(id = R.drawable.ic_baseline_contacts_24),
            contentDescription = "Contacts icon",
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimary)
        )
        Text(
            stringResource(id = R.string.contacts),
            style = LocalTextStyle.current.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

/**
 * Text that appears in the subtitle slot of the
 * [CollapsingTopBar][com.germainkevin.collapsingtopbar.CollapsingTopBar]
 * */
val SubtitleText: @Composable (List<String>) -> Unit = { contactNames ->
    Text(
        text = stringResource(id = R.string.contactNamesCount, contactNames.size.toString()),
        style = LocalTextStyle.current.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onPrimary
        )
    )
}

/**
 * [IconButton] that appears in the navigationIcon slot of the
 * [CollapsingTopBar][com.germainkevin.collapsingtopbar.CollapsingTopBar]
 * */
val NavigationIcon: @Composable () -> Unit = {
    IconButton(onClick = {}) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = stringResource(id = R.string.hamburger_menu),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

/**
 * [IconButton]s that appear in the actions slot of the
 * [CollapsingTopBar][com.germainkevin.collapsingtopbar.CollapsingTopBar]
 * */
val MoreMenuIcons: @Composable (CollapsingTopBarScrollBehavior, Boolean, Boolean) -> Unit =
    { scrollBehavior, isCollapsed, isExpanded ->
        IconButton(onClick = { }) {
            Icon(
                Icons.Outlined.Add,
                contentDescription = Icons.Outlined.Add.name,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        IconButton(onClick = {
            if (isExpanded) {
                scrollBehavior.collapse(delay = 10L, steps = 5.dp)
            } else if (isCollapsed) {
                scrollBehavior.expand(delay = 10L, steps = 5.dp)
            }
        }) {
            val currentStateIcon =
                if (isCollapsed) Icons.Default.KeyboardArrowDown
                else if (isExpanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.MoreHoriz
            Icon(
                imageVector = currentStateIcon,
                contentDescription = currentStateIcon.name,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

val collapsingTopBarColors: @Composable (Window) -> CollapsingTopBarColors = { window ->
    CollapsingTopBarDefaults.colors(
        // This will be the color of the CollapsingTopBar when its state
        // is CollapsingTopBarState.IN_BETWEEN
        backgroundColorWhenCollapsingOrExpanding = MaterialTheme.colorScheme.onPrimaryContainer,
        onBackgroundColorChange = {
            // Changes the status bar color to the current background color of the
            // CollapsingTopBar
            window.statusBarColor = it.toArgb()
        },
    )
}