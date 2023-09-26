package com.vacuumlabs.performancecheck.data.model

/**
 *
 */
@kotlinx.serialization.Serializable
class NewsApiResponse {
    var articles: List<NewsItem> = listOf()
}