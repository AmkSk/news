package com.vacuumlabs.performancecheck.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.DataSource
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.vacuumlabs.perfcollector.PerfCollector
import com.vacuumlabs.performancecheck.ui.theme.PerformanceCheckTheme

/**
 * Represents one item in the list of news view
 */
@Composable
fun NewsListItem(
    perfCollector: PerfCollector, imageUrl: String, title: String, dateString: String, text: String
) {
    Column {
        ImageWithGradient(perfCollector, imageUrl, title)
        RoundedCornersText(dateString, text)
    }
}

@Suppress("OPT_IN_USAGE")
@Composable
private fun ImageWithGradient(
    perfCollector: PerfCollector,
    imageUrl: String,
    title: String,
) {
    Box {
        val context = LocalContext.current

        AsyncImage(
            modifier = Modifier.fillMaxWidth(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            onSuccess = { state ->
                if (state.result.dataSource == DataSource.NETWORK) {
                    val value = context.imageLoader.diskCache!![state.result.diskCacheKey!!]
                    val fileSize = value?.data?.toFile()?.length() ?: 0
                    perfCollector.reportImageLoad(fileSize)
                }
            },
        )
        // Adds the gradient on top of the image
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp),
                text = title,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RoundedCornersText(
    dateString: String, text: String
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 15.dp)
            .border(
                width = 3.dp,
                color = MaterialTheme.colors.primary,
                shape = RoundedCornerShape(10.dp)
            )

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(text = dateString, color = Color.Gray, fontStyle = FontStyle.Italic)
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = text,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
@Preview
private fun NewsListItemPreview() {
    PerformanceCheckTheme {
        NewsListItem(
            perfCollector = PerfCollector(),
            imageUrl = "https://a-static.projektn.sk/2015/04/04_02_2015_basketbal_Kosice_Praha_04221992.jpg",
            title = "Trae Young and his Antlant Hawks have nowhere to hide against the Bolton Celtics",
            dateString = "16 April 2023",
            text = "It took all 11 seconds for the Celtics to establish dominance over the Hawks in Game 1 of their first-round playoff series."
        )
    }
}