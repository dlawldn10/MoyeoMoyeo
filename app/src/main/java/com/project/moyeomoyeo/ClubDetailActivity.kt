package com.project.moyeomoyeo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.moyeomoyeo.DataClass.ClubData
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject


class ClubDetailActivity : AppCompatActivity() {


    var Data = ClubData(0,0,"","","",
        "","",0, 0, 0, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_detail)

        CoroutineScope(Dispatchers.Main).launch {

            withContext(
                CoroutineScope(Dispatchers.IO).coroutineContext
            ) {


                if(intent.getStringExtra("jwt") != null) {

                    getClubData(intent.getStringExtra("jwt")!!, intent.getIntExtra("clubIdx", 0))

                }else{
                    Log.d("리스트 ", "멤버 조회 실패")
                }


            }

            findViewById<TextView>(R.id.DetailName_TextView).text = Data.name
            findViewById<TextView>(R.id.DetailExplain_TextView).text = Data.description
            findViewById<TextView>(R.id.DetailLongExplain_TextView).text = Data.detailDescription
            findViewById<TextView>(R.id.DetailMemberNum_TextView).text = Data.memberCount.toString()
            //TODO : 로고 이미지와 동아리 이미지 추후 추가해야함
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
                startActivity(intent)
            }
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }

    private fun getClubData(jwt: String, clubIdx: Int) {

        val client = OkHttpClient.Builder().build()

        val req = Request.Builder()
            .url("https://moyeo.shop/clubs/$clubIdx")
            .addHeader("x-access-token", jwt)
            .build()

        val response: Response = client.newCall(req).execute()

        val result: String = response.body?.string() ?: "null"
        var jsonObject = JSONObject(result)
        var detailData = jsonObject.getJSONArray("result")

        if (jsonObject.getBoolean("isSuccess")) {
            for (i in 0 until detailData.length()) {

                val entry: JSONObject = detailData.getJSONObject(i)
                Data = ClubData(
                    entry?.get("clubIdx") as Int,
                    entry?.get("sortIdx") as Int,
                    entry?.get("name") as String,
                    entry?.get("description") as String,
                    entry?.get("detailDescription") as String,
                    entry?.get("logoImage") as String,
                    entry?.get("clubImage") as String,
                    entry?.get("areaIdx") as Int,
                    entry?.get("fieldIdx") as Int,
                    entry?.get("userIdx") as Int,
                    entry?.get("memberCount") as Int,
                    entry?.get("isOrganizer") as Int

                )
            }
        } else {
            //작업 실패 했을때
            Log.d("리스트 ", jsonObject.get("code").toString())
            Log.d("리스트 ", jsonObject.get("message").toString())
        }

    }
}
