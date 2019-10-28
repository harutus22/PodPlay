package com.example.podplay.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.podplay.R
import com.example.podplay.viewmodel.SearchViewModel.*

class PodcastListAdapter(private var podcastSummaryViewList: List<PodcastSummaryViewData>?,
                         private val podcastListAdapterListener: PodcastListAdapterListener,
                         private var parentActivity: Activity):
    RecyclerView.Adapter<PodcastListAdapter.ViewHolder>(){

    interface PodcastListAdapterListener{
        fun onShowDetails(podcastSummaryViewData: PodcastSummaryViewData)
    }

    inner class ViewHolder(itemView: View,
                           private val podcastListAdapterListener: PodcastListAdapterListener):
        RecyclerView.ViewHolder(itemView){

        var podcastSummaryViewData: PodcastSummaryViewData? = null
        val nameTextView: TextView = itemView.findViewById(R.id.podcastNameTextView)
        val lastUpdatedTextView: TextView = itemView.findViewById(R.id.podcastLastUpdatedTextView)
        val podcastImageView: ImageView = itemView.findViewById(R.id.podcastImage)

        init {
            itemView.setOnClickListener {
                podcastSummaryViewData?.let {
                    podcastListAdapterListener.onShowDetails(it)
                }
            }
        }
    }

    fun setSearchData(podcastSummaryViewData: List<PodcastSummaryViewData>){
        podcastSummaryViewList = podcastSummaryViewData
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return ViewHolder(view, podcastListAdapterListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchViewList = podcastSummaryViewList ?: return
        val searchView = searchViewList[position]
        holder.podcastSummaryViewData = searchView
        holder.nameTextView.text = searchView.name
        holder.lastUpdatedTextView.text = searchView.lastUpdated
        Glide.with(parentActivity).load(searchView.imageUrl).into(holder.podcastImageView)
    }

    override fun getItemCount(): Int {
        return podcastSummaryViewList?.size ?: 0
    }
}