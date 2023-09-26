package com.vacuumlabs.performancecheck.data

import com.vacuumlabs.performancecheck.data.model.NewsItem

/**
 * Interface representing a repository, that manages articles fetching
 */
interface NewsRepository {
    suspend fun fetchArticles(): Result<List<NewsItem>>
}