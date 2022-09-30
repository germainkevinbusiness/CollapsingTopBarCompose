package com.germainkevin.collapsingtopbarcompose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.germainkevin.collapsingtopbar.CollapsingTopBar
import com.germainkevin.collapsingtopbar.CollapsingTopBarScrollBehavior
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
val SubtitleText: @Composable (Array<String>) -> Unit = { contactNames ->
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
val NavigationIcon: @Composable (() -> Unit) -> Unit = { openLeftDrawer ->
    IconButton(onClick = openLeftDrawer) {
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
val MoreMenuIcons: @Composable () -> Unit = {
    IconButton(onClick = { }) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = stringResource(id = R.string.action_search),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
    IconButton(onClick = {
    }) {
        Icon(
            Icons.Outlined.MoreVert,
            contentDescription = stringResource(id = R.string.more_menu_desc),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeftDrawer(
    closeLeftDrawer: () -> Unit
) {
    // icons to mimic drawer destinations
    val drawerItems = listOf(Icons.Default.Contacts, Icons.Default.Settings, Icons.Default.Email)
    val selectedItem = remember { mutableStateOf(drawerItems[0]) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        drawerItems.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item, contentDescription = null) },
                label = { Text(item.name.replace("Filled.", "")) },
                selected = item == selectedItem.value,
                onClick = {
                    selectedItem.value = item
                    closeLeftDrawer()
                },
                shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                badge = { Text(text = "20") },
                modifier = Modifier.padding(end = 16.dp, top = 12.dp, bottom = 12.dp)
            )
        }
    }
}