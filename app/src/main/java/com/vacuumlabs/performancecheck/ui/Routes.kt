package com.vacuumlabs.performancecheck.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.vacuumlabs.perfcollector.PerfCollector
import com.vacuumlabs.performancecheck.ui.screen.AnimationsScreen
import com.vacuumlabs.performancecheck.ui.screen.IntroScreen
import com.vacuumlabs.performancecheck.ui.screen.NewsListScreen

enum class Routes {
    Intro,
    News,
    Animations,
}

fun NavGraphBuilder.addRoutes(
    perfCollector: PerfCollector,
    navController: NavHostController,
) {
    composable(Routes.Intro.name) {
        IntroScreen(
            perfCollector = perfCollector,
            navController = navController,
        )
    }
    composable(Routes.News.name) {
        NewsListScreen(perfCollector = perfCollector)
    }
    composable(Routes.Animations.name) {
        AnimationsScreen(perfCollector = perfCollector)
    }
}