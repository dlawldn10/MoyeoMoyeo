package com.project.moyeomoyeo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class MyPageActivity : AppCompatActivity() {
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

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            //뒤로가기
            android.R.id.home->
                finish()
        }
        return super.onOptionsItemSelected(item)

    }
}