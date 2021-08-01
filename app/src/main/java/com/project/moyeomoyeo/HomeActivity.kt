package com.project.moyeomoyeo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.ClubData

//메인 화면 액티비티
class HomeActivity : AppCompatActivity() {

    //뒤로가기 타이머
    var backKeyPressedTime: Long = 0

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var jwt = ""
        //이전 인텐트로 전달받은 jwt가 있다면 저장하고, 추후 다음 액티비티로 넘어갈때 전달한다.
        if(intent.getStringExtra("jwt") != null){
            jwt = intent.getStringExtra("jwt")!!
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
        val CircleBttn = findViewById<Button>(R.id.Circles_Bttn)
        val StudyGroupBttn = findViewById<Button>(R.id.StudyGroups_Bttn)
        val ExtraActivityBttn = findViewById<Button>(R.id.ExtraActivies_Bttn)
        val OtherGroupBttn = findViewById<Button>(R.id.OtherGroups_Bttn)

        //각 버튼 씬 전환
        //동아리 게시판
        CircleBttn.setOnClickListener{
            val intent = Intent(this, ClubListActivity::class.java)
            //토큰 정보를 다음 액티비티(동아리 목록 조회 액티비티)로 넘긴다
            intent.putExtra("jwt", jwt)
            startActivity(intent)
        }

        //스터디 그룹 게시판
        StudyGroupBttn.setOnClickListener{
            val intent = Intent(this, StudyActivity::class.java)
            startActivity(intent)
        }

        //대외활동 게시판
        ExtraActivityBttn.setOnClickListener{
            val intent = Intent(this, SpecActivity::class.java)
            startActivity(intent)
        }

        //기타 게시판
        OtherGroupBttn.setOnClickListener{
            val intent = Intent(this, EtcActivity::class.java)
            startActivity(intent)
        }


//        //리사이클러뷰 선언
//        val PreviewList = ArrayList<ClubData>()
//        PreviewList.add(ClubData("펜타킬", "게임 리그 오브 레전드 동아리", 10, null))
//        PreviewList.add(ClubData("S.E.L.", "서울여대 중앙 락밴드 동아리", 14, null))
//
//        viewAdapter = MyClubRecyclerViewAdapter(PreviewList, this)
//        viewManager = LinearLayoutManager(this)
//        recyclerView = findViewById<RecyclerView>(R.id.MyGroup_RecyclerView).apply {
//            setHasFixedSize(true)
//            layoutManager = viewManager
//            adapter = viewAdapter
//        }



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
}