package com.example.podplay.repository

import androidx.lifecycle.LiveData
import com.example.podplay.db.PodcastDao
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast
import com.example.podplay.service.EpisodeResponse
import com.example.podplay.service.FeedService
import com.example.podplay.service.RssFeedResponse
import com.example.podplay.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PodcastRepo(private var feedService: FeedService,
                  private var podcastDao: PodcastDao){

    fun getPodcast(feedUrl: String, callback: (Podcast?) -> Unit){

        val podcast = podcastDao.loadPodcast(feedUrl)

        if (podcast != null){
            podcast.id?.let {
                podcast.episodes = podcastDao.loadEpisodes(it)
                GlobalScope.launch (Dispatchers.Main){
                    callback(podcast)
                }
            }
        } else {
            feedService.getFeed(feedUrl) { feedResponse ->
                var podcast: Podcast? = null
                if (feedResponse != null) {
                    podcast = rssResponseToPodcast(feedUrl, "", feedResponse)
                }
                GlobalScope.launch(Dispatchers.Main) {
                    callback(podcast)
                }
            }
        }
    }

    private fun rssItemsToEpisode(episodeResponses: List<EpisodeResponse>): List<Episode>{
        return episodeResponses.map {
            Episode(
                it.guid ?: "",
                null,
                it.title ?: "",
                it.description ?: "",
                it.url ?: "",
                it.type ?: "",
                DateUtils.xmlDateToDate(it.pubDate),
                it.duration ?: "")
        }
    }

    private fun rssResponseToPodcast(feedUrl: String, imageUrl: String,
                                     rssResponse: RssFeedResponse): Podcast?{
        val items = rssResponse.episodes ?: return null
        val description = if (rssResponse.description == "")
            rssResponse.summary else rssResponse.description
        return Podcast(null, feedUrl, rssResponse.title, description, imageUrl,
            rssResponse.lastUpdated, episodes = rssItemsToEpisode(items))
    }

    fun save(podcast: Podcast){
        GlobalScope.launch {
            val podcastId = podcastDao.insertPodcast(podcast)
            for (episode in podcast.episodes){
                episode.podcastId = podcastId
                podcastDao.insertEpisode(episode)
            }
        }
    }

    fun getAll(): LiveData<List<Podcast>>{
        return podcastDao.loadPodcasts()
    }
}