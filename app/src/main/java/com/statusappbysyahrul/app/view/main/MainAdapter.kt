package com.statusappbysyahrul.app.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.statusappbysyahrul.app.data.response.ListStoryItem
import com.statusappbysyahrul.app.databinding.ItemStoryBinding

class MainAdapter(
    private val listener: (ListStoryItem) -> Unit
) : PagingDataAdapter<ListStoryItem, MainAdapter.StoryViewHolder>(StoryDiffCallback()) {

    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem){
            binding.apply {
                imgStory.load(story.photoUrl)
                name.text = story.name
                description.text = story.description
            }
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }

        holder.itemView.setOnClickListener {
            item?.let { listener(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder =
        StoryViewHolder(ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}
