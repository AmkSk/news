package com.vacuumlabs.performancecheck.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.vacuumlabs.perfcollector.PerfCollector
import com.vacuumlabs.performancecheck.ui.view.NewsListItem
import com.vacuumlabs.performancecheck.ui.viewmodel.NewsListViewModel

@Composable
fun NewsListScreen(
    perfCollector: PerfCollector,
    viewModel: NewsListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    Column {
        TopAppBar(
            title = { Text("News") },
            actions = {
                IconButton(onClick = { viewModel.fetchArticles() }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
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
        val articles = viewModel.articles.collectAsState(initial = emptyList())

        LazyColumn {
            items(articles.value.size) {
                val item = articles.value[it]
                NewsListItem(
                    perfCollector = perfCollector,
                    imageUrl = item.urlToImage ?: "",
                    title = item.title ?: "",
                    dateString = viewModel.formatDate(item.publishedAt ?: ""),
                    text = item.description ?: ""
                )
            }
        }
    }
}