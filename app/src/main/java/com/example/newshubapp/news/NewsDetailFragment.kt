package com.example.newshubapp.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.newshubapp.R
import com.example.newshubapp.data.models.Article
import com.example.newshubapp.databinding.FragmentNewsDetailBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * NewsDetailFragment - Display full article details
 */
class NewsDetailFragment : Fragment() {

    private var _binding: FragmentNewsDetailBinding? = null
    private val binding get() = _binding!!

    private val newsViewModel: NewsViewModel by viewModels()
    private var article: Article? = null

    companion object {
        private const val TAG = "NewsDetailFragment"
        private const val ARG_ARTICLE = "article"

        fun newInstance(article: Article): NewsDetailFragment {
            val fragment = NewsDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_ARTICLE, article)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        article = arguments?.getSerializable(ARG_ARTICLE) as? Article

        if (article != null) {
            displayArticle(article!!)
        } else {
            Toast.makeText(requireContext(), "Article not found", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }

        setupClickListeners()
    }

    private fun displayArticle(article: Article) {
        Log.d(TAG, "Displaying article: ${article.title}")

        binding.tvDetailTitle.text = article.title

        val source = article.source?.name ?: "Unknown Source"
        val date = formatDate(article.publishedAt)
        binding.tvDetailSource.text = "$source • $date"

        binding.tvDetailDescription.text = article.description ?: "No description available"

        binding.tvDetailContent.text = article.content?.replace("\\[.*?\\]".toRegex(), "")
            ?: "Full content not available. Tap 'Read Full Article' to view in browser."

        if (!article.imageUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(article.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(binding.ivDetailImage)
        } else {
            binding.ivDetailImage.visibility = View.GONE
        }

        updateBookmarkIcon(article.isBookmarked)
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnReadFull.setOnClickListener {
            article?.url?.let { url ->
                openInCustomTab(url)
            } ?: run {
                Toast.makeText(requireContext(), "Article URL not available", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBookmark.setOnClickListener {
            article?.let {
                newsViewModel.toggleBookmark(it)
                updateBookmarkIcon(!it.isBookmarked)
                Toast.makeText(
                    requireContext(),
                    if (!it.isBookmarked) "Added to bookmarks" else "Removed from bookmarks",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openInCustomTab(url: String) {
        try {
            Log.d(TAG, "Opening URL in Custom Tab: $url")
            val builder = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setInstantAppsEnabled(true)
                .build()
            builder.launchUrl(requireContext(), android.net.Uri.parse(url))
        } catch (e: Exception) {
            Log.e(TAG, "Error opening Custom Tab: ${e.message}")
            Toast.makeText(requireContext(), "Unable to open article", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBookmarkIcon(isBookmarked: Boolean) {
        if (isBookmarked) {
            binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_filled)
            binding.btnBookmark.setColorFilter(requireContext().getColor(R.color.primary))
        } else {
            binding.btnBookmark.setImageResource(R.drawable.ic_bookmark_border)
            binding.btnBookmark.setColorFilter(null)
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)
            if (date != null) {
                val outputFormat = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
                outputFormat.format(date)
            } else {
                "Unknown date"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting date: ${e.message}")
            "Unknown date"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}