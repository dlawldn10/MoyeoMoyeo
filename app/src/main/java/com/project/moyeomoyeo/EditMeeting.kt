package com.project.moyeomoyeo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class EditMeeting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_meeting)

        val cancelBtn = findViewById<Button>(R.id.CancelBtn)

        cancelBtn.setOnClickListener {
            finish()
        }
    }
}