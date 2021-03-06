

package com.project.moyeomoyeo

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.moyeomoyeo.DataClass.ClubPreviewData
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

//메인 화면 액티비티
class HomeActivity : AppCompatActivity() {

    //뒤로가기 타이머
    var backKeyPressedTime: Long = 0

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    var userData = UserData("", 0, "")
    var myData = MyData(0, "", "", "", "", 0)

    val MyClubList = ArrayList<ClubPreviewData>()
    val RecommendList = ArrayList<ClubPreviewData>()

    val TAG = "나의 모임"

    var fieldFilterValues = arrayListOf<String>("전체", "서버", "웹", "iOS", "안드로이드", "자료구조/알고리즘", "게임", "AI", "데이터 분석", "데이터 시각화", "기타")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY)

        setContentView(R.layout.activity_home)

        getMyData()

        //이전 인텐트로 전달받은 jwt가 있다면 저장하고, 추후 다음 액티비티로 넘어갈때 전달한다.
        if(intent.getSerializableExtra("userData") != null){
            userData = intent.getSerializableExtra("userData") as UserData
        }else{
            Log.d("리스트 ", "멤버 조회 실패")
        }

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)      //뒤로가기 버튼X
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기


        //버튼 선언
        val CircleBttn = findViewById<ImageButton>(R.id.Circles_Bttn)
        val StudyGroupBttn = findViewById<ImageButton>(R.id.StudyGroups_Bttn)
        val ExtraActivityBttn = findViewById<ImageButton>(R.id.ExtraActivies_Bttn)
        val OtherGroupBttn = findViewById<ImageButton>(R.id.OtherGroups_Bttn)
        val CreateBttn = findViewById<FloatingActionButton>(R.id.CreateClub_Bttn)
        val EnterpriseListBttn = findViewById<ImageButton>(R.id.Enterprise_Bttn)

        //각 버튼 씬 전환
        //동아리 게시판x -> 개발
        CircleBttn.setOnClickListener{
            val intent = Intent(this, ClubListActivity::class.java)
            //토큰 정보를 다음 액티비티(동아리 목록 조회 액티비티)로 넘긴다
            intent.putExtra("userData", userData)
            intent.putExtra("sortIdx", 1)
            startActivity(intent)
        }

        //스터디 그룹 게시판x -> 디자인
        StudyGroupBttn.setOnClickListener{
            val intent = Intent(this, ClubListActivity::class.java)
            intent.putExtra("userData", userData)
            intent.putExtra("sortIdx", 2)
            startActivity(intent)
        }

        //대외활동 게시판x -> 마케팅
        ExtraActivityBttn.setOnClickListener{
            val intent = Intent(this, ClubListActivity::class.java)
            intent.putExtra("userData", userData)
            intent.putExtra("sortIdx", 3)
            startActivity(intent)
        }

        //기타 게시판x -> 기획
        OtherGroupBttn.setOnClickListener{
            val intent = Intent(this, ClubListActivity::class.java)
            intent.putExtra("userData", userData)
            intent.putExtra("sortIdx", 4)
            startActivity(intent)
        }

        EnterpriseListBttn.setOnClickListener{
            val intent = Intent(this, EnterpriseListActivity::class.java)
            startActivity(intent)
        }

        //모임 생성
        CreateBttn.setOnClickListener {
            val intent = Intent(this, CreateClub::class.java)
            intent.putExtra("jwt", userData.jwt)
            startActivity(intent)
        }

        getRecommendList()
        getMyList()


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
                val intent = Intent(this, MyPageActivity::class.java)
                intent.putExtra("userData", userData)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)

    }

    //뒤로가기 두번 누르면 종료
    override fun onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish()
        }

    }

    private fun getRecommendList(){

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                //동아리 목록 정보를 받아온다.
                //리스트 초기화
                RecommendList.clear()

                val client = OkHttpClient.Builder().build()
                var url = "https://moyeo.shop/recommends/clubs"

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
                        var tmp : ClubPreviewData = ClubPreviewData(
                            entry.get("clubIdx") as Int,
                            entry.get("name") as String,
                            entry.get("description") as String,
                            entry.get("logoImage") as String,
                            1,
                            entry.get("nickname") as String,
                            entry.get("profileImage") as String,
                            entry.get("fieldIdx") as Int
                        )
                        RecommendList.add(tmp)
                    }
                    Log.d("추천", jsonArray.toString())

                }else{
                    //작업 실패 했을때
                    Log.d(TAG, jsonObject.get("code").toString())
                    Log.d(TAG, jsonObject.get("message").toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }.await()

            //리사이클러뷰 선언
            viewAdapter = RecommendClubListRecyclerViewAdapter(RecommendList, applicationContext, userData, fieldFilterValues)
            viewManager = LinearLayoutManager(applicationContext)
            (viewManager as LinearLayoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView = findViewById<RecyclerView>(R.id.HomeRecommend_RecyclerView).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

        }


    }

    private fun getMyList(){

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                //동아리 목록 정보를 받아온다.
                //리스트 초기화
                MyClubList.clear()

                val client = OkHttpClient.Builder().build()
                var url = "https://moyeo.shop/my/clubs"

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
                        var tmp : ClubPreviewData = ClubPreviewData(
                            entry.get("clubIdx") as Int,
                            entry.get("name") as String,
                            entry.get("description") as String,
                            entry.get("logoImage") as String,
                            1,
                            entry.get("nickname") as String,
                            entry.get("profileImage") as String,
                            0
                        )
                        MyClubList.add(tmp)
                    }
                    Log.d(TAG, jsonArray.toString())

                }else{
                    //작업 실패 했을때
                    Log.d(TAG, jsonObject.get("code").toString())
                    Log.d(TAG, jsonObject.get("message").toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }.await()

            //리사이클러뷰 선언
            viewAdapter = ClubListRecyclerViewAdapter(MyClubList, applicationContext, userData)
            viewManager = LinearLayoutManager(applicationContext)
            recyclerView = findViewById<RecyclerView>(R.id.MyGroup_RecyclerView).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

        }


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
                            "",
                            entry.get("profileImage") as String,
                            0
                        )
                        myData = tmp
                    }


                }else{
                    //작업 실패 했을때
                    Log.d(TAG, jsonObject.get("code").toString())
                    Log.d(TAG, jsonObject.get("message").toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }.await()

            findViewById<TextView>(R.id.HomeUserNickName_TextView).text = myData.nickname

        }


    }


    override fun onRestart() {
        super.onRestart()
        getMyList()
    }
}