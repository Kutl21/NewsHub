package com.example.newshubapp.data.repository

import android.content.Context
import android.util.Log
import com.example.newshubapp.data.api.ApiClient
import com.example.newshubapp.data.database.AppDatabase
import com.example.newshubapp.data.models.Article
import com.example.newshubapp.data.models.WeatherResponse
import com.example.newshubapp.utils.Constants
import com.example.newshubapp.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * NewsRepository - central data layer
 * Handles GNews API + Room DB
 */
class NewsRepository(private val context: Context) {

    private val articleDao = AppDatabase.getInstance(context).articleDao()

    companion object {
        private const val TAG = "NewsRepository"
    }

    /**
     * Get headlines for a category
     * Passing null or "General" fetches general headlines
     */
    suspend fun getHeadlines(category: String? = null): List<Article> {
        return withContext(Dispatchers.IO) {
            try {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    Log.d(TAG, "Fetching from GNews API, category: $category")

                    val apiCategory = if (category != null && category != "General") {
                        category.lowercase()
                    } else null

                    val response = ApiClient.newsApi.getTopHeadlines(
                        country = "za",
                        category = apiCategory
                    )

                    Log.d(TAG, "GNews response: ${response.totalArticles} total articles")

                    if (response.articles.isNotEmpty()) {
                        val articles = response.articles.map {
                            it.copy(category = category ?: "General")
                        }
                        articleDao.insertArticles(articles)
                        Log.d(TAG, "Fetched and cached ${articles.size} articles")
                        articles
                    } else {
                        Log.w(TAG, "GNews returned empty list — loading from cache")
                        articleDao.getAllArticles()
                    }
                } else {
                    Log.d(TAG, "Offline — loading from database")
                    articleDao.getAllArticles()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching headlines: ${e.message}")
                articleDao.getAllArticles()
            }
        }
    }

    /**
     * Search GNews
     */
    suspend fun searchArticles(query: String): List<Article> {
        return withContext(Dispatchers.IO) {
            try {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    val response = ApiClient.newsApi.searchArticles(query = query)
                    Log.d(TAG, "Search returned ${response.articles.size} articles")
                    response.articles
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Search error: ${e.message}")
                emptyList()
            }
        }
    }

    /**
     * Toggle bookmark state
     */
    suspend fun toggleBookmark(article: Article) {
        withContext(Dispatchers.IO) {
            try {
                val existing = articleDao.getArticleByUrl(article.url)
                if (existing != null) {
                    val updated = existing.copy(isBookmarked = !existing.isBookmarked)
                    articleDao.updateArticle(updated)
                    Log.d(TAG, "Toggled bookmark: ${article.title}")
                } else {
                    articleDao.insertArticle(article.copy(isBookmarked = true))
                    Log.d(TAG, "Added bookmark: ${article.title}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Bookmark error: ${e.message}")
            }
        }
    }

    /**
     * Get all bookmarks
     */
    suspend fun getBookmarks(): List<Article> = withContext(Dispatchers.IO) {
        try {
            articleDao.getBookmarkedArticles()
        } catch (e: Exception) {
            Log.e(TAG, "Bookmark retrieval error: ${e.message}")
            emptyList()
        }
    }

    /**
     * Get weather from Open-Meteo
     */
    suspend fun getWeather(latitude: Double, longitude: Double): WeatherResponse? {
        return withContext(Dispatchers.IO) {
            try {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    ApiClient.weatherApi.getCurrentWeather(latitude, longitude)
                } else null
            } catch (e: Exception) {
                Log.e(TAG, "Weather error: ${e.message}")
                null
            }
        }
    }

    /**
     * Check whether the coordinates are within the Free State province
     */
    fun isInFreeState(latitude: Double, longitude: Double): Boolean {
        return latitude in Constants.FREE_STATE_MIN_LAT..Constants.FREE_STATE_MAX_LAT &&
                longitude in Constants.FREE_STATE_MIN_LON..Constants.FREE_STATE_MAX_LON
    }
}