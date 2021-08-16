package com.project.moyeomoyeo.NotUsed

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.MemberData
import com.project.moyeomoyeo.R

//ManageMemberListRecyclerView로 이름변경. 삭제.
class MemberListRecyclerViewAdapter(private val items : ArrayList<MemberData>,
                                    val context : Context)
     {

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val myGroup = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_all_member, parent, false)
//
//        return ViewHolder(myGroup)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        var Photo = holder.PreviewList.findViewById<ImageView>(R.id.Member_Photo_ImageView)
//        var NickName = holder.PreviewList.findViewById<TextView>(R.id.Member_NickName_TextView)
//        var DismissBtn = holder.PreviewList.findViewById<Button>(R.id.Member_Dismiss_Bttn)
//
//        //프사 uri or url 넣기
//        //GroupPhoto <- items[position].ProfilePhoto
//        NickName.text = items[position].nickName
//
//        DismissBtn.setOnClickListener {
//            BuildDismissDialog(items[position].userIdx, NickName.text as String)
//        }
//
//
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    class ViewHolder(val PreviewList: View) : RecyclerView.ViewHolder(PreviewList)
//
//    fun BuildDismissDialog(dismissUserIdx: Int, dismissUserNick: String){
//
//        var dilaog01 = Dialog(context)       // Dialog 초기화
//        dilaog01.setTitle("지원 동기")
//        dilaog01.setContentView(R.layout.dismiss_dialog);             // xml 레이아웃 파일과 연결
//        dilaog01.show()
//
//
//        var okBttn = dilaog01.findViewById<Button>(R.id.dismiss_Ok_Bttn)
//        var cancelBttn = dilaog01.findViewById<Button>(R.id.dismiss_Canel_Bttn)
//        var text = dilaog01.findViewById<TextView>(R.id.dismiss_TextView)
//
//        text.text = "$dismissUserNick 회원님을 모임에서 내보내시겠습니까?"
//
//
//        okBttn.setOnClickListener {
//            //모임원 내보내기
//
//        }
//
//        cancelBttn.setOnClickListener {
//
//            dilaog01.dismiss()
//
//
//        }
//
//    }

}