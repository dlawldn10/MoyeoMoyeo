package com.project.moyeomoyeo

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.URLUtil
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.project.moyeomoyeo.DataClass.ClubData
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ClubDetailActivity : AppCompatActivity() {


    var Data = ClubData(0,0,"","","",
        "","",0, 0, 0, 0, 0, 0,0)

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
                    Log.d("리스트 ", Data.toString())


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
            }else if(Data.isOrganizer == 0 && Data.isMember == 0){
                //외부인 일때
                setContentView(contentViewList[0])
                setContent(0)
            }


        }

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
                    entry?.get("isLike") as Int,
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

            setToolbar()

            findViewById<TextView>(R.id.DetailName_TextView).text = Data.name
            findViewById<TextView>(R.id.DetailExplain_TextView).text = Data.description
            findViewById<TextView>(R.id.DetailLongExplain_TextView).text = Data.detailDescription
            findViewById<TextView>(R.id.DetailMemberNum_TextView).text = Data.memberCount.toString()

            if(URLUtil.isValidUrl(Data.logoImage)){
                Glide.with(this)
                    .load(Data.logoImage)
                    .into(findViewById<ImageView>(R.id.Detail_Photo_imgView))
            }

            if(URLUtil.isValidUrl(Data.clubImage)){
                Glide.with(this)
                    .load(Data.clubImage)
                    .fitCenter()
                    .into(findViewById<ImageView>(R.id.detail_image))
            }

            if(Data.isLike == 1){
                //스크랩한 모임
                findViewById<CheckBox>(R.id.Detail_Scrap_Bttn).isChecked = true
            }else{
                //안한 모임
                findViewById<CheckBox>(R.id.Detail_Scrap_Bttn).isChecked = false
            }



            //지원하기 버튼
            findViewById<Button>(R.id.ApplyClub_Bttn).setOnClickListener {
                ApplyDialogBuilder()
            }


            //스크랩 버튼
            findViewById<CheckBox>(R.id.Detail_Scrap_Bttn).setOnClickListener {
                CoroutineScope(Dispatchers.IO).async {
                    Scrap()
                }
            }

        }else if(contentNum == 1){
            //모임장 버전

            setToolbar()

            val manageBtn = findViewById<Button>(R.id.ManageBtn)
            val attendBtn = findViewById<Button>(R.id.AttendCheckBtn)
            val communityBtn = findViewById<Button>(R.id.CommunityBtn)
            val optionBtn = findViewById<Button>(R.id.OptionBtn)

            findViewById<TextView>(R.id.NameText).text = Data.name
            findViewById<TextView>(R.id.SubNameText).text = Data.description
            findViewById<TextView>(R.id.ContentText).text = Data.detailDescription
            findViewById<TextView>(R.id.CountText).text = Data.memberCount.toString()

            if(URLUtil.isValidUrl(Data.logoImage)){
                Glide.with(this)
                    .load(Data.logoImage)
                    .into(findViewById<ImageView>(R.id.Detail_Photo_imgView))
            }

            if(URLUtil.isValidUrl(Data.clubImage)){
                Glide.with(this)
                    .load(Data.clubImage)
                    .fitCenter()
                    .into(findViewById<ImageView>(R.id.detail_image))
            }

            //모임원 관리 버튼
            manageBtn.setOnClickListener {
                val nextIntent = Intent(this,ManageMemberActivity::class.java)
                nextIntent.putExtra("userData", userData)
                nextIntent.putExtra("clubData", Data)
                startActivity(nextIntent)
            }

            //출석 확인 버튼
            attendBtn.setOnClickListener {
                val nextIntent = Intent(this,AttendCheckMng::class.java)
                nextIntent.putExtra("jwt", userData.jwt)
                nextIntent.putExtra("clubIdx", Data.clubIdx.toString())
                startActivity(nextIntent)
            }

            //모임 수정 버튼
            optionBtn.setOnClickListener {
                val nextIntent = Intent(this,EditClubActivity::class.java)
                nextIntent.putExtra("userData", userData)
                startActivity(nextIntent)
            }

            //커뮤니티 버튼
            communityBtn.setOnClickListener {
                val nextIntent = Intent(this,PostingListActivity::class.java)
                nextIntent.putExtra("userData", userData)
                nextIntent.putExtra("clubIdx", Data.clubIdx)
                startActivity(nextIntent)
            }

        }else if(contentNum == 2){
            //모임원 버전

            setToolbar()

            val communityBtn = findViewById<Button>(R.id.MyClub_Community_Btn)
            val attendBtn = findViewById<Button>(R.id.MyClub_AttendCheck_Btn)
            val withdrawBtn = findViewById<Button>(R.id.withdrawClub_Bttn)

            findViewById<TextView>(R.id.DetailName_TextView).text = Data.name
            findViewById<TextView>(R.id.DetailExplain_TextView).text = Data.description
            findViewById<TextView>(R.id.DetailLongExplain_TextView).text = Data.detailDescription
            findViewById<TextView>(R.id.DetailMemberNum_TextView).text = Data.memberCount.toString()

            if(URLUtil.isValidUrl(Data.logoImage)){
                Glide.with(this)
                    .load(Data.logoImage)
                    .into(findViewById<ImageView>(R.id.Detail_Photo_imgView))
            }

            if(URLUtil.isValidUrl(Data.clubImage)){
                Glide.with(this)
                    .load(Data.clubImage)
                    .fitCenter()
                    .into(findViewById<ImageView>(R.id.detail_image))
            }

            //커뮤니티 버튼
            communityBtn.setOnClickListener {
                //커뮤니티 액티비티로 이동하기
                val intent = Intent(this, PostingListActivity::class.java)
                intent.putExtra("userData", userData)
                intent.putExtra("clubIdx", Data.clubIdx)
                startActivity(intent)
            }

            //출석 체크 버튼
            attendBtn.setOnClickListener {
                val nextIntent = Intent(this,AttendCheck::class.java)
                nextIntent.putExtra("userData", userData)
                nextIntent.putExtra("clubIdx", Data.clubIdx.toString())
                startActivity(nextIntent)
            }
            
            //모임 탈퇴 버튼
            withdrawBtn.setOnClickListener{
                
            }
        }
    }


    fun applyClub(jwt: String, clubIdx: Int, motive: String){
        //1.클라이언트 만들기
        val client = OkHttpClient.Builder().build()


        //2.요청
        val formBody: FormBody = FormBody.Builder()
            .add("motive", motive)
            .build()
        val req = Request.Builder().url("https://moyeo.shop/clubs/$clubIdx").addHeader("x-access-token", jwt).post(formBody).build()



        //3.응답
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(applicationContext, "서버에 접근할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, "다시 시도하세요.", Toast.LENGTH_SHORT).show()
                }
                Log.d("로그인 에러", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                var jsonObject: JSONObject
                var jsonString: String

                try {
                    //주의* response.body!!.string() 값은 한번밖에 호출 못함.
                    jsonString = response.body!!.string()
                    jsonObject = JSONObject(jsonString)

                    if(jsonObject.getBoolean("isSuccess")){
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(applicationContext, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show()

                        }
                    }else{
                        //작업 실패 했을때
                        Log.d("지원하기", jsonObject.get("code").toString())
                        Log.d("지원하기", jsonObject.get("message").toString())
                    }



                }catch (e : JSONException){
                    Log.d("지원하기", "JSON 오류")

                }



            }

        })
    }


    fun ApplyDialogBuilder(){
        var dilaog = Dialog(this)       // Dialog 초기화
        dilaog.setTitle("지원 동기")
        dilaog.setContentView(R.layout.dialog_apply);             // xml 레이아웃 파일과 연결
        dilaog.show()


        var okBttn = dilaog.findViewById<Button>(R.id.applySend_Bttn)
        var cancelBttn = dilaog.findViewById<Button>(R.id.applyCancel_Bttn)
        var motive = dilaog.findViewById<EditText>(R.id.motive_EditText)

        okBttn.setOnClickListener {
            if(motive.text.toString() == null){
                Toast.makeText(this, "지원 동기를 반드시 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }else{
                CoroutineScope(Dispatchers.IO).async {
                    applyClub(userData.jwt, Data.clubIdx, motive.text.toString())
                }
                dilaog.dismiss()
            }

        }

        cancelBttn.setOnClickListener {

            dilaog.dismiss()


        }
    }


    fun Scrap(){
        //스크랩 버튼을 눌렀을때.

        //1.클라이언트 만들기
        val client = OkHttpClient.Builder().build()


        //2.요청
        val formBody: FormBody = FormBody.Builder().build()
        val req = Request.Builder().url("https://moyeo.shop/like/clubs/${Data.clubIdx}")
            .addHeader("x-access-token", userData.jwt)
            .put(formBody)
            .build()


        //3.응답
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(applicationContext, "서버에 접근할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, "다시 시도하세요.", Toast.LENGTH_SHORT).show()
                }
                Log.d("스크랩 실패", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                var jsonObject: JSONObject
                var jsonString: String

                try {
                    //주의* response.body!!.string() 값은 한번밖에 호출 못함.
                    jsonString = response.body!!.string()
                    jsonObject = JSONObject(jsonString)

                    if(jsonObject.getBoolean("isSuccess")){
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(applicationContext, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show()

                        }
                    }else{
                        //작업 실패 했을때
                        Log.d("스크랩", jsonObject.get("code").toString())
                        Log.d("스크랩", jsonObject.get("message").toString())
                    }



                }catch (e : JSONException){
                    Log.d("스크랩", "JSON 오류")

                }



            }

        })
    }

    fun setToolbar(){
        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기
    }

}
