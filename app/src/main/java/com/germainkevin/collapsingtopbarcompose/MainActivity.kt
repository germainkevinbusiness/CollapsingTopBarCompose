package com.germainkevin.collapsingtopbarcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.germainkevin.collapsingtopbar.CollapsingTopBar
import com.germainkevin.collapsingtopbar.rememberCollapsingTopBarScrollBehavior
import com.germainkevin.collapsingtopbarcompose.ui.*
import com.germainkevin.collapsingtopbarcompose.ui.theme.CollapsingTopBarComposeTheme

class MainActivity : ComponentActivity() {

    private val contacts = listOf(
        "Alejandro Balde", "Barella Nicolo", "Cristiano Ronaldo", "David Beckham",
        "Ernesto Valverde", "Federico Valverde", "Granit Xhaka", "Harry Kane",
        "Ilaix Moriba", "Jonathan Davis", "Kaka", "Lionel Andres Messi", "Mascherano",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollapsingTopBarComposeTheme {
                val scrollBehavior = rememberCollapsingTopBarScrollBehavior(
                    isAlwaysCollapsed = false,
                    isExpandedWhenFirstDisplayed = true,
                    centeredTitleWhenCollapsed = true,
                    centeredTitleAndSubtitle = true,
                    expandedTopBarMaxHeight = 200.dp,
                )
                val mainActionState = remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                ) {
                    CollapsingTopBar(
                        scrollBehavior = scrollBehavior,
//                        colors = collapsingTopBarColors(window),
                        title = TitleText,
                        expandedTitle = ExpandedTitleText,
                        subtitle = { SubtitleText(contacts) },
                        navigationIcon = { NavigationIcon() },
                        mainAction = {
                            IconButton(
                                onClick = {}) {
                                Icon(
                                    Icons.Outlined.Add,
                                    contentDescription = Icons.Outlined.Add.name,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        actions = { MoreMenuIcons(scrollBehavior) },
                    )

                    LazyColumn(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .fillMaxSize()
                    ) {
                        items(contacts) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        mainActionState.value = !mainActionState.value
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier.padding(16.dp),
                                    text = it,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}