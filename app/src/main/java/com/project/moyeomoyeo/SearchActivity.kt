package com.project.moyeomoyeo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.ClubData

class SearchActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기


//        //리사이클러뷰 선언
//        val PreviewList = ArrayList<ClubData>()
//        PreviewList.add(ClubData("펜타킬", "게임 리그 오브 레전드 동아리", 10, null))
//        PreviewList.add(ClubData("S.E.L.", "서울여대 중앙 락밴드 동아리", 14, null))
//        PreviewList.add(ClubData("MBTI다모여", "MBTI 과몰입 모임", 100, null))
//        PreviewList.add(ClubData("공무원 합격하자", "(예비)공무원 스터디", 100, null))
//
//        viewAdapter = ReccomendClubRecyclerViewAdapter(PreviewList)
//        viewManager = LinearLayoutManager(this)
//        recyclerView = findViewById<RecyclerView>(R.id.Recommend_RecyclerView).apply {
//            setHasFixedSize(true)
//            layoutManager = viewManager
//            adapter = viewAdapter
//        }



    }

    //액션바 옵션 반영하기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //검색버튼 누름
            R.id.submit_button ->
                Toast.makeText(applicationContext, "검색시작", Toast.LENGTH_SHORT).show()
            //뒤로가기 버튼
            android.R.id.home->
                finish()

        }
        return super.onOptionsItemSelected(item)
    }
}

