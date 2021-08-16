package com.project.moyeomoyeo

import android.R.attr.key
import android.R.attr.value
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.moyeomoyeo.DataClass.ClubData
import com.project.moyeomoyeo.DataClass.UserData


class ManageMemberActivity : AppCompatActivity() {


    var userData = UserData("", 0, "")
    var Data = ClubData(0,0,"","","",
        "","",0, 0, 0, 0, 0, 0, 0)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_member)

        if(intent.getSerializableExtra("userData") != null){
            userData = intent.getSerializableExtra("userData") as UserData
            Data = intent.getSerializableExtra("clubData") as ClubData

            Log.d("멤버조회", "프래그먼트로 값 전달")


        }else{
            Log.d("리스트 ", "멤버 조회 실패")
        }



        val tabLayout=findViewById<TabLayout>(R.id.tab_layout)
        val viewPager2=findViewById<ViewPager2>(R.id.viewPager)

        val adapter=ManageMemberViewPagerAdapter(supportFragmentManager,lifecycle, userData, Data)

        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout,viewPager2){tab,position->
            when(position){
                0->{

                    tab.text = "전체"
                }
                1->{
                    tab.text = "지원 신청"
                }
            }
        }.attach()

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 버튼 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기
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
            //뒤로가기
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }


}