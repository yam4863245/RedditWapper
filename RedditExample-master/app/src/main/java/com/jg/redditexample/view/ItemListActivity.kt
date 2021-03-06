package com.jg.redditexample.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jg.redditexample.R
import com.jg.redditexample.data.Children
import com.jg.redditexample.data.Data
import com.jg.redditexample.model.PostRepositoryRemote
import com.jg.redditexample.viewmodel.PostViewModel
import com.jg.redditexample.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ItemListActivity : AppCompatActivity() {

    private var twoPane: Boolean = false

    private lateinit var viewModel: PostViewModel
    private lateinit var adapter: SimpleItemRecyclerViewAdapter
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var page: String? = ""

    companion object {
        const val TAG = "View"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        if (item_detail_container != null) {
            twoPane = true
        }

        setupViewModel()
        setupRecyclerView(item_list)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        val children = viewModel.posts.value?.children

        adapter = SimpleItemRecyclerViewAdapter(this, children ?: mutableListOf(), twoPane)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this@ItemListActivity,
                LinearLayoutManager.VERTICAL
            )
        )
        //recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.addOnScrollListener(object :
            PaginationScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                if (!page.isNullOrEmpty()) {
                    viewModel.loadTopPosts(after = page!!, before = "")
                }
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupViewModel() {
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory(repository = PostRepositoryRemote()))
                .get(PostViewModel::class.java)
        viewModel.posts.observe(this, renderPosts)


        viewModel.isViewLoading.observe(this, isViewLoadingObserver)
        //viewModel.onMessageError.observe(this,onMessageErrorObserver)
        //viewModel.isEmptyList.observe(this,emptyListObserver)
    }

    private val isViewLoadingObserver = Observer<Boolean> {
        isLoading = it
        if (it) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val renderPosts = Observer<Data> {
        Log.v(TAG, "data updated $it")
        if (it == null) {
            page = ""
            adapter.clearAll()
            return@Observer
        }
        //layoutError.visibility=View.GONE
        //layoutEmpty.visibility=View.GONE
        page = it.after

        filterChildren(it.children)

        adapter.update(it.children)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun filterChildren(children: MutableList<Children>) {
        children.removeIf {
            !it.data.thumbnail.contains("https") ||
                    (!it.data.url.contains("jpg") &&
                    !it.data.url.contains("png") &&
                    !it.data.url.contains("gif"))
        }

        children.forEach {
            it.data.url = it.data.url.replace("gifv","gif")
            Log.e("children", it.data.thumbnail)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_h ->{
                PostRepositoryRemote.isH = !PostRepositoryRemote.isH
                startActivity(Intent(this,ItemListActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
