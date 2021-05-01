package com.example.sleepingpets

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.sleepingpets.adapters.GridAdapter
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.SleepingPetsService
import com.example.sleepingpets.models.db_models.Pet
import com.example.sleepingpets.models.db_models.User
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    val NOTIFICATION_ID = 1
    lateinit var manager: NotificationManagerCompat
    lateinit var player: Ringtone


    private val CHANNEL_ID = "alarm"


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        if (user == null) {
            if (File(filesDir.path + "/id.bin").exists()) {
                val fis: FileInputStream = FileInputStream(filesDir.path + "/id.bin")
                val `is` = ObjectInputStream(fis)
                val id = `is`.readObject() as Int
                //find user
                user = SleepingPetsDatabase.getInstance(this).databaseDao.getUser(id)
                `is`.close()
                fis.close()
                if (user == null) {
                    File(filesDir.path + "/id.bin").delete()
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

        val menu: ImageView = findViewById<View>(R.id.menuButton) as ImageView
        findViewById<TextView>(R.id.wakeupTime).text =
            SimpleDateFormat("HH:mm").format(Time.valueOf(user?.wakeUpTime))
        findViewById<TextView>(R.id.gotobedTime).text =
            SimpleDateFormat("HH:mm").format(Time.valueOf(user?.goToBedTime))
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        menu.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }
        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView!!.setNavigationItemSelectedListener(this)

        findViewById<TextView>(R.id.gotobedTime).setOnClickListener { setBedTime() }

        findViewById<TextView>(R.id.wakeupTime).setOnClickListener { setWakeUpTime() }

        val bottomMenu = findViewById<LinearLayout>(R.id.bottom_menu)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomMenu)
        val closeMenu = findViewById<ImageView>(R.id.open_bottom_menu)
        closeMenu.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        closeMenu.setImageDrawable(resources.getDrawable(R.mipmap.menu_arrow_up))
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        closeMenu.setImageDrawable(resources.getDrawable(R.mipmap.menu_arrow_down))
                    }
                }
            }
        })

        val menuName = findViewById<TextView>(R.id.bottom_menu_name)
        menuName.text = "Choose pet to sleep"
        val progress = findViewById<ProgressBar>(R.id.progress_alarm)
        progress.visibility = View.VISIBLE
        SleepingPetsService.updateUserPets(user!!.id)
        val pets: List<Pet> =
            SleepingPetsDatabase.getInstance(this).databaseDao.getUserPets(user!!.id)
        progress.visibility = View.INVISIBLE
        val grid = findViewById<GridView>(R.id.bottom_grid)
        val gridadapter = GridAdapter(this, pets, false)
        grid.adapter = gridadapter
        grid.setOnItemClickListener { parent, view, position, id ->

        }
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
            timerLength = if (Time.valueOf(user?.goToBedTime) < Time.valueOf(user?.wakeUpTime))
                24 * 60 * 60 * 1000 - Time.valueOf(user?.goToBedTime).time + Time.valueOf(user?.wakeUpTime).time
            else
                Time.valueOf(user?.wakeUpTime).time - Time.valueOf(user?.goToBedTime).time
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
            timerLength = if (Time.valueOf(user?.goToBedTime) < Time.valueOf(user!!.wakeUpTime))
                24 * 60 * 60 * 1000 - Time.valueOf(user?.goToBedTime).time + Time.valueOf(user?.wakeUpTime).time
            else
                Time.valueOf(user?.wakeUpTime).time - Time.valueOf(user?.goToBedTime).time
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
        timerLength = if (System.currentTimeMillis() < Time.valueOf(user?.wakeUpTime).time)
            24 * 60 * 60 * 1000 - System.currentTimeMillis() + Time.valueOf(user?.wakeUpTime).time
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

        val alarmTone: Uri = Uri.fromFile(File(user?.alarmMusic))
        player =
            RingtoneManager.getRingtone(applicationContext, alarmTone)
        player.streamType = AudioManager.STREAM_ALARM
        //player.isLooping = true
        player.play()
        showNotification()
    }

    @SuppressLint("SimpleDateFormat")
    private fun showNotification() {
        createNotificationChannel()

        //start this(MainActivity) on by Tapping notification
        val alarmIntent = Intent(this, AlarmActivity::class.java)
        alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val alarmPIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT)
        val cancelIntent: Intent = Intent(this, CancelReceiver::class.java)
        cancelIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val cancelPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            cancelIntent,
            PendingIntent.FLAG_ONE_SHOT
        )


        //creating notification
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            //icon
            .setSmallIcon(R.mipmap.alarm)
            //title
            .setContentTitle("Alarm: ${SimpleDateFormat("HH:mm").format(Time.valueOf(user?.wakeUpTime))}")
            //description
            .setContentText("You can cancel the alarm")
            //set priority
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            //dismiss on tap
            .setAutoCancel(true)
            //start intent on notification tap (MainActivity)
            .setContentIntent(alarmPIntent)

            //add action buttons to notification
            //icons will not displayed on Android 7 and above
            .addAction(R.mipmap.alarm, "Cancel", cancelPendingIntent)

        //notification manager
        manager =
            NotificationManagerCompat.from(this)
        manager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        val name: CharSequence = "Alarm notification"
        val description = "Notification of alarm"
        //importance of your notification
        val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel =
            NotificationChannel(CHANNEL_ID, name, importance)
        notificationChannel.description = description
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
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
    override fun onRestart() {
        super.onRestart()
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val sleepButton = findViewById<Button>(R.id.sleepButton)

        if (abs(System.currentTimeMillis() - Time.valueOf(user!!.goToBedTime).time) <= Time.valueOf(
                "01:00:00"
            ).time
        ) {
            sleepButton.isClickable = true
            sleepButton.setOnClickListener { startTimer() }
            drawer.rootView.setBackgroundResource(R.drawable.alarm_night)
        } else if (abs(System.currentTimeMillis() - Time.valueOf(user!!.wakeUpTime).time) <= Time.valueOf(
                "01:00:00"
            ).time
        ) {
            sleepButton.isClickable = true
            sleepButton.setOnClickListener {
                timer.cancel()
                onTimerFinished()
            }
            drawer.rootView.setBackgroundResource(R.drawable.alarm_day)
        } else if (System.currentTimeMillis() > Time.valueOf(user!!.goToBedTime).time + Time.valueOf(
                "01:00:00"
            ).time || System.currentTimeMillis() < Time.valueOf(user!!.wakeUpTime).time + Time.valueOf(
                "01:00:00"
            ).time
        ) {
            drawer.rootView.setBackgroundResource(R.drawable.alarm_night)
            sleepButton.isClickable = false
        } else if (System.currentTimeMillis() < Time.valueOf(user!!.goToBedTime).time + Time.valueOf(
                "01:00:00"
            ).time || System.currentTimeMillis() > Time.valueOf(user!!.wakeUpTime).time + Time.valueOf(
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
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        return true
    }


    inner class CancelReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //dismiss notification on Dislike button click
            player.stop()
            //dismiss notification on Dislike button click
            manager.cancel(NOTIFICATION_ID)
        }

    }
}

