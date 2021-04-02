package com.example.sleepingpets

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.observe
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.SleepingPetsService
import com.example.sleepingpets.models.db_models.User
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.time.ExperimentalTime

var user: User? = null

class AlarmActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var navigationView: NavigationView? = null

    lateinit var timer: CountDownTimer
    var secondsRemaining: Long = 0L
    var timerLength: Long = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val toolbar: ImageView = findViewById<View>(R.id.menuButton) as ImageView
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        toolbar.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }
        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView!!.setNavigationItemSelectedListener(this)

        findViewById<TextView>(R.id.gotobedTime).setOnClickListener { setBedTime() }

        findViewById<TextView>(R.id.wakeupTime).setOnClickListener { setWakeUpTime() }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setWakeUpTime() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            findViewById<TextView>(R.id.wakeupTime).text =
                SimpleDateFormat("HH:mm").format(cal.time)
            user?.wakeUpTime = cal.time.toString()
            timerLength = if ( Time.valueOf(user?.goToBedTime) < Time.valueOf(user?.wakeUpTime))
                24 * 60 * 60 * 1000 -  Time.valueOf(user?.goToBedTime ).time+  Time.valueOf(user?.wakeUpTime).time
            else
                Time.valueOf(user?.wakeUpTime).time -  Time.valueOf(user?.goToBedTime).time
            findViewById<TextView>(R.id.timerText).text =
                "${timerLength / (1000 * 60 * 60)}h${timerLength / (1000 * 60)}m"
        }
        TimePickerDialog(
            this,
            R.style.DialogTheme,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setBedTime() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            findViewById<TextView>(R.id.gotobedTime).text =
                SimpleDateFormat("HH:mm").format(cal.time)
            user?.goToBedTime = cal.time.toString()
            timerLength = if ( Time.valueOf(user?.goToBedTime) <  Time.valueOf(user!!.wakeUpTime))
                24 * 60 * 60 * 1000 -  Time.valueOf(user?.goToBedTime).time +  Time.valueOf(user?.wakeUpTime).time
            else
                Time.valueOf(user?.wakeUpTime).time -  Time.valueOf(user?.goToBedTime).time
            "${timerLength / (1000 * 60 * 60)}h${timerLength / (1000 * 60)}m"
            findViewById<TextView>(R.id.timerText).text =
                "${timerLength / (1000 * 60 * 60)}h${timerLength / (1000 * 60)}m"
        }
        TimePickerDialog(
            this,
            R.style.DialogTheme,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    @SuppressLint("SetTextI18n")
    @ExperimentalTime
    private fun startTimer() {
        timerLength = if (System.currentTimeMillis() <  Time.valueOf(user?.wakeUpTime).time)
            24 * 60 * 60 * 1000 - System.currentTimeMillis() +  Time.valueOf(user?.wakeUpTime).time
        else
            Time.valueOf(user?.wakeUpTime).time - System.currentTimeMillis()
        val textTimeR = findViewById<TextView>(R.id.timerText)
        textTimeR.text =
            "${timerLength / (1000 * 60 * 60)}h${timerLength / (1000 * 60)}m"

        timer = object : CountDownTimer(timerLength, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished
                updateCountdownUI()
            }
        }.start()
    }

    @ExperimentalTime
    private fun onTimerFinished() {
        val sleepTimer = findViewById<ProgressBar>(R.id.sleepTimer)
        sleepTimer.progress = 100
        secondsRemaining = 0
        updateCountdownUI()
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountdownUI() {
        val sleepTimer = findViewById<ProgressBar>(R.id.sleepTimer)
        val textTimeR = findViewById<TextView>(R.id.timerText)
        textTimeR.text =
            "${timerLength - secondsRemaining / (1000 * 60 * 60)}h${timerLength - secondsRemaining / (1000 * 60)}m"
        sleepTimer.progress = (timerLength - secondsRemaining).toInt()
    }

    @ExperimentalTime
    override fun onStart() {
        super.onStart()
        SleepingPetsService.updateDatabase()
        if (user == null) {
            if (File(filesDir.path + "id.bin").exists()) {
                val fis: FileInputStream = openFileInput("id.bin")
                val `is` = ObjectInputStream(fis)
                val id = `is`.readObject() as Int
                //find user
                SleepingPetsDatabase.getInstance(this).databaseDao.getUser(id).observe(this){
                    user=it
                }
                `is`.close()
                fis.close()
                if (user == null) {
                    File(filesDir.path + "id.bin").delete()
                    val intent = Intent(this, LogInStartActivity::class.java)
                    startActivity(intent)
                    finish()
                    return
                }
            } else {
                val intent = Intent(this, LogInStartActivity::class.java)
                startActivity(intent)
                finish()
                return
            }
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val sleepButton = findViewById<Button>(R.id.sleepButton)

        if (abs(System.currentTimeMillis() -  Time.valueOf(user!!.goToBedTime).time) <= Time.valueOf("01:00:00").time) {
            sleepButton.isClickable = true
            sleepButton.setOnClickListener { startTimer() }
            drawer.rootView.setBackgroundResource(R.drawable.alarm_night)
        } else if (abs(System.currentTimeMillis() -  Time.valueOf(user!!.wakeUpTime).time) <= Time.valueOf("01:00:00").time) {
            sleepButton.isClickable = true
            sleepButton.setOnClickListener {
                timer.cancel()
                onTimerFinished()
            }
            drawer.rootView.setBackgroundResource(R.drawable.alarm_day)
        } else if (System.currentTimeMillis() >  Time.valueOf(user!!.goToBedTime).time + Time.valueOf("01:00:00").time || System.currentTimeMillis() <  Time.valueOf(user!!.wakeUpTime).time + Time.valueOf(
                "01:00:00"
            ).time
        ) {
            drawer.rootView.setBackgroundResource(R.drawable.alarm_night)
            sleepButton.isClickable = false
        } else if (System.currentTimeMillis() <  Time.valueOf(user!!.goToBedTime).time + Time.valueOf("01:00:00").time || System.currentTimeMillis() >  Time.valueOf(user!!.wakeUpTime).time + Time.valueOf(
                "01:00:00"
            ).time
        ) {
            drawer.rootView.setBackgroundResource(R.drawable.alarm_day)
            sleepButton.isClickable = false
        }
    }

    @ExperimentalTime
    override fun onRestart() {
        super.onRestart()
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val sleepButton = findViewById<Button>(R.id.sleepButton)

        if (abs(System.currentTimeMillis() -  Time.valueOf(user!!.goToBedTime).time) <= Time.valueOf("01:00:00").time) {
            sleepButton.isClickable = true
            sleepButton.setOnClickListener { startTimer() }
            drawer.rootView.setBackgroundResource(R.drawable.alarm_night)
        } else if (abs(System.currentTimeMillis() -  Time.valueOf(user!!.wakeUpTime).time) <= Time.valueOf("01:00:00").time) {
            sleepButton.isClickable = true
            sleepButton.setOnClickListener {
                timer.cancel()
                onTimerFinished()
            }
            drawer.rootView.setBackgroundResource(R.drawable.alarm_day)
        } else if (System.currentTimeMillis() >  Time.valueOf(user!!.goToBedTime).time + Time.valueOf("01:00:00").time || System.currentTimeMillis() <  Time.valueOf(user!!.wakeUpTime).time + Time.valueOf(
                "01:00:00"
            ).time
        ) {
            drawer.rootView.setBackgroundResource(R.drawable.alarm_night)
            sleepButton.isClickable = false
        } else if (System.currentTimeMillis() <  Time.valueOf(user!!.goToBedTime).time + Time.valueOf("01:00:00").time || System.currentTimeMillis() >  Time.valueOf(user!!.wakeUpTime).time + Time.valueOf(
                "01:00:00"
            ).time
        ) {
            drawer.rootView.setBackgroundResource(R.drawable.alarm_day)
            sleepButton.isClickable = false
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_page -> {
                val intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.nav_my_pets -> {
                val intent = Intent(this, MyPetsActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.nav_rating -> {
                val intent = Intent(this, RatingActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.nav_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.nav_suggestions -> {
                val intent = Intent(this, SuggestionsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(Gravity.LEFT)
        return true
    }
}

