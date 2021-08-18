package com.project.moyeomoyeo

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.moyeomoyeo.DataClass.ClubData
import com.project.moyeomoyeo.DataClass.ClubPreviewData
import com.project.moyeomoyeo.DataClass.UserData

class RecommendClubListRecyclerViewAdapter(private val items : ArrayList<ClubPreviewData>,
                                           val context : Context, val userData: UserData)
    : RecyclerView.Adapter<RecommendClubListRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myGroup = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_recommend, parent, false)

        return ViewHolder(myGroup)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var GroupPhoto = holder.PreviewList.findViewById<ImageView>(R.id.RecommendClubLogo_ImageView)
        var GroupName = holder.PreviewList.findViewById<TextView>(R.id.RecommendClubName_TextView)
        var GroupExplain = holder.PreviewList.findViewById<TextView>(R.id.RecommendClubExplain_TextView)

        Glide.with(context).load(items[position].logoImage).into(GroupPhoto)
        GroupName.text = items[position].name
        GroupExplain.text = items[position].description

        holder.PreviewList.setOnClickListener {
            val intent = Intent(context, ClubDetailActivity::class.java)
            intent.putExtra("clubIdx", items[position].clubIdx)
            intent.putExtra("userData", userData)
            context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val PreviewList: View) : RecyclerView.ViewHolder(PreviewList)

}