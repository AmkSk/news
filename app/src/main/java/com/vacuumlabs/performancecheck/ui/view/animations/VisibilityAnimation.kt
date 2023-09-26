package com.vacuumlabs.performancecheck.ui.view.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.vacuumlabs.performancecheck.R

@Composable
fun VisibilityAnimation(visible: Boolean, onClickAction: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable {
                onClickAction()
            }
            .background(color = Color.Magenta)
            .size(width = 300.dp, height = 120.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically() + expandVertically(
                // Expand from the top.
                expandFrom = Alignment.Top
            ) + fadeIn(
                // Fade in with the initial alpha of 0.3f.
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        ) {
            Image(
                bitmap = ImageBitmap.imageResource(R.drawable.logo),
                contentDescription = "animated image 2",
                modifier = Modifier.size(width = 300.dp, height = 100.dp),
            )
        }
    }
}