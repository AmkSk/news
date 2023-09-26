package com.vacuumlabs.performancecheck.ui.view.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.vacuumlabs.performancecheck.R


@Composable
@OptIn(ExperimentalAnimationApi::class)
fun ExpandAnimation(expanded: Boolean, onClickAction: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable {
                onClickAction()
            }
            .size(width = 300.dp, height = 120.dp),
    ) {
        AnimatedContent(targetState = expanded, transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    150,
                    150
                )
            ) with fadeOut(animationSpec = tween(150)) using SizeTransform { initialSize, targetSize ->
                if (targetState) {
                    keyframes {
                        // Expand horizontally first.
                        IntSize(targetSize.width, initialSize.height) at 150
                        durationMillis = 300
                    }
                } else {
                    keyframes {
                        // Shrink vertically first.
                        IntSize(initialSize.width, targetSize.height) at 150
                        durationMillis = 300
                    }
                }
            }
        }) { targetExpanded ->
            if (targetExpanded) {
                Image(
                    bitmap = ImageBitmap.imageResource(R.drawable.logo),
                    contentDescription = "animated image",
                    modifier = Modifier
                        .size(width = 300.dp, height = 120.dp)
                        .background(color = Color.Green),
                )
            } else {
                Image(
                    bitmap = ImageBitmap.imageResource(R.drawable.logo),
                    contentDescription = "animated image",
                    modifier = Modifier
                        .size(width = 100.dp, height = 30.dp)
                        .background(color = Color.Green),
                )
            }
        }
    }
}