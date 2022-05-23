package com.germainkevin.collapsingtopbarcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
                        CollapsingTopBarDefaults
                            .collapsingTopBarScrollBehavior(
                                isAlwaysCollapsed = false,
                                expandedTopBarMaxHeight = 256.dp,
                            )
                    }
                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            CollapsingTopBarExample(
                                scrollBehavior = scrollBehavior,
                                contactNames = contactNames
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