package com.project.moyeomoyeo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.moyeomoyeo.DataClass.UserAttendData

class AttendMemberRecyclerViewAdapter(private val items : ArrayList<UserAttendData>,
                                  val context : Context)
    : RecyclerView.Adapter<AttendMemberRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val member = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member_list, parent, false)

        return ViewHolder(member)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var nickname = holder.MemberList.findViewById<TextView>(R.id.Preview_Name)
        val profileImage = holder.MemberList.findViewById<ImageView>(R.id.Preview_Photo_imgView)

        if(URLUtil.isValidUrl(items[position].profile)){
            Glide.with(context)
                .load(items[position].profile)
                .into(profileImage)
        }

        nickname.text = items[position].nickname
        if(items[position].isAttended == 1){
            holder.MemberList.setBackgroundColor(Color.parseColor("#B9D4FE"))
        }
        else if(items[position].isAttended == 0){
            holder.MemberList.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        else{
            Log.d("출석체크","잘못된 isAttended 형식")
        }

        holder.MemberList.setOnClickListener {
            val intent = Intent(context, AttendCheckForMng::class.java)
            intent.putExtra("clubIdx", items[position].clubIdx.toString())
            intent.putExtra("userIdx", items[position].userIdx.toString())
            intent.putExtra("jwt", items[position].jwt)
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val MemberList: View) : RecyclerView.ViewHolder(MemberList)

}