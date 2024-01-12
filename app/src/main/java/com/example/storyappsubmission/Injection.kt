package com.example.storyappsubmission

import android.content.Context
import com.example.storyappsubmission.data.SsPreferences
import com.example.storyappsubmission.data.StoryDatabase
import com.example.storyappsubmission.repo.MainRepository
import com.example.storyappsubmission.retrofit.ApiConfig
import com.example.storyappsubmission.ui.dataStore

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return MainRepository(database, apiService)
    }
}