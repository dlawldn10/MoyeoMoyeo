package com.project.moyeomoyeo

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.ApplyData
import com.project.moyeomoyeo.DataClass.ClubData
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

//지원자 리스트
class FragmentMemberApply : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    //모임 멤버 리스트
    val RequestedApplyList = ArrayList<ApplyData>()

    var userData = UserData("", 0, "")
    var Data = ClubData(0,0,"","","",
        "","",0, 0, 0, 0, 0, 0, 0)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment__member_apply, container, false)

        val bundle = this.arguments
        if (bundle != null) {
            Log.d("멤버조회", "프래그먼트 값 받음")
            Data = bundle.getSerializable("clubData") as ClubData
            userData = bundle.getSerializable("userData") as UserData

            getApplyList(rootView, userData, Data.clubIdx)
        }


        return rootView
    }

    fun getApplyList(rootView: View, userData: UserData, clubIdx: Int){

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                //동아리 목록 정보를 받아온다.
                //리스트 초기화
                RequestedApplyList.clear()

                val client = OkHttpClient.Builder().build()
                var url = "https://moyeo.shop/clubs/$clubIdx/applicants"

                Log.d("테스트", userData.jwt)
                val req = Request.Builder()
                    .url(url)
                    .addHeader("x-access-token", userData.jwt)
                    .build()

                val response: Response = client.newCall(req).execute()

                var jsonObject = JSONObject(response.body?.string())

                if(jsonObject.getBoolean("isSuccess")){
                    var jsonArray = jsonObject.getJSONArray("result")
                    for (i in 0 until jsonArray.length()) {

                        val entry: JSONObject = jsonArray.getJSONObject(i)
                        var tmp = ApplyData(
                            entry.get("userIdx") as Int,
                            entry.get("nickname") as String,
                            entry.get("profileImage") as String,
                            entry.get("motive") as String
                        )
                        RequestedApplyList.add(tmp)
                    }
                    Log.d("지원자 조회: ", jsonArray.toString())

                }else{
                    //작업 실패 했을때
                    Log.d("지원자 조회: ", jsonObject.get("code").toString())
                    Log.d("지원자 조회: ", jsonObject.get("message").toString())

                }
            }.await()

            //리사이클러뷰 선언
            viewAdapter = ApplyListRecyclerViewAdapter(RequestedApplyList, userData, rootView, Data.clubIdx, rootView.context)
            viewManager = LinearLayoutManager(rootView.context)
            recyclerView = rootView.findViewById<RecyclerView>(R.id.applyMember_RecyclerView)
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }


    }


    fun BuildDeleteDialog(dismissUserNick: String, applyData: ApplyData, rootView: View, userData: UserData, clubIdx: Int){

        var dilaog01 = Dialog(rootView.context)       // Dialog 초기화
        dilaog01.setTitle("지원 취소")
        dilaog01.setContentView(R.layout.dialog_dismiss);             // xml 레이아웃 파일과 연결
        dilaog01.show()


        var okBttn = dilaog01.findViewById<Button>(R.id.dismiss_Ok_Bttn)
        var cancelBttn = dilaog01.findViewById<Button>(R.id.dismiss_Canel_Bttn)
        var text = dilaog01.findViewById<TextView>(R.id.dismiss_TextView)

        text.text = "$dismissUserNick 회원님의 지원을 취소하시겠습니까?"


        okBttn.setOnClickListener {
            //지원자 삭제
            //0-> 불합격
            MemberResult(applyData, 0, userData, clubIdx, rootView)
            dilaog01.dismiss()

        }

        cancelBttn.setOnClickListener {
            dilaog01.dismiss()
        }

    }

    fun BuildAcceptDialog(acceptUserNick: String, applyData: ApplyData, rootView: View, userData: UserData, clubIdx: Int){

        var dilaog01 = Dialog(rootView.context)       // Dialog 초기화
        dilaog01.setTitle("지원 수락")
        dilaog01.setContentView(R.layout.dialog_dismiss);             // xml 레이아웃 파일과 연결
        dilaog01.show()


        var okBttn = dilaog01.findViewById<Button>(R.id.dismiss_Ok_Bttn)
        var cancelBttn = dilaog01.findViewById<Button>(R.id.dismiss_Canel_Bttn)
        var text = dilaog01.findViewById<TextView>(R.id.dismiss_TextView)

        text.text = "$acceptUserNick 회원님의 지원을 수락하시겠습니까?"


        okBttn.setOnClickListener {
            //지원자 지원 수락
            //1-> 합격
            MemberResult(applyData, 1, userData, clubIdx, rootView)
            dilaog01.dismiss()

        }

        cancelBttn.setOnClickListener {

            dilaog01.dismiss()


        }

    }


    fun MemberResult(applicant :ApplyData, result: Int, userData: UserData, clubIdx: Int, rootView: View){

        //1.클라이언트 만들기
        val client = OkHttpClient.Builder().build()


        //2.요청
        val formBody: FormBody = FormBody.Builder()
            .add("targetIdx", applicant.userIdx.toString())
            .add("decision", result.toString())
            .build()

        val req = Request.Builder()
            .url("https://moyeo.shop/clubs/$clubIdx/applicants/manage")
            .addHeader("x-access-token", userData.jwt)
            .patch(formBody)
            .build()


        //3.응답
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "서버에 접근할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "다시 시도하세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                try {
                    //주의* response.body!!.string() 값은 한번밖에 호출 못함.
                    var jsonString = response.body!!.string()
                    var jsonObject = JSONObject(jsonString)

                    if (jsonObject.getBoolean("isSuccess")) {
                        var result = jsonObject.get("result")
                        Log.d("지원 결과", result.toString())

                        getApplyList(rootView, userData, clubIdx)


                    } else {

                        //작업 실패 했을때
                        Log.d("지원자 결과", jsonObject.get("code").toString())
                        Log.d("지원자 결과", jsonObject.get("message").toString())
                    }

                } catch (e: JSONException) {
                    Log.d("지원자 결과", "JSON 오류")
                }


            }

        })



    }





}