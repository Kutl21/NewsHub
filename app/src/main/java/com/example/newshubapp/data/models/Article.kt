package com.example.newshubapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Article data model representing a news article
 * Reference: GNews API (https://gnews.io/docs/v4)
 */
@Entity(tableName = "articles")
data class Article(
    @PrimaryKey
    val url: String,
    @SerializedName("source")
    val source: Source?,
    val title: String,
    val description: String?,
    @SerializedName("image")
    val imageUrl: String?,
    @SerializedName("publishedAt")
    val publishedAt: String,
    val content: String? = null,
    var isBookmarked: Boolean = false,
    var category: String? = null
) : Serializable

/**
 * Source info from GNews
 */
data class Source(
    val name: String,
    val url: String? = null
)

/**
 * NewsResponse for GNews API
 * Key difference: uses "totalArticles" (not "totalResults")
 * and does NOT have a "status" field
 */









