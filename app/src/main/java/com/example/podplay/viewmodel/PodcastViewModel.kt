package com.example.podplay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast
import com.example.podplay.repository.PodcastRepo
import com.example.podplay.util.DateUtils
import java.util.*

class PodcastViewModel(app: Application): AndroidViewModel(app) {

    private var activePodcast: Podcast? = null
    var podcastRepo: PodcastRepo? = null
    var activePodcastViewData: PodcastViewData? = null
    var livePodcastData: LiveData<List<SearchViewModel.PodcastSummaryViewData>>? = null
    var activeEpisodeViewData: EpisodeViewData? = null
    var isVideo: Boolean = false

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
                               var duration: String? = "",
                               var isVideo: Boolean = false)

    private fun episodeToEpisodesView(episode: List<Episode>): List<EpisodeViewData>{
        return episode.map {
            val isVideo = it.mimeType.startsWith("video")
            EpisodeViewData(it.guide, it.title, it.description,
                it.mediaUrl, it.releaseDate, it.duration, isVideo)
        }
    }

    private fun podcastToPodcastView(podcast: Podcast): PodcastViewData{
        return PodcastViewData(podcast.id != null, podcast.feedTitle,
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
                activePodcast = it
                callback(activePodcastViewData)
            }
        }
    }

    fun saveActivePodcast(){
        val repo = podcastRepo ?: return
        activePodcast?.let {
            repo.save(it) }
    }

    private fun podcastToSummaryView(podcast: Podcast): SearchViewModel.PodcastSummaryViewData{
        return SearchViewModel.PodcastSummaryViewData(
            podcast.feedTitle,
            DateUtils.dateToShortDate(podcast.lastUpdated),
            podcast.imageUrl,
            podcast.feedUrl
        )
    }

    fun setActivePodcast(feedUrl: String, callback: (SearchViewModel.PodcastSummaryViewData?) -> Unit){
        val repo = podcastRepo ?: return
        repo.getPodcast(feedUrl){ podcast ->
            if (podcast == null){
                callback(null)
            } else {
                activePodcastViewData = podcastToPodcastView(podcast)
                activePodcast = podcast
                callback(podcastToSummaryView(podcast))
            }
        }
    }

    fun deleteActivePodcast(){
        val repo = podcastRepo ?: return
        activePodcast?.let {
            repo.delete(it)
        }
    }

    fun getPodcasts(): LiveData<List<SearchViewModel.PodcastSummaryViewData>>?{
        val repo = podcastRepo ?: return null
        if(livePodcastData == null){
            val liveData = repo.getAll()
            livePodcastData = Transformations.map(liveData){podcastList ->
                podcastList.map { podcast ->
                    podcastToSummaryView(podcast)
                }
            }
        }
        return livePodcastData
    }
}