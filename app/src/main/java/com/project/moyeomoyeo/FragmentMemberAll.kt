package com.project.moyeomoyeo

import android.R.attr.defaultValue
import android.R.attr.key
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.ClubPreviewData
import com.project.moyeomoyeo.DataClass.MemberData
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject


class FragmentMemberAll : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    //모임 멤버 리스트
    val RequestedMemberList = ArrayList<MemberData>()

    var userData = UserData("", 0, "")
    var clubIdx = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment__member_all, container, false)

        val bundle = this.arguments
        if (bundle != null) {
            Log.d("멤버조회", "프래그먼트 값 받음")
            clubIdx = bundle.getInt("clubIdx", 0)
            userData = bundle.getSerializable("userData") as UserData

            getList(rootView)
        }


        return rootView
    }

    private fun getList(rootView: View){

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                //동아리 목록 정보를 받아온다.
                //리스트 초기화
                RequestedMemberList.clear()

                val client = OkHttpClient.Builder().build()
                var url = "https://moyeo.shop/clubs/$clubIdx/members"

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
                        var tmp : MemberData = MemberData(
                            entry.get("userIdx") as Int,
                            entry.get("nickName") as String
                        )
                        RequestedMemberList.add(tmp)
                    }
                    Log.d("멤버조회: ", jsonArray.toString())

                }else{
                    //작업 실패 했을때
                    Log.d("멤버조회: ", jsonObject.get("code").toString())
                    Log.d("멤버조회: ", jsonObject.get("message").toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }.await()

            //리사이클러뷰 선언
            viewAdapter = MemberListRecyclerViewAdapter(RequestedMemberList, rootView.context)
            viewManager = LinearLayoutManager(rootView.context)
            recyclerView = rootView.findViewById<RecyclerView>(R.id.allMember_RecyclerView).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }


    }



}