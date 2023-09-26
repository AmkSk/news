package com.vacuumlabs.performancecheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.vacuumlabs.perfcollector.PerfCollector
import com.vacuumlabs.performancecheck.ui.Routes
import com.vacuumlabs.performancecheck.ui.addRoutes
import com.vacuumlabs.performancecheck.ui.theme.PerformanceCheckTheme


class MainActivity : ComponentActivity() {

    private val perfCollector = PerfCollector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PerformanceCheckTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Routes.Intro.name) {
                    addRoutes(perfCollector = perfCollector, navController = navController)
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        perfCollector.startFrameMetrics(this)
    }

    override fun onPause() {
        perfCollector.stopFrameMetrics(this)
        super.onPause()
        perfCollector.printCollectedMetrics()
    }

}

