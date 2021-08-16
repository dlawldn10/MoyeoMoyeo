package com.project.moyeomoyeo.NotUsed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.moyeomoyeo.ManageMemberViewPagerAdapter
import com.project.moyeomoyeo.MyPageActivity
import com.project.moyeomoyeo.R

//ManageMemberActivity로 이름 변경. 이 액티비티는 삭제.
class ManageMember : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_manage_member)
//
//        val tabLayout=findViewById<TabLayout>(R.id.tab_layout)
//        val viewPager2=findViewById<ViewPager2>(R.id.viewPager)
//
//        val adapter= ManageMemberViewPagerAdapter(supportFragmentManager,lifecycle)
//
//        viewPager2.adapter = adapter
//
//        TabLayoutMediator(tabLayout,viewPager2){tab,position->
//            when(position){
//                0->{
//                    tab.text = "전체"
//                }
//                1->{
//                    tab.text = "지원 신청"
//                }
//            }
//        }.attach()
//
//        //툴바
//        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
//        setSupportActionBar(toolbar)
//        var actionBar = supportActionBar
//        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 버튼 활성화
//        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
//        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기
//    }
//
//    //액션바 옵션 반영하기
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        //my page 아이콘만 있는 툴바
//        menuInflater.inflate(R.menu.mypage_toolbar, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.MyPage ->{
//                Toast.makeText(applicationContext, "마이페이지", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, MyPageActivity::class.java)
//                startActivity(intent)
//            }
//            //뒤로가기
//            android.R.id.home->
//                finish()
//        }
//        return super.onOptionsItemSelected(item)
//
//    }
}