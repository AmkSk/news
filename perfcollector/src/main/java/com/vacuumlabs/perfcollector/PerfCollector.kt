package com.vacuumlabs.perfcollector

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.FrameMetrics
import android.view.Window
import androidx.core.content.FileProvider
import com.google.gson.Gson
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.SortedMap
import java.util.TreeMap

class PerfCollector(
    val warningLevelMs: Long = 1000 / 60,
    val errorLevelMs: Long = 1000 / 24,
    val showWarning: Boolean = true,
    val showError: Boolean = true,
) {

    private val frameMetricsAvailableListenerMap: MutableMap<String, Window.OnFrameMetricsAvailableListener> =
        HashMap()

    val collectedMetrics: SortedMap<Int, Int> =
        TreeMap()

    private val collectedImageLoads: MutableList<Long> =
        mutableListOf()

    fun startFrameMetrics(activity: Activity) {
        val activityName = activity.javaClass.simpleName
        collectedMetrics.clear()
        val listener: Window.OnFrameMetricsAvailableListener =
            object : Window.OnFrameMetricsAvailableListener {
                private var allFrames = 0
                private var jankyFrames = 0
                override fun onFrameMetricsAvailable(
                    window: Window,
                    frameMetrics: FrameMetrics,
                    dropCountSinceLastInvocation: Int
                ) {
                    val frameMetricsCopy = FrameMetrics(frameMetrics)
                    allFrames++
                    val totalDurationMs =
                        (0.000001 * frameMetricsCopy.getMetric(FrameMetrics.TOTAL_DURATION)).toFloat()
                    if (frameMetricsCopy.getMetric(FrameMetrics.FIRST_DRAW_FRAME) == 0L) {
                        collectedMetrics[totalDurationMs.toInt()] =
                            (collectedMetrics[totalDurationMs.toInt()] ?: 0) + 1
                    }
                    if (totalDurationMs > warningLevelMs) {
                        jankyFrames++
                        var msg = String.format(
                            "Janky frame detected on %s with total duration: %.2fms\n",
                            activityName,
                            totalDurationMs
                        )
                        val layoutMeasureDurationMs =
                            (0.000001 * frameMetricsCopy.getMetric(FrameMetrics.LAYOUT_MEASURE_DURATION)).toFloat()
                        val drawDurationMs =
                            (0.000001 * frameMetricsCopy.getMetric(FrameMetrics.DRAW_DURATION)).toFloat()
                        val gpuCommandMs =
                            (0.000001 * frameMetricsCopy.getMetric(FrameMetrics.COMMAND_ISSUE_DURATION)).toFloat()
                        val othersMs =
                            totalDurationMs - layoutMeasureDurationMs - drawDurationMs - gpuCommandMs
                        val jankyPercent = jankyFrames.toFloat() / allFrames * 100
                        msg += String.format(
                            "Layout/measure: %.2fms, draw:%.2fms, gpuCommand:%.2fms others:%.2fms\n",
                            layoutMeasureDurationMs, drawDurationMs, gpuCommandMs, othersMs
                        )
                        msg += "Janky frames: $jankyFrames/$allFrames($jankyPercent%)"
                        if (showWarning && totalDurationMs > errorLevelMs) {
                            Log.e("FrameMetrics", msg)
                        } else if (showError) {
                            Log.w("FrameMetrics", msg)
                        }
                    }
                }
            }
        activity.window.addOnFrameMetricsAvailableListener(
            listener,
            Handler(Looper.getMainLooper())
        )
        frameMetricsAvailableListenerMap[activityName] = listener
    }

    fun stopFrameMetrics(activity: Activity) {
        val activityName = activity.javaClass.name
        val onFrameMetricsAvailableListener = frameMetricsAvailableListenerMap[activityName]
        if (onFrameMetricsAvailableListener != null) {
            activity.window.removeOnFrameMetricsAvailableListener(
                onFrameMetricsAvailableListener
            )
            frameMetricsAvailableListenerMap.remove(activityName)
        }
    }

    fun printCollectedMetrics(): String {
        val metricsData = MetricsData()
        Log.w("FrameMetrics", "Collected FrameMetrics")
        var frameCount = 0
        collectedMetrics.forEach { frameCount += it.value }
        Log.w("FrameMetrics", "Frame count = $frameCount")
        if (frameCount <= 0) return Gson().toJson(metricsData)
        Log.w("FrameMetrics", "Averages:")
        var percentileCount = 0
        var percentile50 = 0
        var percentile90 = 0
        var percentile95 = 0
        var percentile99 = 0
        collectedMetrics.forEach {
            percentileCount += it.value
            if (percentile50 == 0 && percentileCount > frameCount.toFloat() * 0.5) {
                percentile50 = it.key
            }
            if (percentile90 == 0 && percentileCount > frameCount.toFloat() * 0.90) {
                percentile90 = it.key
            }
            if (percentile95 == 0 && percentileCount > frameCount.toFloat() * 0.95) {
                percentile95 = it.key
            }
            if (percentile99 == 0 && percentileCount > frameCount.toFloat() * 0.99) {
                percentile99 = it.key
            }
        }
        Log.w("FrameMetrics", "50th percentile: ${percentile50}ms")
        Log.w("FrameMetrics", "90th percentile: ${percentile90}ms")
        Log.w("FrameMetrics", "95th percentile: ${percentile95}ms")
        Log.w("FrameMetrics", "99th percentile: ${percentile99}ms")
        metricsData.percentile50ms = percentile50
        metricsData.percentile90ms = percentile90
        metricsData.percentile95ms = percentile95
        metricsData.percentile99ms = percentile99


        Log.w("FrameMetrics", "Histogram:")
        collectedMetrics.forEach {
            Log.w("FrameMetrics", "Frame ms=${it.key} count=${it.value}")
        }
        metricsData.frameData = collectedMetrics

        Log.w("FrameMetrics", "Data as FPS:")
        Log.w("FrameMetrics", "50th percentile: ${1000 / percentile50}")
        Log.w("FrameMetrics", "90th percentile: ${1000 / percentile90}")
        Log.w("FrameMetrics", "95th percentile: ${1000 / percentile95}")
        Log.w("FrameMetrics", "99th percentile: ${1000 / percentile99}")
        Log.w("FrameMetrics", "")
        Log.w("FrameMetrics", "")
        metricsData.percentile50fps = 1000 / percentile50
        metricsData.percentile90fps = 1000 / percentile90
        metricsData.percentile95fps = 1000 / percentile95
        metricsData.percentile99fps = 1000 / percentile99

        val totalImageSize = collectedImageLoads.sum()
        Log.w(
            "FrameMetrics", "Total size of loaded images MB = ${
                "%,.2f".format(Locale.ENGLISH, totalImageSize.toFloat() / 1000000)
            }, bytes = $totalImageSize"
        )
        metricsData.imageLoads = collectedImageLoads

        Log.w("FrameMetrics", "ImageLoads:")
        collectedImageLoads.forEach {
            Log.w("FrameMetrics", "Image size = $it")
        }

        return Gson().toJson(metricsData)
    }

    fun sendAndPrint(context: Context) {
        val file = File(
            context.filesDir, "collected-data-${Build.MANUFACTURER}-${Build.MODEL}-${
                LocalDateTime.now().format(
                    DateTimeFormatter.BASIC_ISO_DATE
                )
            }.json"
        )
        val serializedMetrics = printCollectedMetrics()
        file.writeText(serializedMetrics)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("martin.vodila@vacuumlabs.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Compose - Data")
        intent.putExtra(Intent.EXTRA_TEXT, serializedMetrics)
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                file
            )
        )
        context.startActivity(Intent.createChooser(intent, "Pick email client:"))
    }

    fun reportImageLoad(imageSize: Long) {
        collectedImageLoads.add(imageSize)
    }

    fun resetMetrics() {
        collectedMetrics.clear()
        collectedImageLoads.clear()
    }
}