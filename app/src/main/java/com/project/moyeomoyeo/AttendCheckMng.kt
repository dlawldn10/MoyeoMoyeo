package com.project.moyeomoyeo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AttendCheckMng : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attend_check_mng)

        val backBtn = findViewById<Button>(R.id.BackBtn)

        backBtn.setOnClickListener {
            finish()
        }
    }
}