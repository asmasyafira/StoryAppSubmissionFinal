package com.example.storyappsubmission.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyappsubmission.adapter.ListStoryAdapter
import com.example.storyappsubmission.data.ListStoryDetail
import com.example.storyappsubmission.repo.MainRepository
import com.example.storyappsubmission.viewmodel.DataDummy.generateDummyNewStories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    private lateinit var mainViewModel: MainViewModel
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()
    @Mock
    private val repository = Mockito.mock(MainRepository::class.java)
    @Before
    fun getViewModel() {
        mainViewModel = MainViewModel(repository)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `verify getStories is working and Should Not Return Null`() = runTest {
        val dummyStories = generateDummyNewStories()
        val data: PagingData<ListStoryDetail> = PagedTestDataSources.snapshot(dummyStories)
        val expectedStory = MutableLiveData<PagingData<ListStoryDetail>>()
        val token = "ini token"

        expectedStory.value = data
        `when`(repository.getPagingStories(token)).thenReturn(expectedStory)

        val actualStory: PagingData<ListStoryDetail> = mainViewModel.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.StoryDetailDiffCallback(),
            updateCallback = updateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories, differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `when GetStories is Empty Should Not return Null`() = runTest {
        val data: PagingData<ListStoryDetail> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryDetail>>()
        expectedStory.value = data
        val token = "ini token"

        `when`(repository.getPagingStories(token)).thenReturn(expectedStory)
        val actualStory: PagingData<ListStoryDetail> = mainViewModel.getPagingStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.StoryDetailDiffCallback(),
            updateCallback = updateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        assertEquals(0, differ.snapshot().size)
    }

    private val updateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    class PagedTestDataSources private constructor() :
        PagingSource<Int, LiveData<List<ListStoryDetail>>>() {
        companion object {
            fun snapshot(items: List<ListStoryDetail>): PagingData<ListStoryDetail> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryDetail>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryDetail>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }
}