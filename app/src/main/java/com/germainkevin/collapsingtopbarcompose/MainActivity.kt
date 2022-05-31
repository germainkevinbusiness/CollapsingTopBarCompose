package com.germainkevin.collapsingtopbarcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.germainkevin.collapsingtopbar.CollapsingTopBar
import com.germainkevin.collapsingtopbar.CollapsingTopBarDefaults
import com.germainkevin.collapsingtopbarcompose.ui.theme.CollapsingTopBarComposeTheme

@OptIn(ExperimentalMaterial3Api::class)
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
                    val context = LocalContext.current
                    val contactNames = context.resources.getStringArray(R.array.contactNames)
                    // A scrollBehavior determines the behavior of the CollapsingTopBar
                    // when it is being scrolled and also to track the nestedScroll events
                    val scrollBehavior = remember {
                        CollapsingTopBarDefaults.behaviorOnScroll(
                            isAlwaysCollapsed = false,
                            expandedTopBarMaxHeight = 256.dp,
                        )
                    }
                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
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
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
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
                        },
                        content = { innerPadding ->
                            LazyColumn(
                                contentPadding = innerPadding,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(count = contactNames.size) {
                                    ContactListNames(context, contactNames[it])
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}