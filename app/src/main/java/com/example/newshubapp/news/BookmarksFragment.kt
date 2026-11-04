package com.example.newshubapp.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newshubapp.databinding.FragmentBookmarksBinding
import com.example.newshubapp.data.models.Article

/**
 * BookmarksFragment - Display saved articles
 */
class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private val newsViewModel: NewsViewModel by viewModels()
    private lateinit var adapter: NewsAdapter

    companion object {
        private const val TAG = "BookmarksFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        loadBookmarks()
    }

    /**
     * Setup RecyclerView for bookmarks
     */
    private fun setupRecyclerView() {
        adapter = NewsAdapter(
            onBookmarkClick = { article ->
                newsViewModel.toggleBookmark(article)
            }
        )
        binding.rvBookmarks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BookmarksFragment.adapter
        }
    }

    /**
     * Setup observers for LiveData
     */
    private fun setupObservers() {
        // Observe bookmarks changes
        newsViewModel.bookmarks.observe(viewLifecycleOwner) { bookmarks ->
            Log.d(TAG, "Bookmarks updated: ${bookmarks.size}")
            if (bookmarks.isNotEmpty()) {
                adapter.submitList(bookmarks)
                showBookmarksList()
            } else {
                showEmptyState()
            }
        }

        // Observe loading state
        newsViewModel.isBookmarksLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        newsViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                // Show error toast if needed
            }
        }
    }

    /**
     * Load bookmarks
     */
    private fun loadBookmarks() {
        newsViewModel.loadBookmarks()
    }

    /**
     * Show bookmarks list
     */
    private fun showBookmarksList() {
        binding.rvBookmarks.visibility = View.VISIBLE
        binding.llEmptyState.visibility = View.GONE
    }

    /**
     * Show empty state
     */
    private fun showEmptyState() {
        binding.rvBookmarks.visibility = View.GONE
        binding.llEmptyState.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}