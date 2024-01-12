package com.example.storyappsubmission.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyappsubmission.data.DataLogin
import com.example.storyappsubmission.data.DataRegister
import com.example.storyappsubmission.data.ListStoryDetail
import com.example.storyappsubmission.data.LoginResponse
import com.example.storyappsubmission.repo.MainRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val repo: MainRepository): ViewModel() {
    val stories: LiveData<List<ListStoryDetail>> = repo.stories
    val message: LiveData<String> = repo.message
    val isLoading: LiveData<Boolean> = repo.loading
    val login: LiveData<LoginResponse> = repo.userLogin

    fun login(dataLogin: DataLogin) {
        repo.getLoginResponse(dataLogin)
    }

    fun register(dataRegis: DataRegister) {
        repo.getRegisterResponse(dataRegis)
    }

    fun upload(photo: MultipartBody.Part, des: RequestBody, lat: Double?, lng: Double?, token: String) {
        repo.upload(photo, des, lat, lng, token)
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<ListStoryDetail>> {
        return repo.getPagingStories(token).cachedIn(viewModelScope)
    }

    fun getStories(token: String) {
        repo.getStory(token)
    }
}