package com.example.newshubapp.data.database

import androidx.room.*
import com.example.newshubapp.data.models.Article

/**
 * Data Access Object for Article entity
 * Defines database operations for articles
 * Reference: https://developer.android.com/training/data-storage/room/accessing-data
 */
@Dao
interface ArticleDao {

    /**
     * Get all bookmarked articles
     * Sorted by publication date (newest first)
     */
    @Query("SELECT * FROM articles WHERE isBookmarked = 1 ORDER BY publishedAt DESC")
    suspend fun getBookmarkedArticles(): List<Article>

    /**
     * Get articles by category
     * Sorted by publication date (newest first)
     */
    @Query("SELECT * FROM articles WHERE category = :category ORDER BY publishedAt DESC")
    suspend fun getArticlesByCategory(category: String): List<Article>

    /**
     * Get all articles (capped at 100 for performance)
     * Sorted by publication date (newest first)
     */
    @Query("SELECT * FROM articles ORDER BY publishedAt DESC LIMIT 100")
    suspend fun getAllArticles(): List<Article>

    /**
     * Insert multiple articles
     * Replaces existing articles with same URL
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>)

    /**
     * Insert a single article
     * Replaces existing article with same URL
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article)

    /**
     * Update an existing article
     */
    @Update
    suspend fun updateArticle(article: Article)

    /**
     * Delete an article from the database
     */
    @Delete
    suspend fun deleteArticle(article: Article)

    /**
     * Get a specific article by URL
     * Returns null if not found
     */
    @Query("SELECT * FROM articles WHERE url = :url")
    suspend fun getArticleByUrl(url: String): Article?

    /**
     * Clear all articles from database
     * Used when refreshing data
     */
    @Query("DELETE FROM articles")
    suspend fun clearArticles()

    /**
     * Clear only unbookmarked articles
     * Keeps user's saved articles
     */
    @Query("DELETE FROM articles WHERE isBookmarked = 0")
    suspend fun clearUnbookmarkedArticles()
}