package com.example.newshubapp.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newshubapp.R
import com.example.newshubapp.databinding.FragmentCategoriesBinding

/**
 * CategoriesFragment - Browse news by category
 * Displays categories in a grid layout with icons
 */
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val newsViewModel: NewsViewModel by viewModels()

    companion object {
        private const val TAG = "CategoriesFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoriesRecyclerView()
    }

    /**
     * Setup categories grid
     */
    private fun setupCategoriesRecyclerView() {
        val categories = resources.getStringArray(R.array.news_categories).toList()
        val icons = resources.getStringArray(R.array.category_icons).toList()

        val categoryItems = categories.mapIndexed { index, category ->
            val icon = if (index < icons.size) icons[index] else "📰"
            CategoryItem(name = category, icon = icon)
        }

        val adapter = CategoryAdapter(categoryItems) { category ->
            Log.d(TAG, "Category clicked: $category")

            // Load news for the selected category
            newsViewModel.loadHeadlines(category)

            Toast.makeText(requireContext(), "Loading $category news...", Toast.LENGTH_SHORT).show()

            // ✅ Navigate to Home tab so user sees the news
            val navController = findNavController()
            navController.navigate(R.id.navigation_home)
        }

        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = adapter
        }

        Log.d(TAG, "Categories setup complete: ${categoryItems.size} categories")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Data class for category item
 */
data class CategoryItem(
    val name: String,
    val icon: String
)

/**
 * CategoryAdapter for displaying categories in grid
 */
class CategoryAdapter(
    private val categories: List<CategoryItem>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)

        fun bind(category: CategoryItem) {
            tvCategoryIcon.text = category.icon
            tvCategoryName.text = category.name

            itemView.setOnClickListener {
                onItemClick(category.name)
            }
        }
    }
}