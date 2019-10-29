package com.example.podplay.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.podplay.R
import com.example.podplay.util.DateUtils
import com.example.podplay.util.HtmlUtils
import com.example.podplay.viewmodel.PodcastViewModel

class EpisodeListAdapter (private var episodeViewList: List<PodcastViewModel.EpisodeViewData>?):
    RecyclerView.Adapter<EpisodeListAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.episode_item,
            parent, false))
    }

    override fun getItemCount(): Int {
        return  episodeViewList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeViewList = episodeViewList ?: return
        val episodeView = episodeViewList[position]
        holder.episodeViewData = episodeView
        holder.titleTextView.text = episodeView.title
        holder.descTextView.text = HtmlUtils.htmlToSpannable(episodeView.description ?: "")
        holder.durationTextView.text = episodeView.duration
        holder.releaseDateTextView.text = episodeView.releaseDate?.let { DateUtils.dateToShortDate(it) }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var episodeViewData: PodcastViewModel.EpisodeViewData? = null
        val titleTextView: TextView = itemView.findViewById(R.id.titleView)
        val descTextView: TextView = itemView.findViewById(R.id.descView)
        val durationTextView: TextView = itemView.findViewById(R.id.durationView)
        val releaseDateTextView: TextView = itemView.findViewById(R.id.releaseDateView)
    }
}