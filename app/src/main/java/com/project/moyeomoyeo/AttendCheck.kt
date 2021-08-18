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
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.AwardData
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

    var attendCount : Int = 0
    var awardCount = 0
    var progressCount : Int = 0

    lateinit var attendCountText : TextView
    lateinit var awardCountText : TextView
    lateinit var progressCountText : TextView

    lateinit var gridView : GridView
    lateinit var AwardGridViewAdapter : AwardGridViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attend_check)

        attendCountText = findViewById(R.id.attendCount_text)
        awardCountText = findViewById(R.id.awardCount_text)
        progressCountText = findViewById(R.id.progressCount_text)

        gridView = findViewById(R.id.award_gridView)

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

                    progressCount = jsonObject.getInt("meetingCount")
                    attendCount = jsonObject.getInt("attendanceCount")


                    AwardGridViewAdapter = AwardGridViewAdapter(applicationContext, LoadAward(attendCount))
                    gridView?.adapter = AwardGridViewAdapter

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
            awardCountText.text = "$awardCount 회"
        }, 0)
    }

    private fun LoadAward(attendCount : Int) : ArrayList<AwardData>{
        var AwardList : ArrayList<AwardData> = ArrayList()

        //초기값 (댓글, 게시글 업적 수 포함)
        awardCount = 2

        if(attendCount >= 1){
            AwardList.add(AwardData("첫 출석", true))
            awardCount++
        }
        else{
            AwardList.add(AwardData("첫 출석", false))
        }

        if(attendCount >= 5){
            AwardList.add(AwardData("출석 5회", true))
            awardCount++
        }
        else{
            AwardList.add(AwardData("출석 5회", false))
        }

        if(attendCount >= 10){
            AwardList.add(AwardData("출석 10회", true))
            awardCount++
        }
        else{
            AwardList.add(AwardData("출석 10회", false))
        }

        if(attendCount >= 15){
            AwardList.add(AwardData("출석 15회", true))
            awardCount++
        }
        else{
            AwardList.add(AwardData("출석 15회", false))
        }

        AwardList.add(AwardData("첫 게시글", true))
        AwardList.add(AwardData("게시글 3개", false))
        AwardList.add(AwardData("게시글 5개", false))
        AwardList.add(AwardData("게시글 10개", false))
        AwardList.add(AwardData("첫 댓글", true))
        AwardList.add(AwardData("댓글 5개", false))
        AwardList.add(AwardData("댓글 10개", false))
        AwardList.add(AwardData("댓글 15개", false))

        return AwardList
    }
}



