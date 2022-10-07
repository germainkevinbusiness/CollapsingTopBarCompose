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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.germainkevin.collapsingtopbar.*
import com.germainkevin.collapsingtopbarcompose.R


/**
 * Text that appears in the title slot of the
 * [CollapsingTopBar][com.germainkevin.collapsingtopbar.CollapsingTopBar]
 * */
val TitleText: @Composable () -> Unit = {
    val titleText = stringResource(id = R.string.contacts)
    Text(
        text = titleText,
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
    val expandedTitleText = stringResource(id = R.string.contacts)
        Image(
            modifier = Modifier.size(42.dp),
            painter = painterResource(id = R.drawable.ic_baseline_contacts_24),
            contentDescription = "Contacts icon",
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimary)
        )
        Text(
            text = expandedTitleText,
            style = LocalTextStyle.current.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onPrimary
            )
        )
}

/**
 * Text that appears in the subtitle slot of the
 * [CollapsingTopBar][com.germainkevin.collapsingtopbar.CollapsingTopBar]
 * */
val SubtitleText: @Composable (List<String>) -> Unit = { contactNames ->
    Text(
        text = "adnfabkfas.fdb.saf.djahbs.fhdsfsah.fdafaf.dfkhdsafk.akds.hfksa.fhsfk.s",
        style = LocalTextStyle.current.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
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
val MoreMenuIcons: @Composable (CollapsingTopBarScrollBehavior) -> Unit = { scrollBehavior ->
    IconButton(onClick = {
        if (scrollBehavior.isExpanded) {
            scrollBehavior.collapse(delay = 10L, steps = 5.dp)
        } else if (scrollBehavior.isCollapsed) {
            scrollBehavior.expand(delay = 10L, steps = 5.dp)
        }
    }) {
        val currentStateIcon =
            if (scrollBehavior.isCollapsed) Icons.Default.KeyboardArrowDown
            else if (scrollBehavior.isExpanded) Icons.Default.KeyboardArrowUp
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