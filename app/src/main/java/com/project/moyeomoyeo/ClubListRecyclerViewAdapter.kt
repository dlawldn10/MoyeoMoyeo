package com.project.moyeomoyeo

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.GroupPreviews

class ClubListRecyclerViewAdapter(private val items : ArrayList<GroupPreviews>, val context : Context)
    : RecyclerView.Adapter<ClubListRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myGroup = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_club_list_preview, parent, false)

        return ViewHolder(myGroup)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var GroupPhoto = holder.PreviewList.findViewById<ImageView>(R.id.Preview_Photo_imgView)
        var GroupName = holder.PreviewList.findViewById<TextView>(R.id.Preview_Name)
        var GroupExplain = holder.PreviewList.findViewById<TextView>(R.id.Preview_Explain)

        //프사 uri or url 넣기
        //GroupPhoto <- items[position].ProfilePhoto
        GroupName.text = items[position].GroupName
        GroupExplain.text = items[position].Explain

        holder.PreviewList.setOnClickListener {
            Toast.makeText(it.context, GroupName.text, Toast.LENGTH_SHORT).show()
            val intent = Intent(context, ClubDetailActivity::class.java)
            context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val PreviewList: View) : RecyclerView.ViewHolder(PreviewList)

}