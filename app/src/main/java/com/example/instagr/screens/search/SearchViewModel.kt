package com.example.instagr.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.instagr.data.SearchRepository
import com.example.instagr.models.SearchPost
import com.example.instagr.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class SearchViewModel(searchRepo: SearchRepository,
                      onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {

    private val searchText = MutableLiveData<String>()
    val posts: LiveData<List<SearchPost>> = Transformations.switchMap(searchText) { text ->
        searchRepo.searchPosts(text)
    }

    fun setSearchText(text: String) {
        searchText.value = text
    }
}