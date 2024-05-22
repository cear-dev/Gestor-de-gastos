package com.example.tdmpa_511_3p_ex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonGoToAppointmentLayout = findViewById<Button>(R.id.button_goToAppointmentLayout)
        val buttonGoToDiaryLayout = findViewById<Button>(R.id.button_goToDiaryLayout)

        buttonGoToAppointmentLayout.setOnClickListener {
            val intent = Intent(this, activity_appointment::class.java)
            intent.putExtra("ModifyAppointmentFlag", false)
            startActivity(intent)
        }

        buttonGoToDiaryLayout.setOnClickListener {
            val intent = Intent(this, activity_diary::class.java)
            startActivity(intent)
        }
    }
}