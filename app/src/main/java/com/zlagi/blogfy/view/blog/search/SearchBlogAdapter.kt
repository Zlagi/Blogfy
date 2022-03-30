package com.zlagi.blogfy.view.blog.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.blogfy.R
import com.zlagi.blogfy.databinding.SearchItemBinding
import com.zlagi.presentation.model.BlogPk
import com.zlagi.presentation.model.BlogPresentationModel
import io.github.rosariopfernandes.firecoil.load

class SearchBlogAdapter(
    val imageLoader: ImageLoader
) : ListAdapter<BlogPresentationModel, SearchBlogAdapter.SearchBlogViewHolder>(DiffCallback()) {

    private val storageRef = FirebaseStorage.getInstance()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchBlogViewHolder {
        return SearchBlogViewHolder(
            SearchItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: SearchBlogViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class SearchBlogViewHolder(
        private val binding: SearchItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseValueOf")
        fun bind(
            item: BlogPresentationModel
        ) {
            binding.run {
                blogTitle.text = item.title
                blogAuthor.text = item.username
                val lottieDrawable = LottieDrawable()
                LottieCompositionFactory.fromRawRes(itemView.context, R.raw.image_loader)
                    .addListener { lottieComposition ->
                        lottieDrawable.composition = lottieComposition
                        lottieDrawable.repeatCount = LottieDrawable.INFINITE
                        lottieDrawable.playAnimation()
                    }
                with(blogImage) {
                    transitionName = item.pk.toString()
                    if (item.updated.isNotEmpty()) load(storageRef.getReferenceFromUrl("gs://blogfy-e5b41.appspot.com/image/${item.updated}")) {
                        placeholder(lottieDrawable)
                    }
                    else load(storageRef.getReferenceFromUrl("gs://blogfy-e5b41.appspot.com/image/${item.created}")) {
                        placeholder(lottieDrawable)
                    }
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<BlogPresentationModel>() {

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
}
