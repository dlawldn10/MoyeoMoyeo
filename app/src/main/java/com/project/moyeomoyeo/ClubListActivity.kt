package com.project.moyeomoyeo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.ClubPreviewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject


class ClubListActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    //최신 동아리 모집 공고(임시)
    val ArrangedClubList = ArrayList<ClubPreviewData>()

    //사용자 토큰
    var jwt = "null"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_list)


        //이전 인텐트로 전달받은 jwt가 있다면 저장하고, 추후 다음 액티비티로 넘어갈때 전달한다.
        if(intent.getStringExtra("jwt") != null){
            jwt = intent.getStringExtra("jwt")!!
            findViewById<TextView>(R.id.ClubListTitle_TextView).text = "최신 동아리 모집 공고"
            getList(jwt, "1")

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



        findViewById<Button>(R.id.arrangeType_Bttn).setOnClickListener {
            ArrangeFilter()
        }

    }

    //액션바 옵션 반영하기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //mypage만 있는 툴바
        menuInflater.inflate(R.menu.mypage_toolbar, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            //마이페이지 아이콘 클릭
            R.id.MyPage->{
                Toast.makeText(applicationContext, "마이페이지", Toast.LENGTH_SHORT).show()
                intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
            }
//            //돋보기 아이콘 클릭
//            R.id.gotoSearch-> {
//                intent = Intent(this, SearchActivity::class.java)
//                startActivity(intent)
//                }
            //뒤로가기 클릭
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }

    //코루틴에서 호출
    //okhttp 비동기 방식 - GET
    //서버에서 동아리 목록 가져오기
    private fun getList(jwt: String, idx: String){

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                //동아리 목록 정보를 받아온다.
                try {
                    //리스트 초기화
                    ArrangedClubList.clear()
                    
                    val client = OkHttpClient.Builder().build()

                    val req = Request.Builder()
                        .url("https://moyeo.shop/clubs?choice=$idx")
                        .addHeader("x-access-token", jwt)
                        .build()

                    val response: Response = client.newCall(req).execute()

                    val result: String = response.body?.string() ?: "null"
                    var jsonObject = JSONObject(result)

                    if(jsonObject.getBoolean("isSuccess")){
                        var jsonArray = jsonObject.getJSONArray("result")
                        for (i in 0 until jsonArray.length()) {

                            val entry: JSONObject = jsonArray.getJSONObject(i)
                            var tmp : ClubPreviewData = ClubPreviewData(
                                entry.get("clubIdx") as Int,
                                entry.get("name") as String,
                                entry.get("description") as String,
                                entry.get("logoImage") as String,
                                entry.get("isLike") as Int
                            )
                            ArrangedClubList.add(tmp)
                        }
                        Log.d("리스트: ", jsonArray.toString())

                    }else{
                        //작업 실패 했을때
                        Log.d("리스트: ", jsonObject.get("code").toString())
                        Log.d("리스트: ", jsonObject.get("message").toString())
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.await()

            //리사이클러뷰 선언
            viewAdapter = ClubListRecyclerViewAdapter(ArrangedClubList, applicationContext, jwt)
            viewManager = LinearLayoutManager(applicationContext)
            recyclerView = findViewById<RecyclerView>(R.id.ClubList_RecyclerView).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }

    }

    fun ArrangeFilter(){
        val myAlertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        myAlertBuilder.setItems(R.array.sortIdx, DialogInterface.OnClickListener { dialog, pos ->
            when (pos) {
                0 -> {
                    //최신순
                    findViewById<TextView>(R.id.ClubListTitle_TextView).text = "최신 동아리 모집 공고"
                    getList(jwt, "1")
                    Log.d("리스트: ", "최신순으로 정렬")
                }
                1 -> {
                    //인기순
                    findViewById<TextView>(R.id.ClubListTitle_TextView).text = "인기 동아리 모집 공고"
                    getList(jwt, "2")
                    Log.d("리스트: ", "인기순으로 정렬")
                }
                2 -> {
                    //추천순
//                    findViewById<TextView>(R.id.ClubListTitle_TextView).text = "추천 동아리 모집 공고"
//                    getList(jwt, "3")
                    Log.d("리스트: ", "추천순으로 정렬")
                }
            }
        })
        myAlertBuilder.show()
    }
}