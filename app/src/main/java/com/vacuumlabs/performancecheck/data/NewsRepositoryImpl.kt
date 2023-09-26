package com.vacuumlabs.performancecheck.data

import android.util.Log
import com.vacuumlabs.performancecheck.data.model.NewsApiResponse
import com.vacuumlabs.performancecheck.data.model.NewsItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


class NewsRepositoryImpl : NewsRepository {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun fetchArticles(): Result<List<NewsItem>> {
        Log.i("", "Calling fetchArticles()")
        return try {
            val response: NewsApiResponse =
                client.get(
                    "${API_URL}${REQUEST_PATH}" +
                            "?language=en" +
                            "&apiKey=${API_KEY}" +
                            "&pageSize=${PAGE_SIZE}"
                )
                    .body()
            Result.success(response.articles.filterEmptyItems())
        } catch (exception: Exception) {
            Log.e("ERROR", exception.stackTraceToString())
            Result.failure(exception)
        }
    }

    private fun List<NewsItem>.filterEmptyItems(): List<NewsItem> =
        this.filter { it.title != null && it.description != null && it.urlToImage != null && it.publishedAt != null }

    companion object {
        const val API_URL = "https://newsapi.org"
        const val REQUEST_PATH = "/v2/top-headlines"
        const val API_KEY = "03d0ecf80b5645af81d0c107c9139e6e"
        const val PAGE_SIZE = 100
    }
}