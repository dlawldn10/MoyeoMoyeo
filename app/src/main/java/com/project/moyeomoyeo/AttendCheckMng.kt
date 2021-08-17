package com.project.moyeomoyeo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.project.moyeomoyeo.DataClass.ClubPreviewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.project.moyeomoyeo.DataClass.UserAttendData

class AttendCheckMng : AppCompatActivity() {

    lateinit var qrCodeIV : ImageView
    lateinit var QRbttn : Button
    lateinit var dateText : TextView
    lateinit var memberNumText : TextView

    lateinit var recyclerView : RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    var jwt = ""
    var clubIdx = ""
    val TAG = "출석체크(모임장)"

    var memberSize = 0
    var memberSize_attend = 0

    var MemberList = ArrayList<UserAttendData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attend_check_mng)

        qrCodeIV = findViewById(R.id.QR_imageView)
        QRbttn = findViewById(R.id.QRBtn)
        dateText = findViewById(R.id.DateText)
        recyclerView = findViewById(R.id.recyclerView)
        memberNumText = findViewById(R.id.MemberNumText)

        if(intent.getStringExtra("jwt") != null){
            jwt = intent.getStringExtra("jwt")!!
        }
        else{
            Log.d(TAG, "jwt 토큰이 없습니다")
        }

        if(intent.getStringExtra("clubIdx") != null){
            clubIdx = intent.getStringExtra("clubIdx")!!
        }
        else{
            Log.d(TAG, "clubIdx 값이 없습니다")
        }

        val now = System.currentTimeMillis()
        val date = SimpleDateFormat("MM월 dd일", Locale.KOREAN).format(now)
        dateText.text = date

        getMemberList()


        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 버튼 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기

        QRbttn.setOnClickListener {

            val now = System.currentTimeMillis()
            val date = SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(now)

            val data = (clubIdx + date).trim()
            Log.d(TAG, "data = $data")

            QRDate(date.toString())
            val writer = QRCodeWriter()
            try{
                val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512,512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width){
                    for (y in 0 until height){
                        bmp.setPixel(x,y,if(bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                    }
                }
                qrCodeIV.setImageBitmap(bmp)

            }catch(e: WriterException){
                e.printStackTrace()
            }


        }

    }

    private fun QRDate(date : String) {
        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {

                val url = HttpUrl.Builder()
                    .scheme("https")
                    .host("moyeo.shop")
                    .addPathSegment("clubs")
                    .addPathSegment(clubIdx)
                    .addPathSegment("attendance")
                    .addPathSegment("qr")
                    .addQueryParameter("qrDate", date)
                    .build()

                Log.d(TAG, "url : $url")

                val formbody = FormBody.Builder()
                    .build()
                val client = OkHttpClient.Builder().build()
                val req = Request.Builder()
                    .url(url)
                    .addHeader("x-access-token", jwt)
                    .post(formbody)
                    .build()
                val response = client.newCall(req).enqueue(object : Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(TAG, "FAIL")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        var jsonObject = JSONObject(response.body?.string())
                        Log.d(TAG, jsonObject.getString("message"))
                        Log.d(TAG, jsonObject.getString("code"))
                        if(jsonObject.getBoolean("isSuccess")){
                        }
                    }

                })


            }.await()
        }
    }

    private fun getMemberList(){

        val client = OkHttpClient.Builder().build()
        val req = Request.Builder()
            .url("https://moyeo.shop/clubs/$clubIdx/attendance")
            .addHeader("x-access-token", jwt)
            .build()
        val response = client.newCall(req).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "멤버조회 요청 실패")
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
                        var tmp : UserAttendData = UserAttendData(
                            entryUser.get("nickname") as String,
                            entryUser.get("profileImage") as String,
                            entryResult.get("isAttended") as Int
                        )
                        MemberList.add(tmp)
                        if(tmp.isAttended == 1){
                            memberSize_attend++
                        }
                    }

                    memberSize = resultArray.length()
                }
                memberNumText.text = "$memberSize_attend / $memberSize"
            }

        })


        viewAdapter = AttendMemberRecyclerViewAdapter(MemberList, applicationContext)
        viewManager = LinearLayoutManager(applicationContext)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            //뒤로가기
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }


}