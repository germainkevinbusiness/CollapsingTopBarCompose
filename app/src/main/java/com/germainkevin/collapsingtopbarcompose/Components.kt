package com.germainkevin.collapsingtopbarcompose

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.germainkevin.collapsingtopbar.CollapsingTopBar
import com.germainkevin.collapsingtopbar.TopBarScrollBehavior

@Composable
fun CollapsingTopBarExample(scrollBehavior: TopBarScrollBehavior, contactNames: Array<String>) {
    CollapsingTopBar(
        scrollBehavior = scrollBehavior,
        centeredTitleAndSubtitle = true,
        title = {
            Text(
                stringResource(id = R.string.all_contacts),
                style = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        },
        subtitle = {
            Text(
                stringResource(
                    id = R.string.contactNamesCount,
                    contactNames.size.toString()
                ),
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(id = R.string.hamburger_menu),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = { MoreMenuIcons() },
    )
}

@Composable
fun ContactListNames(context: Context, contactName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { createToast(context, contactName) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = contactName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        )
        Divider()
    }
}

@Composable
fun MoreMenuIcons() {
    IconButton(onClick = { }) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = stringResource(id = R.string.action_search),
            tint = MaterialTheme.colorScheme.primary
        )
    }
    IconButton(onClick = {
    }) {
        Icon(
            Icons.Outlined.MoreVert,
            contentDescription = stringResource(id = R.string.more_menu_desc),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

fun createToast(context: Context, message: String) =
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()