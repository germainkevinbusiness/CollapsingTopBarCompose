package com.germainkevin.collapsingtopbarcompose

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.germainkevin.collapsingtopbar.*
import com.germainkevin.collapsingtopbarcompose.ui.theme.CollapsingTopBarComposeTheme

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
                    CollapsingTopBarExample(context)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingTopBarExample(context: Context) {
    val scrollBehavior = remember {
        CollapsingTopBarDefaults
            .collapsingTopBarScrollBehavior(
                isAlwaysCollapsed = false,
                expandedTopBarMaxHeight = 256.dp,
                expandedTitleTextStyle = DefaultExpandedTitleTextStyle.copy(
                    fontSize = 48.sp,
                    color = Color.Red
                ),
                expandedSubtitleTextStyle = DefaultExpandedSubtitleTextStyle.copy(color = Color.Green)
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
                        "All contacts",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                subtitle = {
                    Text(
                        "${contactNames.size} contacts",
                        color = Color.DarkGray
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                    }) {
                        Icon(
                            Icons.Outlined.MoreVert,
                            contentDescription = "More menu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
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

@Composable
private fun ContactListNames(context: Context, contactName: String) {
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

private fun createToast(context: Context, message: String) =
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()

val contactNames = listOf(
    "Andre Bosan", "Emiliano Martinez", "Ibrahim Hovis", "Omaz", "Ubanohf", "Yerri Mina",
    "Benzema", "Jonahtan", "David Jamez", "Girano Sanri", "Himenez Verdan", "Kalib Mandi",
    "Andre Bosan", "Emiliano Martinez", "Ibrahim Hovis", "Omaz", "Ubanohf", "Yerri Mina",
)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CollapsingTopBarComposeTheme {
    }
}