package com.statusappbysyahrul.app

import com.statusappbysyahrul.app.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "12-12-2023",
                "Syahrul Romadon",
                "this is description",
                -9.373,
                "id + $i",
                1.999,
            )
            items.add(story)
        }
        return items
    }
}