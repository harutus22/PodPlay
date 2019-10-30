package com.example.podplay.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast

@Dao
interface PodcastDao {
    @Query("SELECT * FROM podcast ORDER BY feedTitle")
    fun loadPodcasts(): LiveData<List<Podcast>>

    @Query("SELECT * FROM Episode WHERE podcastId = :podcastId ORDER BY releaseDate DESC")
    fun loadEpisodes(podcastId: Long): List<Episode>

    @Query("SELECT * FROM Podcast WHERE feedUrl = :url")
    fun loadPodcast(url: String): Podcast?

    @Query("SELECT * FROM Podcast ORDER BY feedTitle")
    fun loadPodcastsStatic(): List<Podcast>

    @Insert(onConflict = REPLACE)
    fun insertPodcast(podcast: Podcast): Long

    @Insert(onConflict = REPLACE)
    fun insertEpisode(episode: Episode): Long

    @Delete
    fun deletePodcast(podcast: Podcast)
}