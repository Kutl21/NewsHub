package com.example.newshubapp.news

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newshubapp.R
import com.example.newshubapp.data.models.Article
import java.text.SimpleDateFormat
import java.util.*

/**
 * NewsAdapter for displaying articles in RecyclerView
 */
class NewsAdapter(
    private val onBookmarkClick: (Article) -> Unit
) : ListAdapter<Article, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    companion object {
        private const val TAG = "NewsAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivNewsImage: ImageView = itemView.findViewById(R.id.ivNewsImage)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvSource: TextView = itemView.findViewById(R.id.tvSource)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val btnBookmark: ImageButton = itemView.findViewById(R.id.btnBookmark)

        fun bind(article: Article) {
            tvTitle.text = article.title

            tvDescription.text = if (!article.description.isNullOrEmpty()) {
                article.description
            } else {
                "No description available"
            }

            tvSource.text = article.source?.name ?: "Unknown Source"
            tvTime.text = formatTime(article.publishedAt)

            if (!article.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(article.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(ivNewsImage)
            } else {
                ivNewsImage.setImageResource(R.drawable.placeholder_image)
            }

            updateBookmarkIcon(article.isBookmarked)

            // FIX: Open the article URL directly inside the adapter
            itemView.setOnClickListener {
                Log.d(TAG, "Opening article URL: ${article.url}")
                try {
                    val customTabsIntent = CustomTabsIntent.Builder().build()
                    customTabsIntent.launchUrl(itemView.context, android.net.Uri.parse(article.url))
                } catch (e: Exception) {
                    Log.e(TAG, "Error opening URL: ${e.message}")
                    Toast.makeText(itemView.context, "Could not open link", Toast.LENGTH_SHORT).show()
                }
            }

            btnBookmark.setOnClickListener {
                onBookmarkClick(article)
            }
        }

        private fun updateBookmarkIcon(isBookmarked: Boolean) {
            if (isBookmarked) {
                btnBookmark.setImageResource(R.drawable.ic_bookmark_filled)
                btnBookmark.setColorFilter(itemView.context.getColor(R.color.primary))
            } else {
                btnBookmark.setImageResource(R.drawable.ic_bookmark_border)
                btnBookmark.setColorFilter(null)
            }
        }
    }

    private fun formatTime(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)

            if (date != null) {
                val now = Date()
                val diff = now.time - date.time
                val minutes = diff / (1000 * 60)
                val hours = diff / (1000 * 60 * 60)
                val days = diff / (1000 * 60 * 60 * 24)

                when {
                    minutes < 1 -> "Just now"
                    minutes < 60 -> "${minutes}m ago"
                    hours < 24 -> "${hours}h ago"
                    days < 7 -> "${days}d ago"
                    else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
                }
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}