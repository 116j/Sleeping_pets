package com.example.sleepingpets

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MusicActivity : AppCompatActivity() {
    val REQ_CODE_PICK_SOUNDFILE = 1000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
    }

    fun chooseAlarmMusic() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "audio/mpeg"
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Choose music for your alarm"
            ), REQ_CODE_PICK_SOUNDFILE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_PICK_SOUNDFILE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                user?.alarmMusic = data.data.toString()
            }
        }
    }
}