package com.vacuumlabs.perfcollector

data class MetricsData(
    var percentileCount: Int = 0,
    var percentile50ms: Int = 0,
    var percentile90ms: Int = 0,
    var percentile95ms: Int = 0,
    var percentile99ms: Int = 0,
    var percentile50fps: Int = 0,
    var percentile90fps: Int = 0,
    var percentile95fps: Int = 0,
    var percentile99fps: Int = 0,
    var frameData: MutableMap<Int, Int> = mutableMapOf(),
    var imageLoads: List<Long> = mutableListOf(),
)
