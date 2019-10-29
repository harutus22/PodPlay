package com.example.podplay.db

import android.content.Context
import androidx.room.*
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast
import java.util.*

@Database(entities = [Episode::class, Podcast::class], version = 1)
@TypeConverters(Converters::class)
abstract class PodPlayDatabase() : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao

    companion object {
        private var instance: PodPlayDatabase? = null

        fun getInstance(context: Context): PodPlayDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    PodPlayDatabase::class.java, "PodPlayer"
                ).build()
            }
            return instance as PodPlayDatabase
        }
    }
}

class Converters{
    @TypeConverter
    fun fromTimeStamp(value: Long?): Date?{
        return if(value == null) null else Date(value)
    }

    @TypeConverter
    fun toTimeStamp(date: Date?): Long?{
        return date?.time
    }
}