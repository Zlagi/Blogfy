package com.zlagi.blogfy.view.blog.search.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zlagi.blogfy.databinding.SearchHistoryItemBinding
import com.zlagi.blogfy.view.blog.search.history.SearchHistoryAdapter.SearchViewHolder
import com.zlagi.presentation.model.HistoryPresentationModel

enum class SearchAction {
    GET, DELETE
}

class SearchHistoryAdapter(
    val onClick: (SearchAction, HistoryPresentationModel) -> Unit
) : ListAdapter<HistoryPresentationModel, SearchViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        return SearchViewHolder(
            SearchHistoryItemBinding.inflate(
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
        private val binding: SearchHistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryPresentationModel) {
            binding.apply {
                searchHistoryTitle.text = item.query
                searchHistoryItemIconClicked(binding, HistoryPresentationModel(item.query))
                searchHistoryItemTitleClicked(binding, HistoryPresentationModel(item.query))
            }
        }
    }

    private fun searchHistoryItemTitleClicked(
        binding: SearchHistoryItemBinding,
        item: HistoryPresentationModel
    ) {
        binding.searchHistoryTitle.setOnClickListener {
            onClick(SearchAction.GET, item)
        }
    }

    @SuppressLint("UseValueOf")
    private fun searchHistoryItemIconClicked(
        binding: SearchHistoryItemBinding,
        item: HistoryPresentationModel
    ) {
        binding.searchHistoryClearButton.setOnClickListener {
            onClick(SearchAction.DELETE, item)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<HistoryPresentationModel>() {

        override fun areItemsTheSame(
            oldItem: HistoryPresentationModel,
            newItem: HistoryPresentationModel
        ) =
            oldItem.query == newItem.query

        override fun areContentsTheSame(
            oldItem: HistoryPresentationModel,
            newItem: HistoryPresentationModel
        ) =
            oldItem == newItem
    }
}
