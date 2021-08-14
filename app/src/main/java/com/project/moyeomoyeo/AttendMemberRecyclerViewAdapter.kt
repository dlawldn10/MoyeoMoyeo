package com.project.moyeomoyeo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.UserAttendData

class AttendMemberRecyclerViewAdapter(private val items : ArrayList<UserAttendData>,
                                  val context : Context)
    : RecyclerView.Adapter<AttendMemberRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myGroup = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member_list, parent, false)

        return ViewHolder(myGroup)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var nickname = holder.MemberList.findViewById<TextView>(R.id.nickname_text)

        nickname.text = items[position].nickname
        if(items[position].isAttended == 1){
            holder.MemberList.setBackgroundColor(Color.parseColor("#32BF44"))
        }
        else if(items[position].isAttended == 0){
            holder.MemberList.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        else{
            Log.d("출석체크","잘못된 isAttended 형식")
        }

        holder.MemberList.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val MemberList: View) : RecyclerView.ViewHolder(MemberList)

}