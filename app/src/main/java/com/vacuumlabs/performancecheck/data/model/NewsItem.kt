package com.vacuumlabs.performancecheck.data.model

/**
 * Item returned from the API
 */
@kotlinx.serialization.Serializable
class NewsItem {
    var urlToImage: String? = ""
    var title: String? = ""
    var publishedAt: String? = ""
    var description: String? = ""
}