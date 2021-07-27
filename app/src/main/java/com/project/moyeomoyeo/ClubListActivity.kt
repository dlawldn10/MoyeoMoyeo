package com.project.moyeomoyeo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.GroupPreviews

class ClubListActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_list)

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기

        //리사이클러뷰 선언
        //최신 동아리 모집 공고
        val NewCircleList = ArrayList<GroupPreviews>()
        NewCircleList.add(GroupPreviews("펜타킬", "게임 리그 오브 레전드 동아리", 10, null))
        NewCircleList.add(GroupPreviews("S.E.L.", "서울여대 중앙 락밴드 동아리", 14, null))

        viewAdapter = ClubListRecyclerViewAdapter(NewCircleList, applicationContext)
        viewManager = LinearLayoutManager(this)
        recyclerView = findViewById<RecyclerView>(R.id.NewCircles_RecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }


        //인기 동아리 모집 공고
        val PopularCircleList = ArrayList<GroupPreviews>()
        PopularCircleList.add(GroupPreviews("동아리 이름", "동아리 한 줄 소개", 10, null))
        PopularCircleList.add(GroupPreviews("동아리 이름", "동아리 한 줄 소개", 10, null))

        viewAdapter = ClubListRecyclerViewAdapter(PopularCircleList, applicationContext)
        viewManager = LinearLayoutManager(this)
        recyclerView = findViewById<RecyclerView>(R.id.PopularCircles_RecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //전체 동아리 모집 공고
        val AllCircleList = ArrayList<GroupPreviews>()
        AllCircleList.add(GroupPreviews("동아리 이름", "동아리 한 줄 소개", 10, null))
        AllCircleList.add(GroupPreviews("동아리 이름", "동아리 한 줄 소개", 10, null))

        viewAdapter = ClubListRecyclerViewAdapter(AllCircleList, applicationContext)
        viewManager = LinearLayoutManager(this)
        recyclerView = findViewById<RecyclerView>(R.id.AllCircles_RecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }

    //액션바 옵션 반영하기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //mypage와 search 아이콘이 있는 툴바
        menuInflater.inflate(R.menu.mypage_search_toolbar, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            //마이페이지 아이콘 클릭
            R.id.MyPage->
                Toast.makeText(applicationContext, "마이페이지", Toast.LENGTH_SHORT).show()
            //돋보기 아이콘 클릭
            R.id.gotoSearch-> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                }
            //뒤로가기 클릭
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }
}