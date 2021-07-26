package com.project.moyeomoyeo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AttendCheck : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attend_check)

        val backBtn = findViewById<Button>(R.id.BackBtn)
        val myPageBtn = findViewById<Button>(R.id.MyPageBtn)
        val qrBtn = findViewById<Button>(R.id.QRBtn)

        backBtn.setOnClickListener {
            finish()
        }
    }
}