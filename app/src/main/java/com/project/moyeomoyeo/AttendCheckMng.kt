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
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.project.moyeomoyeo.DataClass.UserAttendData
import kotlinx.coroutines.*

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

    var temp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attend_check_mng)

        qrCodeIV = findViewById(R.id.QR_imageView)
     //   QRbttn = findViewById(R.id.QRBtn)
        dateText = findViewById(R.id.DateText)
        //recyclerView = findViewById(R.id.recyclerView)
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

        //오늘 날짜 표시
        val now = System.currentTimeMillis()
        val date = SimpleDateFormat("MM월 dd일", Locale.KOREAN).format(now)
        dateText.text = date



        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 버튼 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기


        //QR 생성
        val dateQR = SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(now)

        val data = (clubIdx + dateQR).trim()
        Log.d(TAG, "data = $data")

        QRDate(dateQR.toString())
        val writer = QRCodeWriter()
        try{
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 100,100)
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



        getMemberList()


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
                            Log.d(TAG, "QR 생성 성공")
                        }
                    }

                })

            }.await()
        }

    }

    private fun getMemberList(){

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                val client = OkHttpClient.Builder().build()
                val req = Request.Builder()
                    .url("https://moyeo.shop/clubs/$clubIdx/members")
                    .addHeader("x-access-token", jwt)
                    .build()
                val response = client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(TAG, "멤버조회 요청 실패")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        var jsonObject = JSONObject(response.body?.string())
                        Log.d(TAG, jsonObject.getString("message"))
                        Log.d(TAG, jsonObject.getString("code"))
                        if (jsonObject.getBoolean("isSuccess")) {
                            var resultArray = jsonObject.getJSONArray("result")

                            for (i in 0 until resultArray.length()) {

                                val entryResult: JSONObject = resultArray.getJSONObject(i)

                                //모임원의 isAttended 조회
                                val url = HttpUrl.Builder()
                                    .scheme("https")
                                    .host("moyeo.shop")
                                    .addPathSegment("clubs")
                                    .addPathSegment(clubIdx)
                                    .addPathSegment("attendance")
                                    .addQueryParameter(
                                        "targetIdx",
                                        (entryResult.get("userIdx") as Int).toString()
                                    )
                                    .build()

                                val client = OkHttpClient.Builder().build()
                                val req = Request.Builder()
                                    .url(url)
                                    .addHeader("x-access-token", jwt)
                                    .build()
                                val response = client.newCall(req).enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        Log.d(TAG, "출석 상황 조회 요청 실패")
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        var jsonObject = JSONObject(response.body?.string())
                                        Log.d(TAG, jsonObject.getString("message"))
                                        Log.d(TAG, jsonObject.getString("code"))

                                        if (jsonObject.getBoolean("isSuccess")) {
                                            var resultArray2 = jsonObject.getJSONArray("result")
                                            val today = SimpleDateFormat(
                                                "yyyy-MM-dd",
                                                Locale.KOREAN
                                            ).format(System.currentTimeMillis())
                                            for (i in 0 until resultArray2.length()) {
                                                val entryResult2: JSONObject =
                                                    resultArray2.getJSONObject(i)
                                                val QRday =
                                                    (entryResult2.get("createdAt") as String).substring(0, 10)

                                                if (QRday == today) {
                                                    temp = resultArray2.getJSONObject(i)
                                                        .get("isAttended") as Int
                                                }
                                            }
                                            val isAttend = temp
                                            var tmp: UserAttendData = UserAttendData(
                                                jwt,
                                                clubIdx.toInt(),
                                                entryResult.get("userIdx") as Int,
                                                entryResult.get("nickname") as String,
                                                entryResult.get("profileImage") as String,
                                                isAttend
                                            )
                                            MemberList.add(tmp)
                                            if (isAttend == 1) {
                                                memberSize_attend++
                                            }
                                        }
                                    }

                                })

                            }

                            memberSize = resultArray.length()


                        }
                        memberNumText.text = "$memberSize_attend / $memberSize"


                    }
                }


                )
            }.await()

            delay(500L)

            viewAdapter = AttendMemberRecyclerViewAdapter(MemberList, applicationContext)
            viewManager = LinearLayoutManager(applicationContext)
            recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

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