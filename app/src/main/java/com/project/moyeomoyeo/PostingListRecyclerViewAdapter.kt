package com.project.moyeomoyeo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.PostingPreviewData
import com.project.moyeomoyeo.DataClass.UserData

class PostingListRecyclerViewAdapter(private val items : ArrayList<PostingPreviewData>,
                                     val context : Context, val userData: UserData)
    : RecyclerView.Adapter<PostingListRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myGroup = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_posting_list_preview, parent, false)

        return ViewHolder(myGroup)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var ProfilePhoto = holder.PreviewList.findViewById<ImageView>(R.id.Preview_Photo_imgView)
        var NickName = holder.PreviewList.findViewById<TextView>(R.id.Posting_NickName)
        var TimeStamp = holder.PreviewList.findViewById<TextView>(R.id.Posting_Date)
        var content = holder.PreviewList.findViewById<EditText>(R.id.Posting_Content_editText)
//        var commentNum

        //프사 uri넣기
        //GroupPhoto <- items[position].ProfilePhoto    //프로필 사진 uri도 필요함.
        NickName.text = items[position].userIdx.toString()  //이거 이 유저인덱스에 해당하는 유저의 닉네임이 필요함.
        TimeStamp.text = items[position].createdAt.toString()
        content.setText(items[position].content)
        //댓글 몇개인지도 필요.

        holder.PreviewList.setOnClickListener {
            Toast.makeText(it.context, NickName.text, Toast.LENGTH_SHORT).show()
            //댓글 펼쳐지기
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val PreviewList: View) : RecyclerView.ViewHolder(PreviewList)

}