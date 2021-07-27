package com.project.moyeomoyeo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.GroupPreviews

class MyClubRecyclerViewAdapter(private val items : ArrayList<GroupPreviews>)
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

        GroupName.text = items[position].GroupName
        GroupExplain.text = items[position].Explain
        GroupPepNum.text = items[position].PeopleNum.toString()

        holder.PreviewList.setOnClickListener {
            Toast.makeText(it.context, GroupName.text, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val PreviewList: CardView) : RecyclerView.ViewHolder(PreviewList)

    }