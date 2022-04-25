package com.zlagi.blogfy.view.blog.feed

import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.google.firebase.storage.FirebaseStorage
import com.zlagi.blogfy.R
import com.zlagi.blogfy.databinding.FeedItemLayoutBinding
import com.zlagi.presentation.model.BlogPresentationModel
import io.github.rosariopfernandes.firecoil.load

class FeedViewHolder(
    private val binding: FeedItemLayoutBinding,
    listener: OnItemSelectedListener<BlogPresentationModel> = { _, _ -> }
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var currentFeedItem: BlogPresentationModel
    private var currentPosition: Int = -1

    init {
        listener.let { l ->
            itemView.setOnClickListener { l(currentPosition, currentFeedItem) }
        }
    }

    fun bind(position: Int, item: BlogPresentationModel, firebaseStorage: FirebaseStorage) {
        currentPosition = position
        currentFeedItem = item
        with(binding) {
            blogAuthorTextView.text = item.username
            blogTitleTextView.text = item.title
            blogUpdateDateTextView.text = item.created
            val lottieDrawable = LottieDrawable()
            LottieCompositionFactory.fromRawRes(itemView.context, R.raw.image_loader)
                .addListener { lottieComposition ->
                    lottieDrawable.composition = lottieComposition
                    lottieDrawable.repeatCount = LottieDrawable.INFINITE
                    lottieDrawable.playAnimation()
                }
            with(blogImageView) {
                transitionName = item.pk.toString()
                if (item.updated.isNotEmpty()) load(
                    firebaseStorage.getReferenceFromUrl(
                        "gs://blogfy-e5b41.appspot.com/image/${item.updated}"
                    )
                ) {
                    placeholder(lottieDrawable)
                }
                else load(
                    firebaseStorage.getReferenceFromUrl(
                        "gs://blogfy-e5b41.appspot.com/image/${item.created}"
                    )
                ) {
                    placeholder(lottieDrawable)
                }
            }
        }
    }
}
