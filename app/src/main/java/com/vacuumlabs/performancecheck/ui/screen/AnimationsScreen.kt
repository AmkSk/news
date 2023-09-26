package com.vacuumlabs.performancecheck.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.vacuumlabs.perfcollector.PerfCollector
import com.vacuumlabs.performancecheck.R
import com.vacuumlabs.performancecheck.ui.view.animations.ExpandAnimation
import com.vacuumlabs.performancecheck.ui.view.animations.VisibilityAnimation
import com.vacuumlabs.performancecheck.ui.viewmodel.AnimationsViewModel

@Composable
fun AnimationsScreen(
    perfCollector: PerfCollector,
    viewModel: AnimationsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    Column(
    ) {
        TopAppBar(
            title = { Text("Animations") },
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

        Animations(
            viewModel = viewModel,
        )
    }
}

@Composable
private fun Animations(
    viewModel: AnimationsViewModel,
) {
    var animationCanvasSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    Box(
        Modifier
            .fillMaxSize()
            .onSizeChanged {
                animationCanvasSize = it
            },
    ) {
        val baseSize = 64.dp

        (1..AnimationsViewModel.scalingElementsCount).forEach {
            ScalingImage(
                viewModel = viewModel,
                index = it - 1,
                animationCanvasSize = animationCanvasSize,
                baseSize = baseSize,
            )
        }

        (1..AnimationsViewModel.movingElementsCount).forEach {
            MovingImage(
                viewModel = viewModel,
                index = it - 1,
                animationCanvasSize = animationCanvasSize,
                baseSize = baseSize,
            )
        }


        (1..AnimationsViewModel.disappearingElementsCount).forEach {
            DisappearingImage(
                viewModel = viewModel,
                index = it - 1,
                animationCanvasSize = animationCanvasSize,
                baseSize = baseSize,
            )
        }

//        Experiments(expanded, visible)
    }
}

@Composable
private fun ScalingImage(
    viewModel: AnimationsViewModel,
    index: Int,
    animationCanvasSize: IntSize,
    baseSize: Dp,
) {
    val localPosition = viewModel.scalingLogoPositionsFlows[index].collectAsState()
    val expanding = viewModel.scalingLogoTriggeredFlows[index].collectAsState()
    val size by animateDpAsState(
        if (!expanding.value) baseSize else baseSize * 2,
        animationSpec = tween(
            durationMillis = AnimationsViewModel.cycleDuration.toInt(),
            easing = LinearEasing
        ),
    )
    val alpha by animateFloatAsState(
        if (!expanding.value) 0f else 1f,
        animationSpec = tween(
            durationMillis = AnimationsViewModel.cycleDuration.toInt(),
            easing = LinearEasing
        )
    )
    Image(
        imageVector = ImageVector.vectorResource(R.drawable.logo_symbol_rgb),
        contentDescription = "animated scale and opacity",
        modifier = Modifier
            .size(size)
            .absoluteOffset {
                IntOffset(
                    x = (localPosition.value.first * (animationCanvasSize.width - baseSize.toPx())).toInt(),
                    y = (localPosition.value.second * (animationCanvasSize.height - baseSize.toPx())).toInt()
                )
            }
            .alpha(alpha),
    )
}

@Composable
private fun MovingImage(
    viewModel: AnimationsViewModel,
    index: Int,
    animationCanvasSize: IntSize,
    baseSize: Dp,
) {
    val localPosition = viewModel.movingLogoPositionFlows[index].collectAsState()
    val baseSizePx = (LocalDensity.current.density * baseSize.value).toInt()
    val animationCanvasOffset by animateIntOffsetAsState(
        IntOffset(
            x = (localPosition.value.first * (animationCanvasSize.width - baseSizePx)).toInt(),
            y = (localPosition.value.second * (animationCanvasSize.height - baseSizePx)).toInt()
        ),
        animationSpec = tween(
            durationMillis = AnimationsViewModel.cycleDuration.toInt(),
            easing = LinearEasing,
        )
    )
    Image(
        imageVector = ImageVector.vectorResource(R.drawable.logo_symbol_rgb),
        contentDescription = "animated bouncing on screen - $index",
        modifier = Modifier
            .size(baseSize)
            .absoluteOffset {
                animationCanvasOffset
            },
    )
}

@Composable
private fun DisappearingImage(
    viewModel: AnimationsViewModel,
    index: Int,
    animationCanvasSize: IntSize,
    baseSize: Dp,
) {
    val localPosition = viewModel.disappearingLogoPositionsFlows[index].collectAsState()
    val visible = viewModel.disappearingLogoVisibilityFlows[index].collectAsState()
    val alpha by animateFloatAsState(
        if (visible.value) 0f else 1f,
        animationSpec = tween(
            durationMillis = AnimationsViewModel.cycleDuration.toInt(),
            easing = LinearEasing
        )
    )
    Image(
        imageVector = ImageVector.vectorResource(R.drawable.logo_symbol_rgb),
        contentDescription = "animated opacity - $index",
        modifier = Modifier
            .size(baseSize)
            .absoluteOffset {
                IntOffset(
                    x = (localPosition.value.first * (animationCanvasSize.width - baseSize.toPx())).toInt(),
                    y = (localPosition.value.second * (animationCanvasSize.height - baseSize.toPx())).toInt()
                )
            }
            .alpha(alpha),
    )
}

@Composable
private fun Experiments(
    expanded: State<Boolean>,
    visible: State<Boolean>
) {
    ExpandAnimation(
        expanded = expanded.value,
        onClickAction = { },
    )

    VisibilityAnimation(
        visible = visible.value,
        onClickAction = { },
    )
}
