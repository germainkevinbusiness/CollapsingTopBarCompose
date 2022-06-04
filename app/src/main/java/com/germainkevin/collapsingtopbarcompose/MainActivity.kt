package com.germainkevin.collapsingtopbarcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.germainkevin.collapsingtopbar.CollapsingTopBar
import com.germainkevin.collapsingtopbar.rememberCollapsingTopBarScrollBehavior
import com.germainkevin.collapsingtopbarcompose.ui.ContactListNames
import com.germainkevin.collapsingtopbarcompose.ui.LeftDrawer
import com.germainkevin.collapsingtopbarcompose.ui.MoreMenuIcons
import com.germainkevin.collapsingtopbarcompose.ui.theme.CollapsingTopBarComposeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollapsingTopBarComposeTheme {
                // A surface container using the 'background' color from the theme
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

                    val contactNames = stringArrayResource(id = R.array.contactNames)

                    /**
                     * A scrollBehavior determines the behavior of the CollapsingTopBar when it is
                     * being scrolled and also to track the nestedScroll events*/
                    val scrollBehavior = rememberCollapsingTopBarScrollBehavior(
                        isAlwaysCollapsed = false,
                        isExpandedWhenFirstDisplayed = true,
                        centeredTitleAndSubtitle = false,
                        expandedTopBarMaxHeight = 256.dp,
                    )
                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        scaffoldState = scaffoldState,
                        drawerContent = { LeftDrawer(closeLeftDrawer = closeLeftDrawer) },
                        topBar = {
                            CollapsingTopBar(
                                scrollBehavior = scrollBehavior,
                                title = {
                                    Text(
                                        stringResource(id = R.string.all_contacts),
                                        style = LocalTextStyle.current.copy(
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onPrimary
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
                                    IconButton(onClick = openLeftDrawer) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = stringResource(id = R.string.hamburger_menu),
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                },
                                actions = { MoreMenuIcons() },
                            )
                        },
                        content = { innerPadding ->
                            LazyColumn(
                                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                                contentPadding = innerPadding,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                                items(count = contactNames.size) {
                                    ContactListNames(
                                        context = LocalContext.current,
                                        contactName = contactNames[it]
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}