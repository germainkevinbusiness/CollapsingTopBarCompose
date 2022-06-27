package com.germainkevin.collapsingtopbarcompose

import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun createToast(context: Context, message: String) =
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()