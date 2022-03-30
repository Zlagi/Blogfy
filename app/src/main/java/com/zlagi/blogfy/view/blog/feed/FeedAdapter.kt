package com.zlagi.blogfy.view.blog.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.blogfy.databinding.FeedItemLayoutBinding
import com.zlagi.presentation.model.BlogPresentationModel


class FeedAdapter(
    private val firebaseStorage: FirebaseStorage,
    private val listener: OnItemSelectedListener<BlogPresentationModel> = { _, _ -> }
) : ListAdapter<BlogPresentationModel, FeedViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedViewHolder {
        val binding = FeedItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeedViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(position, getItem(position), firebaseStorage)
    }
}

private val DIFF_UTIL = object : DiffUtil.ItemCallback<BlogPresentationModel>() {
    override fun areItemsTheSame(
        oldItem: BlogPresentationModel,
        newItem: BlogPresentationModel
    ) =
        oldItem.pk == newItem.pk

    override fun areContentsTheSame(
        oldItem: BlogPresentationModel,
        newItem: BlogPresentationModel
    ) =
        oldItem == newItem
}
