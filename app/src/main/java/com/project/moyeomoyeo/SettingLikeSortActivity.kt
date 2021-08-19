package com.project.moyeomoyeo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import com.project.moyeomoyeo.DataClass.MyData
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class SettingLikeSortActivity : AppCompatActivity() {

    var TAG = "프로필"

    var myData = MyData(0, "", "", "", "", 0)

    var userData = UserData("", 0, "")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_like_sort)

        if(intent.getSerializableExtra("userData") != null){
            userData = intent.getSerializableExtra("userData") as UserData
            myData = intent.getSerializableExtra("myData") as MyData

        }else{
            Log.d("리스트 ", "멤버 조회 실패")
        }


        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 버튼 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기

        val likeDevelop = findViewById<ImageButton>(R.id.LikeSort_Develop)
        val likeDesign = findViewById<ImageButton>(R.id.LikeSort_Design)
        val likeMarketing = findViewById<ImageButton>(R.id.LikeSort_Marketing)
        val likePlan = findViewById<ImageButton>(R.id.LikeSort_Plan)

        likeDevelop.setOnClickListener {
            myData.likeSortIdx = 1
            UpdateProfile()
        }

        likeDesign.setOnClickListener {
            myData.likeSortIdx = 2
            UpdateProfile()
        }

        likeMarketing.setOnClickListener {
            myData.likeSortIdx = 3
            UpdateProfile()
        }

        likePlan.setOnClickListener {
            myData.likeSortIdx = 4
            UpdateProfile()
        }


    }


    private fun UpdateProfile(){

        val client = OkHttpClient()

        val json = JSONObject()
        json.put("nickname",myData.nickname)
        json.put("name",myData.name)
        json.put("phoneNumber",myData.phoneNumber)
        json.put("profileImage",myData.profileImage)
        json.put("likeSortIdx",myData.likeSortIdx)


        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        var url = "https://moyeo.shop/user"

        val request = Request.Builder()
            .header("x-access-token",userData.jwt)
            .url(url)
            .put(body)
            .build()


        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.d(TAG, "Fail")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "Success")

                val result= response.body?.string() ?: "null"
                var jsonObject = JSONObject(result)

                Log.d(TAG, jsonObject.get("code").toString())
                Log.d(TAG, jsonObject.get("message").toString())

                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(applicationContext, jsonObject.get("message").toString(), Toast.LENGTH_SHORT)

                }

                if (jsonObject.get("code") == 1000){
                    //홈 화면으로
                    gotoHome(userData)
                }


            }
        })
    }

    fun gotoHome(userData : UserData){
        val intent = Intent(this, HomeActivity::class.java)
        //토큰 정보를 다음 액티비티(홈 액티비티)로 넘긴다
        intent.putExtra("userData", userData)
        startActivity(intent)
    }
}