package com.project.moyeomoyeo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.PostingData
import com.project.moyeomoyeo.DataClass.UserData

class PostingListRecyclerViewAdapter(private val items : ArrayList<PostingData>,
                                     val context : Context, val userData: UserData)
    : RecyclerView.Adapter<PostingListRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myGroup = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_posting_list_preview, parent, false)

        return ViewHolder(myGroup)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var ProfilePhoto = holder.PreviewList.findViewById<ImageView>(R.id.Posting_Photo_imgView)
        var NickName = holder.PreviewList.findViewById<TextView>(R.id.Posting_NickName)
        var TimeStamp = holder.PreviewList.findViewById<TextView>(R.id.Posting_Date)
        var content = holder.PreviewList.findViewById<TextView>(R.id.Posting_Content_TextView)
        var CommentCount = holder.PreviewList.findViewById<TextView>(R.id.PostingPreview_CommentCount_TextView)
//        var commentNum

        //프사 uri넣기
//        ProfilePhoto.setImageURI(items[position].profileImage)

        NickName.text = items[position].nickname
        TimeStamp.text = items[position].createdAt
        content.setText(items[position].content)
        CommentCount.text = items[position].commentsCount.toString()

        holder.PreviewList.setOnClickListener {
            //포스팅 상세보기
            val intent = Intent(context, PostingDetailActivity::class.java)
            intent.putExtra("postingData", items[position])
            intent.putExtra("userData", userData)
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val PreviewList: View) : RecyclerView.ViewHolder(PreviewList)

}