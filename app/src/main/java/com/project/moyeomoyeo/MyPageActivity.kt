package com.project.moyeomoyeo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.project.moyeomoyeo.DataClass.MyData
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

class MyPageActivity : AppCompatActivity() {

    var userData = UserData("", 0, "")
    var myData = MyData(0, "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 버튼 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기

        //이전 인텐트로 전달받은 jwt가 있다면 저장하고, 추후 다음 액티비티로 넘어갈때 전달한다.
        if(intent.getSerializableExtra("userData") != null){
            userData = intent.getSerializableExtra("userData") as UserData
            getMyData()

        }else{
            Log.d("리스트 ", "멤버 조회 실패")
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

    private fun getMyData(){

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                //내(사용자) 정보를 불러온다

                val client = OkHttpClient.Builder().build()
                var url = "https://moyeo.shop/user/info"

                val req = Request.Builder()
                    .url(url)
                    .addHeader("x-access-token", userData.jwt)
                    .build()

                val response: Response = client.newCall(req).execute()

                val result: String = response.body?.string() ?: "null"
                var jsonObject = JSONObject(result)

                if(jsonObject.getBoolean("isSuccess")){
                    var jsonArray = jsonObject.getJSONArray("result")
                    for (i in 0 until jsonArray.length()) {

                        val entry: JSONObject = jsonArray.getJSONObject(i)
                        var tmp: MyData = MyData(
                            entry.get("userIdx") as Int,
                            entry.get("nickname") as String,
                            entry.get("name") as String,
                            entry.get("profileImage") as String
                        )
                        myData = tmp
                    }


                }else{
                    //작업 실패 했을때
                    Log.d("마이페이지", jsonObject.get("code").toString())
                    Log.d("마이페이지", jsonObject.get("message").toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }.await()

            findViewById<TextView>(R.id.MyPageNickname_TextView).text = myData.nickname
            Glide.with(this@MyPageActivity).load(myData.profileImage).into(findViewById<ImageView>(R.id.MyPageProfilePhoto_ImageView))


        }


    }
}