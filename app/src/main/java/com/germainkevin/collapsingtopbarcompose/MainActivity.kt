package com.germainkevin.collapsingtopbarcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.germainkevin.collapsingtopbar.CollapsingTopBar
import com.germainkevin.collapsingtopbar.CollapsingTopBarState
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
                    centeredTitleWhenCollapsed = false,
                    centeredTitleAndSubtitle = true,
                    expandedTopBarMaxHeight = 156.dp,
                )
                val isCollapsed = scrollBehavior.currentState == CollapsingTopBarState.COLLAPSED
                val isExpanded = scrollBehavior.currentState == CollapsingTopBarState.EXPANDED
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                ) {
                    CollapsingTopBar(
                        scrollBehavior = scrollBehavior,
                        colors = collapsingTopBarColors(window),
                        title = TitleText,
                        expandedTitle = ExpandedTitleText,
                        subtitle = { SubtitleText(contacts) },
                        navigationIcon = { NavigationIcon() },
                        actions = { MoreMenuIcons(scrollBehavior, isCollapsed, isExpanded) },
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
                                    .clickable { },
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