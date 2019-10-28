package com.example.podplay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast
import com.example.podplay.repository.PodcastRepo
import java.util.*

class PodcastViewModel(app: Application): AndroidViewModel(app) {

    var podcastRepo: PodcastRepo? = null
    var activePodcastViewData: PodcastViewData? = null

    data class PodcastViewData(var subscribed: Boolean = false,
                               var feedTitle: String? = "",
                               var feedUrl: String? = "",
                               var feedDesc: String? = "",
                               var imageUrl: String? = "",
                               var episodes: List<EpisodeViewData>)

    data class EpisodeViewData(var guid: String? = "",
                               var title: String? = "",
                               var description: String? = "",
                               var mediaUrl: String? = "",
                               var releaseDate: Date? = null,
                               var duration: String? = "")

    private fun episodeToEpisodesView(episode: List<Episode>): List<EpisodeViewData>{
        return episode.map {
            EpisodeViewData(it.guide, it.title, it.description,
                it.mediaUrl, it.releaseDate, it.duration)
        }
    }

    private fun podcastToPodcastView(podcast: Podcast): PodcastViewData{
        return PodcastViewData(false, podcast.feedTitle,
            podcast.feedUrl, podcast.feedDesc, podcast.imageUrl, episodeToEpisodesView(podcast.episodes))
    }

    fun getPodcast(podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData,
                   callback: (PodcastViewData?) -> Unit){
        val repo = podcastRepo ?: return
        val feedUrl = podcastSummaryViewData.feedUrl ?: return

        repo.getPodcast(feedUrl){
            it?.let {
                it.feedTitle = podcastSummaryViewData.name ?: ""
                it.imageUrl = podcastSummaryViewData.imageUrl ?: ""
                activePodcastViewData = podcastToPodcastView(it)
                callback(activePodcastViewData)
            }
        }
    }
}