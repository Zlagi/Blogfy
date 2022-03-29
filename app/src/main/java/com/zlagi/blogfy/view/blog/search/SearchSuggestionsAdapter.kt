package com.zlagi.blogfy.view.blog.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zlagi.blogfy.databinding.SearchSuggestionItemBinding
import com.zlagi.blogfy.view.blog.search.SearchSuggestionsAdapter.SearchViewHolder
import com.zlagi.presentation.model.SearchSuggestionPresentationModel
import com.zlagi.presentation.model.SearchSuggestionPresentationModelNullable

class SearchSuggestionsAdapter(
    val onClick: (SearchSuggestionPresentationModelNullable) -> Unit
) : ListAdapter<SearchSuggestionPresentationModel, SearchViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        return SearchViewHolder(
            SearchSuggestionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class SearchViewHolder(
        private val binding: SearchSuggestionItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchSuggestionPresentationModel) {
            binding.apply {
                searchSuggestionTitle.text = item.query
                searchSuggestionItemIconClicked(binding, SearchSuggestionPresentationModelNullable(item.pk, null))
                searchSuggestionItemTitleClicked(binding, SearchSuggestionPresentationModelNullable(null, item.query))
            }
        }
    }

    private fun searchSuggestionItemTitleClicked(
        binding: SearchSuggestionItemBinding,
        searchSuggestionItem: SearchSuggestionPresentationModelNullable
    ) {
        binding.searchSuggestionTitle.setOnClickListener {
            onClick(searchSuggestionItem)
        }
    }

    @SuppressLint("UseValueOf")
    private fun searchSuggestionItemIconClicked(
        binding: SearchSuggestionItemBinding,
        searchSuggestionItem: SearchSuggestionPresentationModelNullable
    ) {
        binding.searchSuggestionClearButton.setOnClickListener {
            onClick(searchSuggestionItem)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<SearchSuggestionPresentationModel>() {

        override fun areItemsTheSame(
            oldItem: SearchSuggestionPresentationModel,
            newItem: SearchSuggestionPresentationModel
        ) =
            oldItem.pk == newItem.pk

        override fun areContentsTheSame(
            oldItem: SearchSuggestionPresentationModel,
            newItem: SearchSuggestionPresentationModel
        ) =
            oldItem == newItem
    }
}
