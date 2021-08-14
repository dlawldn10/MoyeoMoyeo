package com.project.moyeomoyeo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class AttendCheck : AppCompatActivity() {

    lateinit var userData : UserData
    lateinit var jwt : String
    var userIdx = 0
    lateinit var clubIdx : String
    val TAG = "출석체크(모임원)"

    var attendCount = 0
    var allCount = 0
    var progressCount = 0

    lateinit var attendCountText : TextView
    lateinit var allCountText : TextView
    lateinit var progressCountText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attend_check)

        attendCountText = findViewById(R.id.attendCount_text)
        allCountText = findViewById(R.id.allCount_text)
        progressCountText = findViewById(R.id.progressCount_text)

        if(intent.getSerializableExtra("userData") != null){
            userData = intent.getSerializableExtra("userData") as UserData

            jwt = userData.jwt
            userIdx = userData.userIdx
        }
        else{
            Log.d(TAG, "UserData가 없습니다")
        }

        if(intent.getStringExtra("clubIdx") != null){
            clubIdx = intent.getStringExtra("clubIdx")!!
        }
        else{
            Log.d(TAG, "clubIdx 값이 없습니다")
        }

        LoadAttendCount()

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 버튼 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기


        var createQR = findViewById<Button>(R.id.QRBtn).setOnClickListener {
            val intent = Intent(this, ScanQRActivity::class.java)
            startActivity(intent)
        }
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
            .url("https://moyeo.shop/clubs/$clubIdx/attendance")
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
                    var resultArray = jsonObject.getJSONArray("result")
                    var userArray = jsonObject.getJSONArray("userInfo")

                    for(i in 0 until userArray.length()){
                        val entryResult: JSONObject = resultArray.getJSONObject(i)
                        val entryUser: JSONObject = userArray.getJSONObject(i)
                        if(entryResult.get("isAttended") == 1 && entryUser.get("userIdx") == userIdx){
                            attendCount++
                        }
                    }
                    attendCountText.text = attendCount.toString()
                    Log.d(TAG, "attendCount : $attendCount")
                }
            }

        })
    }

    private fun LoadProgressCount(){

        val client = OkHttpClient.Builder().build()
        val req = Request.Builder()
            .url("https://moyeo.shop/clubs/$clubIdx/qr")
            .addHeader("x-access-token", jwt)
            .build()
        val response = client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "진행값 조회 요청 실패")
            }

            override fun onResponse(call: Call, response: Response) {

                var jsonObject = JSONObject(response.body?.string())
                Log.d(TAG, jsonObject.getString("message"))
                Log.d(TAG, jsonObject.getString("code"))

                if(jsonObject.getBoolean("isSuccess")){
                    var resultArray = jsonObject.getJSONArray("result")

                    progressCount = resultArray.length()
                    progressCountText.text = progressCount.toString()

                    Log.d(TAG, "progressCount : $progressCount")
                }
            }

        })
    }
}