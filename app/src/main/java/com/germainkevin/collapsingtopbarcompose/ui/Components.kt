package com.germainkevin.collapsingtopbarcompose.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.germainkevin.collapsingtopbarcompose.R
import com.germainkevin.collapsingtopbarcompose.createToast

@Composable
fun ContactListNames(context: Context, contactName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { createToast(context, contactName) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = contactName,
            style = MaterialTheme.typography.bodyLarge
                .copy(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun MoreMenuIcons() {
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
    drawerItems: List<ImageVector>,
    closeLeftDrawer: () -> Unit
) {
    val selectedItem = remember { mutableStateOf(drawerItems[0]) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        drawerItems.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item, contentDescription = null) },
                label = { Text(item.name) },
                selected = item == selectedItem.value,
                onClick = {
                    selectedItem.value = item
                    closeLeftDrawer()
                },
                shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                badge = { Text(text = " 20") },
                modifier = Modifier.padding(end = 16.dp, top = 12.dp, bottom = 12.dp)
            )
        }
    }
}