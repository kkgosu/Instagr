package com.example.instagr.screens.search

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.instagr.R
import com.example.instagr.screens.common.BaseActivity
import com.example.instagr.screens.common.ImagesAdapter
import com.example.instagr.screens.common.setupAuthGuard
import com.example.instagr.screens.common.setupBottomNavigation
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity(), TextWatcher {

    private lateinit var mViewModel: SearchViewModel
    private lateinit var mAdapter: ImagesAdapter
    private var isSearchEntered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupBottomNavigation(1)
        Log.d(TAG, "onCreate: ")

        setupAuthGuard {
            mAdapter = ImagesAdapter()
            search_result_recycler.layoutManager = GridLayoutManager(this, 3)
            search_result_recycler.adapter = mAdapter

            mViewModel = initViewModel()
            mViewModel.posts.observe(this, Observer {
                it?.let { posts ->
                    mAdapter.updateImages(posts.map { it.image })
                }
            })

            search_input.addTextChangedListener(this)
            mViewModel.setSearchText("")
        }
    }

    override fun afterTextChanged(s: Editable?) {
        if (!isSearchEntered) {
            isSearchEntered = true
            Handler().postDelayed({
                                      isSearchEntered = false
                                      mViewModel.setSearchText(search_input.text.toString())
                                  }, 500)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    companion object {
        const val TAG = "SearchActivity"
    }
}
