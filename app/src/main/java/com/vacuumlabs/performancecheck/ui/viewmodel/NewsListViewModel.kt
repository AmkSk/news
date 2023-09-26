package com.vacuumlabs.performancecheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vacuumlabs.performancecheck.data.NewsRepository
import com.vacuumlabs.performancecheck.data.NewsRepositoryImpl
import com.vacuumlabs.performancecheck.data.model.NewsItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

class NewsListViewModel : ViewModel() {

    private val repository: NewsRepository = NewsRepositoryImpl()

    val articles = MutableStateFlow<List<NewsItem>>(value = emptyList())

    init {
        fetchArticles()
    }

    fun fetchArticles() {
        viewModelScope.launch {
            articles.value = repository.fetchArticles().getOrNull() ?: emptyList()
        }
    }

    fun formatDate(dateString: String): String {
        val locale = Locale.getDefault()

        val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val outputFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)

        val localDateTime = LocalDate.parse(dateString, inputFormatter)
        val zonedDateTime = localDateTime.atStartOfDay(ZoneId.systemDefault())

        return outputFormatter.format(zonedDateTime)
    }
}