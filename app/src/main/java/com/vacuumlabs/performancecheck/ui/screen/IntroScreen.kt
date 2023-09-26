package com.vacuumlabs.performancecheck.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.imageLoader
import com.vacuumlabs.perfcollector.PerfCollector
import com.vacuumlabs.performancecheck.ui.Routes

@Composable
fun IntroScreen(
    perfCollector: PerfCollector,
    navController: NavHostController,

    ) {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("Intro") },
            actions = {
                IconButton(onClick = { perfCollector.sendAndPrint(context) }) {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
                IconButton(onClick = { perfCollector.resetMetrics() }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        )
        Text(text = "Scenarios:", style = TextStyle.Default.copy(fontSize = 20.sp))

        Button(
            onClick = {
                context.imageLoader.diskCache?.clear()
                perfCollector.resetMetrics()
                navController.navigate(Routes.News.name)
            },
            modifier = Modifier.semantics { contentDescription = "news" }
        ) {
            Text(text = "News List", color = Color.White)
        }

        Button(
            onClick = {
                perfCollector.resetMetrics()
                navController.navigate(Routes.Animations.name)
            },
        ) {
            Text(text = "Animations", color = Color.White)
        }
    }
}