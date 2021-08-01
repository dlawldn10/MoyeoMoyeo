package com.project.moyeomoyeo

import android.R.attr.name
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.moyeomoyeo.DataClass.ClubData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject


class ClubDetailActivity : AppCompatActivity() {

    //사용자 토큰
    var jwt = "null"
    var club = 0
    var Data = ClubData(0,0,"","","",
        "","",0, 0, 0, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_detail)

        CoroutineScope(Dispatchers.Main).launch {

            if(intent.getStringExtra("jwt") != null){

                jwt = intent.getStringExtra("jwt")!!
                club = intent.getIntExtra("clubIdx", 0)

//                Data = getClubData(jwt, club)

                Log.d("리스트 ", Data.clubIdx.toString())
                findViewById<TextView>(R.id.DetailName_TextView).text = Data.name
                findViewById<TextView>(R.id.DetailExplain_TextView).text = Data.description
                findViewById<TextView>(R.id.DetailLongExplain_TextView).text = Data.detailDescription
                findViewById<TextView>(R.id.DetailMemberNum_TextView).text = Data.memberCount.toString()
                //TODO : 로고 이미지와 동아리 이미지 추후 추가해야함
            }else{
                Log.d("리스트 ", "멤버 조회 실패")
            }


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

//    fun getClubData(jwt: String, clubIdx: Int): ClubData{
//
//        CoroutineScope(Dispatchers.IO).async {
//            //동아리 정보를 받아온다.
//        try {
//
//            val client = OkHttpClient.Builder().build()
//            val httpUrl: HttpUrl = HttpUrl.Builder()
//                .scheme("https")
//                .host("moyeo.shop")
//                .addPathSegment("clubs/:clubIdx")
//                .addQueryParameter("clubIdx", clubIdx.toString())
//                .build()
//
//
//            val req = Request.Builder()
//                .url(httpUrl)
//                .addHeader("x-access-token", jwt)
//                .build()
//
//            val response: Response = client.newCall(req).execute()
//
//            val result: String = response.body?.string() ?: "null"
//            var jsonObject = JSONObject(result)
//
//            if(jsonObject.getBoolean("isSuccess")){
//                Data = ClubData(
//                    jsonObject.get("clubIdx") as Int,
//                    jsonObject.get("sortIdx") as Int,
//                    jsonObject.get("name") as String,
//                    jsonObject.get("description") as String,
//                    jsonObject.get("detailDescription") as String,
//                    jsonObject.get("logoImage") as String,
//                    jsonObject.get("clubImage") as String,
//                    jsonObject.get("areaIdx") as Int,
//                    jsonObject.get("fieldIdx") as Int,
//                    jsonObject.get("userIdx") as Int,
//                    jsonObject.get("memberCount") as Int,
//                    jsonObject.get("isOrganizer") as Int
//
//                )
//                Log.d("리스트 ", Data.clubIdx.toString())
//                Log.d("리스트: ", jsonObject.toString())
//            }else{
//                //작업 실패 했을때
//                Log.d("리스트: ", jsonObject.get("code").toString())
//                Log.d("리스트: ", jsonObject.get("message").toString())
//            }
//
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        }
//
//
//        return Data
//
//    }
}