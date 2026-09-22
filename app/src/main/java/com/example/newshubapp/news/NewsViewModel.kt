package com.example.newshubapp.news

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newshubapp.data.models.Article
import com.example.newshubapp.data.models.WeatherResponse
import com.example.newshubapp.data.repository.NewsRepository
import com.example.newshubapp.utils.Constants
import kotlinx.coroutines.launch

/**
 * NewsViewModel handles news, weather, bookmarks, and search
 */
class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NewsRepository(application)
    private val TAG = "NewsViewModel"

    // ---------- NEWS ----------
    private val _news = MutableLiveData<List<Article>>()
    val news: LiveData<List<Article>> = _news

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // ---------- BOOKMARKS ----------
    private val _bookmarks = MutableLiveData<List<Article>>()
    val bookmarks: LiveData<List<Article>> = _bookmarks

    private val _isBookmarksLoading = MutableLiveData<Boolean>()
    val isBookmarksLoading: LiveData<Boolean> = _isBookmarksLoading

    // ---------- WEATHER ----------
    private val _weather = MutableLiveData<WeatherResponse?>()
    val weather: LiveData<WeatherResponse?> = _weather

    private val _weatherLocation = MutableLiveData<String?>()
    val weatherLocation: LiveData<String?> = _weatherLocation

    private val _isInFreeState = MutableLiveData<Boolean>()
    val isInFreeState: LiveData<Boolean> = _isInFreeState

    private val _isWeatherLoading = MutableLiveData<Boolean>()
    val isWeatherLoading: LiveData<Boolean> = _isWeatherLoading

    private val _weatherErrorMessage = MutableLiveData<String?>()
    val weatherErrorMessage: LiveData<String?> = _weatherErrorMessage

    // ---------- SEARCH STATE ----------
    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> = _isSearching

    // Track current category & search
    private var currentCategory: String? = null
    private var currentSearchQuery: String = ""

    init {
        Log.d(TAG, "NewsViewModel initialized")
        loadHeadlines()
    }

    // =====================================================
    // NEWS
    // =====================================================

    fun loadHeadlines(category: String? = null) {
        currentCategory = category
        currentSearchQuery = ""
        _searchQuery.value = ""
        _isSearching.value = false
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading headlines for category: ${category ?: "General"}")
                val articles = repository.getHeadlines(category)
                if (articles.isNotEmpty()) {
                    _news.value = articles
                    Log.d(TAG, "Loaded ${articles.size} articles")
                } else {
                    _errorMessage.value = "No news available"
                    _news.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading headlines: ${e.message}")
                _errorMessage.value = "Failed to load news: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshNews() {
        if (currentSearchQuery.isNotEmpty()) {
            // Re-run search
            searchArticles(currentSearchQuery)
        } else {
            Log.d(TAG, "Refreshing news")
            loadHeadlines(currentCategory)
        }
    }

    // =====================================================
    // SEARCH
    // =====================================================

    /**
     * Search for articles by keyword.
     * Empty query reloads the default headlines.
     */
    fun searchArticles(query: String) {
        if (query.isBlank()) {
            Log.d(TAG, "Empty search — loading default headlines")
            loadHeadlines(currentCategory)
            return
        }

        currentSearchQuery = query
        _searchQuery.value = query
        _isSearching.value = true
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(TAG, "Searching for: $query")
                val articles = repository.searchArticles(query)
                if (articles.isNotEmpty()) {
                    _news.value = articles
                    Log.d(TAG, "Search returned ${articles.size} articles")
                } else {
                    _errorMessage.value = "No results found for: $query"
                    _news.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Search error: ${e.message}")
                _errorMessage.value = "Search failed: ${e.message}"
            } finally {
                _isLoading.value = false
                _isSearching.value = false
            }
        }
    }

    /**
     * Clear search and reload headlines
     */
    fun clearSearch() {
        Log.d(TAG, "Clearing search")
        currentSearchQuery = ""
        _searchQuery.value = ""
        _isSearching.value = false
        loadHeadlines(currentCategory)
    }

    // =====================================================
    // BOOKMARKS
    // =====================================================

    fun toggleBookmark(article: Article) {
        viewModelScope.launch {
            try {
                repository.toggleBookmark(article)

                val currentList = _news.value?.toMutableList() ?: return@launch
                val index = currentList.indexOfFirst { it.url == article.url }
                if (index != -1) {
                    currentList[index] = currentList[index].copy(
                        isBookmarked = !currentList[index].isBookmarked
                    )
                    _news.value = currentList
                }

                loadBookmarks()
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling bookmark: ${e.message}")
            }
        }
    }

    fun loadBookmarks() {
        _isBookmarksLoading.value = true
        viewModelScope.launch {
            try {
                val bookmarksList = repository.getBookmarks()
                _bookmarks.value = bookmarksList
                Log.d(TAG, "Loaded ${bookmarksList.size} bookmarks")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading bookmarks: ${e.message}")
                _bookmarks.value = emptyList()
            } finally {
                _isBookmarksLoading.value = false
            }
        }
    }

    suspend fun getBookmarks(): List<Article> {
        return try {
            repository.getBookmarks()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bookmarks: ${e.message}")
            emptyList()
        }
    }

    // =====================================================
    // WEATHER
    // =====================================================

    fun getWeather(latitude: Double, longitude: Double) {
        _isWeatherLoading.value = true
        _weatherErrorMessage.value = null

        viewModelScope.launch {
            try {
                Log.d(TAG, "Getting weather for: $latitude, $longitude")

                if (repository.isInFreeState(latitude, longitude)) {
                    Log.d(TAG, "Location is in Free State")
                    _isInFreeState.value = true

                    val weatherData = repository.getWeather(latitude, longitude)
                    if (weatherData != null) {
                        _weather.value = weatherData
                        _weatherLocation.value = getLocationName(latitude, longitude)
                        Log.d(TAG, "Weather loaded: ${weatherData.current.temperature}°C")
                    } else {
                        _weatherErrorMessage.value = "Unable to fetch weather data"
                    }
                } else {
                    Log.d(TAG, "Location is outside Free State")
                    _isInFreeState.value = false
                    _weatherErrorMessage.value =
                        "Weather services are currently available for supported Free State locations only."
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting weather: ${e.message}")
                _weatherErrorMessage.value = "Failed to get weather"
            } finally {
                _isWeatherLoading.value = false
            }
        }
    }

    private fun getLocationName(latitude: Double, longitude: Double): String? {
        return Constants.FREE_STATE_LOCATIONS.firstOrNull {
            Math.abs(it.latitude - latitude) < 0.01 &&
                    Math.abs(it.longitude - longitude) < 0.01
        }?.name ?: "Free State"
    }
}