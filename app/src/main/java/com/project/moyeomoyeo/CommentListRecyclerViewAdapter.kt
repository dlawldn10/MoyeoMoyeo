package com.project.moyeomoyeo

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.moyeomoyeo.DataClass.CommentData
import com.project.moyeomoyeo.DataClass.UserData

class CommentListRecyclerViewAdapter(private val items : MutableList<CommentData>,
                                     val context : Context, val userData: UserData, val activity: PostingDetailActivity)
    : RecyclerView.Adapter<CommentListRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myGroup = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_posting_comment, parent, false)


        return ViewHolder(myGroup)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var Photo = holder.CommentList.findViewById<ImageView>(R.id.Comment_Photo_imgView)
        var NickName = holder.CommentList.findViewById<TextView>(R.id.Comment_NickName)
        var TimeStamp = holder.CommentList.findViewById<TextView>(R.id.Comment_Date)
        var Content = holder.CommentList.findViewById<TextView>(R.id.Comment_Content_TextView)
        var RecommentBtn = holder.CommentList.findViewById<ImageButton>(R.id.Recomment_Bttn)


        Glide.with(context).load(items[position].profileImage).into(Photo)
        NickName.text = items[position].nickname
        TimeStamp.text = items[position].createdAt
        Content.text = items[position].content

        //답글 이라면 
        if(items[position].parentCommentIdx != 0){
            //ui다르게하기
            RecommentBtn.visibility = View.GONE
            holder.CommentList.findViewById<LinearLayout>(R.id.RecommentMargin_layout).visibility = View.VISIBLE
            var rootView = holder.CommentList.findViewById<LinearLayout>(R.id.Comment_Rootlayout)
            rootView.setBackgroundColor(Color.parseColor("#EFF2F9"))
        }


        RecommentBtn.setOnClickListener {
            RecommentDialogBuilder(items[position], holder.CommentList.context)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val CommentList: View) : RecyclerView.ViewHolder(CommentList)


    fun RecommentDialogBuilder(commentData: CommentData, context: Context){
        var dilaog = Dialog(context)       // Dialog 초기화
//        dilaog.setTitle("지원 동기")
        dilaog.setContentView(R.layout.dialog_recomment);             // xml 레이아웃 파일과 연결
        dilaog.show()


        var okBttn = dilaog.findViewById<Button>(R.id.recomment_Ok_Bttn)
        var cancelBttn = dilaog.findViewById<Button>(R.id.recomment_Canel_Bttn)

        okBttn.setOnClickListener {

            activity.setRecommentData(commentData.commentIdx)
            dilaog.dismiss()

        }

        cancelBttn.setOnClickListener {

            dilaog.dismiss()


        }
    }

}