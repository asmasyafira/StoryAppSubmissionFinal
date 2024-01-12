package com.example.storyappsubmission.viewmodel

import com.example.storyappsubmission.data.DataLogin
import com.example.storyappsubmission.data.ListStoryDetail
import com.example.storyappsubmission.data.LoginResponse
import com.example.storyappsubmission.data.LoginResult

object DataDummy {
    fun generateDummyNewStories(): List<ListStoryDetail> {
        val storyList = ArrayList<ListStoryDetail>()
        for (i in 0..5) {
            val stories = ListStoryDetail(
                "Title $i",
                "Ama",
                "Yuhuuu",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            storyList.add(stories)
        }
        return storyList
    }
}