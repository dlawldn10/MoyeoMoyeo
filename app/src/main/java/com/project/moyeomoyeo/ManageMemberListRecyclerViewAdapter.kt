package com.project.moyeomoyeo

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.moyeomoyeo.DataClass.MemberData
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

class ManageMemberListRecyclerViewAdapter(private val items : ArrayList<MemberData>,
                                          val context : Context, val userData: UserData, val clubIdx: Int, val rootView: View)
    : RecyclerView.Adapter<ManageMemberListRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myGroup = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_member, parent, false)

        return ViewHolder(myGroup)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var Photo = holder.PreviewList.findViewById<ImageView>(R.id.Member_Photo_ImageView)
        var NickName = holder.PreviewList.findViewById<TextView>(R.id.Member_NickName_TextView)
        var DismissBtn = holder.PreviewList.findViewById<ImageButton>(R.id.Member_Dismiss_Bttn)

        //프사 uri or url 넣기
        //GroupPhoto <- items[position].ProfilePhoto
        Glide.with(context).load(items[position].profileImage).into(Photo)
        NickName.text = items[position].nickname

        DismissBtn.setOnClickListener {
            BuildDismissDialog(items[position])
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val PreviewList: View) : RecyclerView.ViewHolder(PreviewList)

    fun BuildDismissDialog(member: MemberData){

        var dilaog01 = Dialog(context)       // Dialog 초기화
        dilaog01.setTitle("지원 동기")
        dilaog01.setContentView(R.layout.dialog_dismiss)             // xml 레이아웃 파일과 연결
        dilaog01.show()


        var okBttn = dilaog01.findViewById<Button>(R.id.dismiss_Ok_Bttn)
        var cancelBttn = dilaog01.findViewById<Button>(R.id.dismiss_Canel_Bttn)
        var text = dilaog01.findViewById<TextView>(R.id.dismiss_TextView)

        text.text = "${member.nickname} 회원님을 모임에서 내보내시겠습니까?"


        okBttn.setOnClickListener {
            //모임원 내보내기
            deleteMember(member)
            dilaog01.dismiss()
        }

        cancelBttn.setOnClickListener {

            dilaog01.dismiss()


        }

    }


    private fun deleteMember(member: MemberData){
        Log.d("멤버 내보내기: ", "delete함수 실행")
        CoroutineScope(Dispatchers.IO).async {
            //동아리 목록 정보를 받아온다.
            //리스트 초기화
            Log.d("멤버 내보내기: ", "delete함수 내부 실행")
            val client = OkHttpClient.Builder().build()
            var url = "https://moyeo.shop/clubs/$clubIdx/members/out?memberIdx=${member.userIdx}"

            val formBody: FormBody = FormBody.Builder()
                .build()

            val req = Request.Builder().url(url)
                .addHeader("x-access-token", userData.jwt)
                .delete(formBody)
                .build()

            val response: Response = client.newCall(req).execute()

            var jsonObject = JSONObject(response.body?.string())

            if(jsonObject.getBoolean("isSuccess")){
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show()
                }
                Log.d("멤버 내보내기: ", jsonObject.get("message").toString())
                FragmentMemberAll().getList(rootView, userData, clubIdx)

            }else{
                //작업 실패 했을때
                Log.d("멤버조회: ", jsonObject.get("code").toString())
                Log.d("멤버조회: ", jsonObject.get("message").toString())
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }




    }

}