package com.project.moyeomoyeo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.PostingPreviewData
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.sql.Timestamp

//외부인 질의응답 -> QnA Activity
//모임 내 커뮤니티 -> Community Activity
class PostingListActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    //사용자 토큰
    var userData = UserData("", 0, "")

    //요청된 포스팅글 리스트
    val RequestedPostingList = ArrayList<PostingPreviewData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting_list)

        if(intent.getSerializableExtra("userData") != null){
            userData = intent.getSerializableExtra("userData") as UserData
            openClubCommunity(userData.jwt, intent.getIntExtra("clubIdx", 0))
        }else{
            Log.d("리스트 ", "멤버 조회 실패")
        }

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기

    }

    //액션바 옵션 반영하기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //마이페이지만 있는 툴바
        menuInflater.inflate(R.menu.mypage_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.MyPage->{
                Toast.makeText(applicationContext, "마이페이지", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MyPageActivity::class.java)
                intent.putExtra("userData", userData)
                startActivity(intent)
            }
            //뒤로가기 버튼
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }

    private fun openClubCommunity(jwt: String, clubIdx: Int) {

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                val client = OkHttpClient.Builder().build()

                val req = Request.Builder()
                    .url("https://moyeo.shop/clubs/$clubIdx/posts")
                    .addHeader("x-access-token", jwt)
                    .build()

                val response: Response = client.newCall(req).execute()
                var jsonObject = JSONObject(response.body?.string() ?: "null")

                if (jsonObject.getBoolean("isSuccess")) {
                    var jsonArray = jsonObject.getJSONArray("result")
                    for (i in 0 until jsonArray.length()) {

                        val entry: JSONObject = jsonArray.getJSONObject(i)
                        var tmp = PostingPreviewData(
                            entry.get("postIdx") as Int,
                            entry.get("userIdx") as Int,
                            entry.get("clubIdx") as Int,
                            entry.get("content") as String,
                            entry.get("createdAt") as Timestamp
                        )
                        RequestedPostingList.add(tmp)
                    }
                    Log.d("리스트: ", jsonArray.toString())



                } else {
                    //작업 실패 했을때
                    Log.d("리스트 ", jsonObject.get("code").toString())
                    Log.d("리스트 ", jsonObject.get("message").toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }.await()

            //리사이클러뷰 선언
            viewAdapter = PostingListRecyclerViewAdapter(RequestedPostingList, applicationContext, userData)
            viewManager = LinearLayoutManager(applicationContext)
            recyclerView = findViewById<RecyclerView>(R.id.PostingListRecyclerView).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

    }
}