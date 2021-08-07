package com.project.moyeomoyeo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.moyeomoyeo.DataClass.ClubData
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject


class ClubDetailActivity : AppCompatActivity() {


    var Data = ClubData(0,0,"","","",
        "","",0, 0, 0, 0, 0, 0)

    var userData = UserData("", 0, "")

    //외부인 버전, 모임장 버전, 모임원 버전
    var contentViewList = arrayListOf<Int>(R.layout.activity_club_detail, R.layout.activity_my_club_mng, R.layout.activity_my_club_detail)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {

            withContext(
                CoroutineScope(Dispatchers.IO).coroutineContext
            ) {


                if(intent.getSerializableExtra("userData") != null){
                    userData = intent.getSerializableExtra("userData") as UserData
                    getClubData(userData.jwt, intent.getIntExtra("clubIdx", 0))


                }else{
                    Log.d("리스트 ", "멤버 조회 실패")
                }


            }

            if(Data.isOrganizer == 1 && Data.isMember == 0){
                //모임장일때
                setContentView(contentViewList[1])
                setContent(1)
            }else if(Data.isOrganizer == 0 && Data.isMember == 1){
                //동아리원 일때
                setContentView(contentViewList[2])
                setContent(2)
            }else{
                //외부인 일때
                setContentView(contentViewList[0])
                setContent(0)
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
                intent.putExtra("userData", userData)
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
        var jsonObject = JSONObject(response.body?.string() ?: "null")
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
                    entry?.get("isMember") as Int,
                    entry?.get("isOrganizer") as Int
                )
            }



        } else {
            //작업 실패 했을때
            Log.d("리스트 ", jsonObject.get("code").toString())
            Log.d("리스트 ", jsonObject.get("message").toString())
        }

    }

    fun setContent(contentNum : Int){
        Log.d("디테일", contentNum.toString())
        if(contentNum == 0){
            //외부인 버전
            findViewById<TextView>(R.id.DetailName_TextView).text = Data.name
            findViewById<TextView>(R.id.DetailExplain_TextView).text = Data.description
            findViewById<TextView>(R.id.DetailLongExplain_TextView).text = Data.detailDescription
            findViewById<TextView>(R.id.DetailMemberNum_TextView).text = Data.memberCount.toString()

        }else if(contentNum == 1){
            //모임장 버전
            val manageBtn = findViewById<Button>(R.id.ManageBtn)
            val attendBtn = findViewById<Button>(R.id.AttendCheckBtn)
            val optionBtn = findViewById<Button>(R.id.OptionBtn)

            findViewById<TextView>(R.id.NameText).text = Data.name
            findViewById<TextView>(R.id.SubNameText).text = Data.description
            findViewById<TextView>(R.id.ContentText).text = Data.detailDescription
            findViewById<TextView>(R.id.CountText).text = Data.memberCount.toString()

            manageBtn.setOnClickListener {
                val nextIntent = Intent(this,ManageMember::class.java)
                startActivity(nextIntent)
            }

            attendBtn.setOnClickListener {
                val nextIntent = Intent(this,AttendCheckMng::class.java)
                startActivity(nextIntent)
            }

            optionBtn.setOnClickListener {
                val nextIntent = Intent(this,EditClub::class.java)
                startActivity(nextIntent)
            }

        }else if(contentNum == 2){
            //모임원 버전
            val communityBtn = findViewById<Button>(R.id.MyClub_Community_Btn)
            val attendBtn = findViewById<Button>(R.id.MyClub_AttendCheck_Btn)

            findViewById<TextView>(R.id.DetailName_TextView).text = Data.name
            findViewById<TextView>(R.id.DetailExplain_TextView).text = Data.description
            findViewById<TextView>(R.id.DetailLongExplain_TextView).text = Data.detailDescription
            findViewById<TextView>(R.id.DetailMemberNum_TextView).text = Data.memberCount.toString()

            communityBtn.setOnClickListener {
                //커뮤니티 액티비티로 이동하기
            }

            attendBtn.setOnClickListener {
                val nextIntent = Intent(this,AttendCheckMng::class.java)
                startActivity(nextIntent)
            }
        }
    }
}
