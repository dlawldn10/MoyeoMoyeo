package com.project.moyeomoyeo.NotUsed

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.ClubData
import com.project.moyeomoyeo.R

//삭제
class MyClubRecyclerViewAdapter(private val items : ArrayList<ClubData>, val context : Context)
    : RecyclerView.Adapter<MyClubRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myGroup = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_clubs, parent, false) as CardView

        return ViewHolder(myGroup)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var GroupName = holder.PreviewList.findViewById<TextView>(R.id.MyGroup_Name)
        var GroupExplain = holder.PreviewList.findViewById<TextView>(R.id.MyGroup_Explain)
        var GroupPepNum = holder.PreviewList.findViewById<TextView>(R.id.MyGroup_PeopleNum)

        GroupName.text = items[position].name
        GroupExplain.text = items[position].description
//        GroupPepNum.text = items[position].PeopleNum.toString()

        holder.PreviewList.setOnClickListener {
            val intent = Intent(context, MyClubDetailActivity::class.java)
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val PreviewList: CardView) : RecyclerView.ViewHolder(PreviewList)

    }