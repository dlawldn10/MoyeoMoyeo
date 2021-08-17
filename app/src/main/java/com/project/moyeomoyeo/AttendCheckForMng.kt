package com.project.moyeomoyeo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.project.moyeomoyeo.DataClass.UserAttendData
import com.project.moyeomoyeo.DataClass.UserData
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class AttendCheckForMng : AppCompatActivity() {

    lateinit var userData : UserData
    lateinit var jwt : String
    lateinit var userIdxStr : String
    var userIdx = 0
    lateinit var clubIdx : String
    val TAG = "출석체크(모임장입장모임원)"

    var attendCount : Int = 0
    var allCount = 0
    var progressCount : Int = 0

    lateinit var attendCountText : TextView
    lateinit var allCountText : TextView
    lateinit var progressCountText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attend_check_for_mng)

        attendCountText = findViewById(R.id.attendCount_text)
        allCountText = findViewById(R.id.allCount_text)
        progressCountText = findViewById(R.id.progressCount_text)

        if(intent.getStringExtra("clubIdx") != null){
            clubIdx = intent.getStringExtra("clubIdx")!!
        }
        else{
            Log.d(TAG, "clubIdx 값이 없습니다")
        }

        if(intent.getStringExtra("userIdx") != null){
            userIdxStr = intent.getStringExtra("userIdx")!!
            userIdx = userIdxStr.toInt()
        }
        else{
            Log.d(TAG, "userIdx 값이 없습니다")
        }

        if(intent.getStringExtra("jwt") != null){
            jwt = intent.getStringExtra("jwt")!!
        }
        else{
            Log.d(TAG, "jwt 값이 없습니다")
        }

        LoadAttendCount()

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 버튼 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기
    }

    //액션바 옵션 반영하기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //my page 아이콘만 있는 툴바
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
            //뒤로가기
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }

    private fun LoadAttendCount(){

        val client = OkHttpClient.Builder().build()
        val req = Request.Builder()
            .url("https://moyeo.shop/clubs/$clubIdx/attendance?targetIdx=$userIdx")
            .addHeader("x-access-token", jwt)
            .build()
        val response = client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "출석 조회 요청 실패")
            }

            override fun onResponse(call: Call, response: Response) {

                var jsonObject = JSONObject(response.body?.string())
                Log.d(TAG, jsonObject.getString("message"))
                Log.d(TAG, jsonObject.getString("code"))

                if(jsonObject.getBoolean("isSuccess")){

                    progressCount = jsonObject.getInt("meetingCount")
                    attendCount = jsonObject.getInt("attendanceCount")

                    SetText()

                    Log.d(TAG, "attendCount : $attendCount")
                    Log.d(TAG, "progressCount : $progressCount")
                }
            }

        })
    }
    private fun SetText(){
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            progressCountText.text = "$progressCount 회"
            attendCountText.text = "$attendCount 회"
        }, 0)
    }

}


