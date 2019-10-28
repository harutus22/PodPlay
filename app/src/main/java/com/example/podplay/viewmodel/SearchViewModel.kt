package com.example.podplay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.podplay.repository.ItunesRepo
import com.example.podplay.service.PodcastResponse
import com.example.podplay.util.DateUtils

class SearchViewModel(app: Application): AndroidViewModel(app) {
    var itunesRepo: ItunesRepo? = null


    private fun itunesPodcastToPodcastSummaryView(itunesPodcast: PodcastResponse.ItunesPodcast):
            PodcastSummaryViewData{
        return PodcastSummaryViewData(
            itunesPodcast.collectionCensoredName,
            DateUtils.jsonDateToShortDate(itunesPodcast.releaseDate),
            itunesPodcast.artworkUrl30,
            itunesPodcast.feedUrl)
    }

    fun searchPodcast(term: String, callback: (List<PodcastSummaryViewData>) -> Unit){
        itunesRepo?.searchByTerm(term) {results ->
            if (results == null){
                callback(emptyList())
            } else {
                val searchView = results.map { podcast ->
                    itunesPodcastToPodcastSummaryView(podcast)
                }
                callback(searchView)
            }
        }
    }

    data class PodcastSummaryViewData(var name: String? = "",
                                      var lastUpdated: String? = "",
                                      var imageUrl: String? = "",
                                      var feedUrl: String? = "")
}

