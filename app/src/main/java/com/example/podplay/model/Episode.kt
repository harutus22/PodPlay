package com.example.podplay.model

import androidx.room.*
import java.util.*

@Entity(foreignKeys = [ForeignKey(entity = Podcast::class,
    parentColumns = ["id"],
    childColumns = ["podcastId"],
    onDelete = ForeignKey.CASCADE)], indices = [Index("podcastId")])
data class Episode(@PrimaryKey var guide: String = "",
                   var podcastId: Long? = null,
                   var title: String = "",
                   var description: String = "",
                   var mediaUrl: String = "",
                   var mimeType: String = "",
                   var releaseDate: Date = Date(),
                   var duration: String = "")

@Entity
data class Podcast(@PrimaryKey(autoGenerate = true)var id: Long? = null,
                   var feedUrl: String = "",
                   var feedTitle: String = "",
                   var feedDesc: String = "",
                   var imageUrl: String = "",
                   var lastUpdated: Date = Date(),
                   @Ignore
                   var episodes: List<Episode>  = listOf())