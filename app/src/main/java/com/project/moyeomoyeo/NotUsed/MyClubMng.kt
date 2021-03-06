package com.project.moyeomoyeo.NotUsed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.project.moyeomoyeo.AttendCheckMng
import com.project.moyeomoyeo.MyPageActivity
import com.project.moyeomoyeo.R

//ClubListActivity로 합쳐지면서 필요없게됨. 삭제.
class MyClubMng : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_club_mng)

        val manageBtn = findViewById<Button>(R.id.ManageBtn)
        val attendBtn = findViewById<Button>(R.id.AttendCheckBtn)
        val optionBtn = findViewById<Button>(R.id.OptionBtn)

        manageBtn.setOnClickListener {
            val nextIntent = Intent(this, ManageMember::class.java)
            startActivity(nextIntent)
        }

        attendBtn.setOnClickListener {
            val nextIntent = Intent(this, AttendCheckMng::class.java)
            startActivity(nextIntent)
        }

        optionBtn.setOnClickListener {
            val nextIntent = Intent(this, EditClub::class.java)
            startActivity(nextIntent)
        }

        //툴바
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)      //뒤로가기 활성화
        actionBar?.setDisplayShowCustomEnabled(true)    //커스텀 허용
        actionBar?.setDisplayShowTitleEnabled(false)     //기본 제목 없애기
    }

    //액션바 옵션 반영하기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //마이페이지만 있는 툴바
        menuInflater.inflate(R.menu.mypage_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.MyPage ->{
                Toast.makeText(applicationContext, "마이페이지", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
            }
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }
}