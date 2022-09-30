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
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import com.germainkevin.collapsingtopbar.*
import com.germainkevin.collapsingtopbarcompose.ui.*
import com.germainkevin.collapsingtopbarcompose.ui.theme.CollapsingTopBarComposeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollapsingTopBarComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    val scaffoldState = rememberScaffoldState()

                    val openLeftDrawer: () -> Unit = {
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }
                    val closeLeftDrawer: () -> Unit = {
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }
                    }

                    HomeScreen(
                        contactNames = stringArrayResource(id = R.array.contactNames),
                        scaffoldState = scaffoldState,
                        openLeftDrawer = openLeftDrawer,
                        closeLeftDrawer = closeLeftDrawer,
                        window = this@MainActivity.window
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    contactNames: Array<String>,
    scaffoldState: ScaffoldState,
    openLeftDrawer: () -> Unit,
    closeLeftDrawer: () -> Unit,
    window: Window
) {
    val scrollBehavior = rememberCollapsingTopBarScrollBehavior(
        isAlwaysCollapsed = false,
        isExpandedWhenFirstDisplayed = true,
        centeredTitleWhenCollapsed = false,
        centeredTitleAndSubtitle = true,
        expandedTopBarMaxHeight = 156.dp,
    )
    val context = LocalContext.current
    val isCollapsed = scrollBehavior.currentState == CollapsingTopBarState.COLLAPSED
    val isExpanded = scrollBehavior.currentState == CollapsingTopBarState.EXPANDED
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        scaffoldState = scaffoldState,
        drawerContent = { LeftDrawer(closeLeftDrawer) },
        topBar = {
            CollapsingTopBar(
                scrollBehavior = scrollBehavior,
                colors = CollapsingTopBarDefaults.colors(
                    // This will be the color of the CollapsingTopBar when its state
                    // is CollapsingTopBarState.IN_BETWEEN
                    backgroundColorWhenCollapsingOrExpanding = MaterialTheme.colorScheme.onPrimaryContainer,
                    onBackgroundColorChange = {
                        // Changes the status bar color to the current background color of the
                        // CollapsingTopBar
                        window.statusBarColor = it.toArgb()
                    },
                ),
                title = TitleText,
                expandedTitle = ExpandedTitleText,
                subtitle = { SubtitleText(contactNames) },
                navigationIcon = { NavigationIcon(openLeftDrawer) },
                actions = { MoreMenuIcons() },
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
            }
            items(contactNames) { name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .clickable {
                            createToast(context, name)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        text = name,
                        style = MaterialTheme.typography.bodyLarge
                            .copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                    OutlinedButton(onClick = {
                        if (isExpanded) {
                            scrollBehavior.collapse(delay = 10L, steps = 5.dp)
                        } else if (isCollapsed) {
                            scrollBehavior.expand(delay = 10L, steps = 5.dp)
                        }
                    }) {
                        Text(
                            text = if (isCollapsed) "Expand" else if (isExpanded) "Collapse" else "...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}