package com.example.podplay.model

import java.util.*

data class Episode(var guide: String = "",
                   var title: String = "",
                   var description: String = "",
                   var mediaUrl: String = "",
                   var mimeType: String = "",
                   var releaseDate: Date = Date(),
                   var duration: String = "")

data class Podcast(var feedUrl: String = "",
                   var feedTitle: String = "",
                   var feedDesc: String = "",
                   var imageUrl: String = "",
                   var lastUpdated: Date = Date(),
                   var episodes: List<Episode>  = listOf())