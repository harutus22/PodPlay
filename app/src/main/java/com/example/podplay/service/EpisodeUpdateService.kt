package com.example.podplay.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.podplay.R
import com.example.podplay.db.PodPlayDatabase
import com.example.podplay.repository.PodcastRepo
import com.example.podplay.ui.PodcastActivity
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class EpisodeUpdateService: JobService() {

    companion object{
        const val EPISODE_CHANNEL_ID = "podplay_episode_channel"
        const val EXTRA_FEED_URL = "PodcastFeedUrl"
    }

    override fun onStopJob(job: JobParameters): Boolean {
        return true
    }

    override fun onStartJob(job: JobParameters): Boolean {
        val db = PodPlayDatabase.getInstance(this)
        val repo = PodcastRepo(FeedService.instance, db.podcastDao())
        GlobalScope.launch {
            repo.updatePodcastEpisodes { podcastUpdates ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    createNotificationChannel()
                }
                for (podcastUpdate in podcastUpdates){
                    displayNotification(podcastUpdate)
                }
                jobFinished(job, false)
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.getNotificationChannel(EPISODE_CHANNEL_ID) == null){
            val channel = NotificationChannel(EPISODE_CHANNEL_ID, "Episodes", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun displayNotification(podcastInfo: PodcastRepo.PodcastUpdateInfo){
        val contentIntent = Intent(this, PodcastActivity::class.java)
        contentIntent.putExtra(EXTRA_FEED_URL, podcastInfo.feedUrl)
        val pendingContentIntent = PendingIntent.getActivity(this, 0,
            contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, EPISODE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_episode_icon)
            .setContentTitle(getString(R.string.episode_notification_title))
            .setContentText(getString(R.string.episode_notification_text, podcastInfo.newCount,podcastInfo.name))
            .setNumber(podcastInfo.newCount)
            .setAutoCancel(true)
            .setContentIntent(pendingContentIntent)
            .build()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(podcastInfo.name, 0, notification)
    }

}