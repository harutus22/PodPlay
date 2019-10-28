package com.example.podplay.ui

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.podplay.R
import com.example.podplay.adapter.PodcastListAdapter
import com.example.podplay.repository.ItunesRepo
import com.example.podplay.repository.PodcastRepo
import com.example.podplay.service.ItunesService
import com.example.podplay.viewmodel.PodcastViewModel
import com.example.podplay.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_podcast.*

class PodcastActivity : AppCompatActivity(), PodcastListAdapter.PodcastListAdapterListener {
    override fun onShowDetails(podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData) {
    }

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var podcastListAdapter: PodcastListAdapter
    private lateinit var searchMenuItem: MenuItem
    private lateinit var podcastViewModel: PodcastViewModel

    companion object{
        val TAG = javaClass.simpleName
        private val TAG_DETAILS_FRAGMENT = "DetailsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast)
        setToolbar()
        setUpViewModels()
        updateControls()
        handleIntent(intent)
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)

        searchMenuItem = menu!!.findItem(R.id.search_item)
        val searchView = searchMenuItem.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        if (podcastRecyclerView.visibility == View.INVISIBLE){
            searchMenuItem.isVisible = false
        }

        return true
    }

    private fun performSearch(term: String){
        showProgressBar()
        searchViewModel.searchPodcast(term) {results ->
            hideProgressBar()
            toolbar.title = getString(R.string.search_results)
            podcastListAdapter.setSearchData(results)
        }
        val itunesService = ItunesService.instance
        val itunesRepo = ItunesRepo(itunesService)
        itunesRepo.searchByTerm(term) {Log.i(TAG, "Results = $it")}
    }

    private fun setToolbar(){
        setSupportActionBar(toolbar)
    }

    private fun setUpViewModels(){
        val service = ItunesService.instance
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchViewModel.itunesRepo = ItunesRepo(service)

        podcastViewModel = ViewModelProviders.of(this).get(PodcastViewModel::class.java)
        podcastViewModel.podcastRepo = PodcastRepo()
    }

    private fun updateControls(){
        podcastRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        podcastRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(podcastRecyclerView.context, layoutManager.orientation)
        podcastRecyclerView.addItemDecoration(dividerItemDecoration)

        podcastListAdapter = PodcastListAdapter(null, this, this)
        podcastRecyclerView.adapter = podcastListAdapter
    }

    private fun handleIntent(intent: Intent){
        if(Intent.ACTION_SEARCH == intent.action){
            val query = intent.getStringExtra(SearchManager.QUERY)
            performSearch(query)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent!!)
    }

    private fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        progressBar.visibility = View.INVISIBLE
    }

    private fun createPodcastDetailFragment(): PodcastDetailsFragment{
        var podcastDetailsFragment = supportFragmentManager.findFragmentByTag(TAG_DETAILS_FRAGMENT) as PodcastDetailsFragment?

        if (podcastDetailsFragment == null){
            podcastDetailsFragment = PodcastDetailsFragment.newInstance()
        }

        return podcastDetailsFragment
    }

    private fun showDetailsFragment(){
        val podcastDetailFragment = createPodcastDetailFragment()
        supportFragmentManager.beginTransaction().add(R.id.podcastDetailsContainer, podcastDetailFragment,
            TAG_DETAILS_FRAGMENT).addToBackStack("DetailsFragment").commit()
        podcastRecyclerView.visibility = View.VISIBLE
        searchMenuItem.isVisible = false
    }

    private fun showError(message: String){
        AlertDialog.Builder(this).setMessage(message).
            setPositiveButton(R.string.ok_button, null).create().show()
    }

    fun onSHowDetails(podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData){
        val feedUrl = podcastSummaryViewData.feedUrl ?: return
        showProgressBar()
        podcastViewModel.getPodcast(podcastSummaryViewData){
            hideProgressBar()
            if (it != null){
                showDetailsFragment()
            } else {
                showError("Error loading feed $feedUrl")
            }
        }
    }
}
