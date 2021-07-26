package com.project.moyeomoyeo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MyMeetingMng : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_meeting_mng)

        val manageBtn = findViewById<Button>(R.id.ManageBtn)
        val attendBtn = findViewById<Button>(R.id.AttendCheckBtn)
        val optionBtn = findViewById<Button>(R.id.OptionBtn)
        val backBtn = findViewById<Button>(R.id.BackBtn)

        manageBtn.setOnClickListener {
            val nextIntent = Intent(this,ManageMember::class.java)
            startActivity(nextIntent)
        }

        attendBtn.setOnClickListener {
            val nextIntent = Intent(this,AttendCheckMng::class.java)
            startActivity(nextIntent)
        }

        optionBtn.setOnClickListener {
            val nextIntent = Intent(this,EditMeeting::class.java)
            startActivity(nextIntent)
        }

        backBtn.setOnClickListener {
            val nextIntent = Intent(this,CreateMeeting::class.java)
            startActivity(nextIntent)
        }
    }
}