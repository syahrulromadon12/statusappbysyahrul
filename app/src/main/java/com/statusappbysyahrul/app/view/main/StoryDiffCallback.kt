package com.statusappbysyahrul.app.view.main

import androidx.recyclerview.widget.DiffUtil
import com.statusappbysyahrul.app.data.response.ListStoryItem

class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
    override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem == newItem
    }
}
