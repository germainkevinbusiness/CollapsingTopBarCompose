package com.germainkevin.collapsingtopbarcompose

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.germainkevin.collapsingtopbar.*
import com.germainkevin.collapsingtopbarcompose.ui.*
import com.germainkevin.collapsingtopbarcompose.ui.theme.CollapsingTopBarComposeTheme

class MainActivity : ComponentActivity() {

    private val contacts = listOf(
        "Contact #1",
        "Contact #2",
        "Contact #3",
        "Contact #4",
        "Contact #5",
        "Contact #6",
        "Contact #7",
        "Contact #8",
        "Contact #9",
        "Contact #10",
        "Contact #11",
        "Contact #12",
        "Contact #13",
        "Contact #14",
        "Contact #15",
        "Contact #16",
        "Contact #17",
        "Contact #18",
        "Contact #19",
        "Contact #20",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollapsingTopBarComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val scaffoldState = rememberScaffoldState()
                    HomeScreen(
                        scaffoldState = scaffoldState,
                        window = this@MainActivity.window,
                        contacts = contacts
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    scaffoldState: ScaffoldState,
    contacts: List<String>,
    window: Window
) {
    val scrollBehavior = rememberCollapsingTopBarScrollBehavior(
        isAlwaysCollapsed = false,
        isExpandedWhenFirstDisplayed = false,
        centeredTitleWhenCollapsed = false,
        centeredTitleAndSubtitle = true,
        expandedTopBarMaxHeight = 200.dp,
    )
    val isCollapsed = scrollBehavior.currentState == CollapsingTopBarState.COLLAPSED
    val isExpanded = scrollBehavior.currentState == CollapsingTopBarState.EXPANDED
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        scaffoldState = scaffoldState,
        topBar = {
            CollapsingTopBar(
                scrollBehavior = scrollBehavior,
                colors = collapsingTopBarColors(window),
                title = TitleText,
                expandedTitle = ExpandedTitleText,
                subtitle = { SubtitleText(contacts) },
                navigationIcon = { NavigationIcon() },
                actions = { MoreMenuIcons(scrollBehavior, isCollapsed, isExpanded) },
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contacts) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .clickable { },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = it,
                        style = MaterialTheme.typography.bodyLarge
                            .copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                }
            }
        }
    }
}