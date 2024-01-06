package com.zlagi.blogfy.view.blog.search.result

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.blogfy.R
import com.zlagi.blogfy.databinding.SearchItemBinding
import com.zlagi.presentation.model.BlogPresentationModel
import io.github.rosariopfernandes.firecoil.load

class SearchResultAdapter(
    private val firestoreImageBucketUrl: String
) : ListAdapter<BlogPresentationModel, SearchResultAdapter.SearchBlogViewHolder>(DiffCallback()) {

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
        holder.bind(currentList[position], firestoreImageBucketUrl)
    }

    inner class SearchBlogViewHolder(
        private val binding: SearchItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseValueOf")
        fun bind(
            item: BlogPresentationModel,
            firestoreImageBucketUrl: String
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
                    if (item.updated.isNotEmpty()) load(
                        storageRef.getReferenceFromUrl(
                            "$firestoreImageBucketUrl${item.updated}"
                        )
                    ) {
                        placeholder(lottieDrawable)
                    }
                    else load(
                        storageRef.getReferenceFromUrl(
                            "$firestoreImageBucketUrl${item.created}"
                        )
                    ) {
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
