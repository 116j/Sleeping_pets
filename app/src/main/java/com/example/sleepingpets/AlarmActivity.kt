package com.example.sleepingpets

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
var alarm: AlarmActivity? = null

class AlarmActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var navigationView: NavigationView? = null

    lateinit var timer: CountDownTimer
    var secondsRemaining: Long = 0L
    var timerLength: Long = 0L

    var manager: NotificationManagerCompat? = null
    var player: MediaPlayer? = null

    val REQ_CODE_PICK_SLEEP = 1000;
    val REQ_CODE_PICK_ALARM = 2000;
    val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "alarm"
    var isDay=false

    val durationList = listOf("10 min", "30 min", "1 hour", "2 hours")

    @ExperimentalTime
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        update()

        val menu: ImageView = findViewById<View>(R.id.menuButton) as ImageView
        findViewById<TextView>(R.id.wakeupTime).text =
            SimpleDateFormat("HH:mm").format(Time.valueOf(user?.wakeUpTime))
        findViewById<TextView>(R.id.gotobedTime).text =
            SimpleDateFormat("HH:mm").format(Time.valueOf(user?.goToBedTime))
        timerLength = if (Time.valueOf(user?.goToBedTime) > Time.valueOf(user?.wakeUpTime))
            24 * 60 * 60 * 1000 - Time.valueOf(user?.goToBedTime).time + Time.valueOf(user?.wakeUpTime).time
        else
            Time.valueOf(user?.wakeUpTime).time - Time.valueOf(user?.goToBedTime).time
        findViewById<TextView>(R.id.timerText).text =
            "${timerLength / (1000 * 60 * 60) % 24}h${timerLength / (1000 * 60) % 60}m"
        val sleepTimer = findViewById<ProgressBar>(R.id.sleepTimer)
        sleepTimer.max = timerLength.toInt()
        sleepTimer.progress = timerLength.toInt()
        val sleepButton = findViewById<Button>(R.id.sleepButton)
        sleepButton.setOnClickListener {
            startTimer()
        }

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
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED;
                }
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
        pets.toMutableList().add(Pet(name = "", type = "", obj = "", userId = 0))
        progress.visibility = View.INVISIBLE
        val grid = findViewById<GridView>(R.id.bottom_grid)
        val gridadapter = GridAdapter(this, pets)
        grid.adapter = gridadapter
        grid.setOnItemClickListener { parent, view, position, id ->

        }
        val musicBtn = findViewById<ImageView>(R.id.musicButton)
        musicBtn.setOnClickListener { showMusicWindow() }

        findViewById<TextView>(R.id.alarmcoins).text = user?.balance.toString()
    }

    @ExperimentalTime
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setWakeUpTime() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            findViewById<TextView>(R.id.wakeupTime).text =
                SimpleDateFormat("HH:mm").format(cal.time)
            user?.wakeUpTime = SimpleDateFormat("HH:mm:ss").format(cal.time)
            timerLength = if (Time.valueOf(user?.goToBedTime) > Time.valueOf(user?.wakeUpTime))
                24 * 60 * 60 * 1000 - Time.valueOf(user?.goToBedTime).time + Time.valueOf(user?.wakeUpTime).time
            else
                Time.valueOf(user?.wakeUpTime).time - Time.valueOf(user?.goToBedTime).time
            findViewById<TextView>(R.id.timerText).text =
                "${timerLength / (1000 * 60 * 60) % 24}h${timerLength / (1000 * 60) % 60}m"

            if (abs(System.currentTimeMillis() - Time.valueOf(user!!.wakeUpTime).time) <= Time.valueOf(
                    "01:00:00"
                ).time
            ) {
                val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
                drawer.rootView.setBackgroundResource(R.drawable.alarm_day)
            }
            SleepingPetsService.updateUser(user!!, this)
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

    @ExperimentalTime
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setBedTime() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            findViewById<TextView>(R.id.gotobedTime).text =
                SimpleDateFormat("HH:mm").format(cal.time)
            user?.goToBedTime = SimpleDateFormat("HH:mm:ss").format(cal.time)
            timerLength = if (Time.valueOf(user?.goToBedTime) > Time.valueOf(user!!.wakeUpTime))
                24 * 60 * 60 * 1000 - Time.valueOf(user?.goToBedTime).time + Time.valueOf(user?.wakeUpTime).time
            else
                Time.valueOf(user?.wakeUpTime).time - Time.valueOf(user?.goToBedTime).time
            findViewById<TextView>(R.id.timerText).text =
                "${timerLength / (1000 * 60 * 60) % 24}h${timerLength / (1000 * 60) % 60}m"
            if (abs(System.currentTimeMillis() - Time.valueOf(user!!.goToBedTime).time) <= Time.valueOf(
                    "01:00:00"
                ).time
            ) {
                val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
                val sleepButton = findViewById<Button>(R.id.sleepButton)
                // sleepButton.isClickable = true
                drawer.rootView.setBackgroundResource(R.drawable.alarm_night)
                sleepButton.setOnClickListener {
                    startTimer()
                }
            }
            SleepingPetsService.updateUser(user!!, this)
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

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    @ExperimentalTime
    private fun startTimer() {
        val sleepButton = findViewById<Button>(R.id.sleepButton)
        sleepButton.text = "wake up"
        sleepButton.setOnClickListener {
            cancelTimer()
        }

        timerLength =
            if (Time.valueOf(SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().timeInMillis)).time > Time.valueOf(
                    user?.wakeUpTime
                ).time
            )
                24 * 60 * 60 * 1000 - Time.valueOf(SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().timeInMillis)).time + Time.valueOf(
                    user?.wakeUpTime
                ).time
            else
                Time.valueOf(user?.wakeUpTime).time - Time.valueOf(
                    SimpleDateFormat("HH:mm:ss").format(
                        Calendar.getInstance().timeInMillis
                    )
                ).time
        val sleepTimer = findViewById<ProgressBar>(R.id.sleepTimer)
        sleepTimer.max = timerLength.toInt()
        sleepTimer.progress = timerLength.toInt()

        timer = object : CountDownTimer(timerLength, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished
                updateCountdownUI()
            }
        }.start()
        Toast.makeText(
            this,
            "Don't close the app, otherwise you will disable the alarm",
            Toast.LENGTH_SHORT
        ).show()
    }

    @SuppressLint("SetTextI18n")
    @ExperimentalTime
    public fun cancelTimer() {
        val sleepButton = findViewById<Button>(R.id.sleepButton)
        sleepButton.text = "sleep"
        sleepButton.setOnClickListener {
            startTimer()
        }
        timer.cancel()
        player?.stop()
        val sleepTimer = findViewById<ProgressBar>(R.id.sleepTimer)
        timerLength = if (Time.valueOf(user?.goToBedTime) > Time.valueOf(user?.wakeUpTime))
            24 * 60 * 60 * 1000 - Time.valueOf(user?.goToBedTime).time + Time.valueOf(user?.wakeUpTime).time
        else
            Time.valueOf(user?.wakeUpTime).time - Time.valueOf(user?.goToBedTime).time
        sleepTimer.max = timerLength.toInt()
        sleepTimer.progress = timerLength.toInt()
        secondsRemaining = 0
        val textTime = findViewById<TextView>(R.id.timerText)
        textTime.text =
            "${timerLength / (1000 * 60 * 60) % 24}h${timerLength / (1000 * 60) % 60}m"
    }

    @ExperimentalTime
    private fun onTimerFinished() {
        secondsRemaining = 0
        updateCountdownUI()
        player = MediaPlayer()
        if (File(user?.alarmMusic).exists())
            player?.setDataSource(this, Uri.fromFile(File(user?.alarmMusic)))
        else
            player?.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager;
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            0
        )
        player?.isLooping = true
        player?.prepare();
        player?.start();


        showNotification()
    }

    @SuppressLint("SimpleDateFormat")
    private fun showNotification() {
        createNotificationChannel()

        //start this(MainActivity) on by Tapping notification
        val alarmIntent = Intent(this, AlarmActivity::class.java)
        //alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val alarmPIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, alarmIntent, 0)
        val cancelIntent: Intent = Intent(this, CancelReceiver::class.java)
        alarm = this
        val cancelPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
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
            .setCategory(NotificationCompat.CATEGORY_ALARM)
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
        manager?.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        val name: CharSequence = "Alarm notification"
        val description = "Notification of alarm"
        //importance of your notification
        val importance: Int = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel =
            NotificationChannel(CHANNEL_ID, name, importance)
        notificationChannel.description = description
        notificationChannel.setSound(null, null)
        notificationChannel.enableVibration(true)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountdownUI() {
        val sleepTimer = findViewById<ProgressBar>(R.id.sleepTimer)
        val textTime = findViewById<TextView>(R.id.timerText)
        textTime.text =
            "${secondsRemaining / (1000 * 60 * 60) % 24}h${secondsRemaining / (1000 * 60) % 60}m"
        sleepTimer.progress = (secondsRemaining).toInt()
    }

    @ExperimentalTime
    override fun onStart() {
        super.onStart()
        update()
    }

    @ExperimentalTime
    override fun onRestart() {
        super.onRestart()
        update()
    }

    @SuppressLint("SimpleDateFormat")
    @ExperimentalTime
    fun update() {
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

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val sleepButton = findViewById<Button>(R.id.sleepButton)

        if (abs(
                Time.valueOf(SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().timeInMillis)).time - Time.valueOf(
                    user!!.goToBedTime
                ).time
            ) <= Time.valueOf(
                "00:30:00"
            ).time
        ) {
            isDay=false
            drawer.rootView.setBackgroundResource(R.drawable.alarm_night)
        } else if (abs(
                Time.valueOf(SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().timeInMillis)).time - Time.valueOf(
                    user!!.wakeUpTime
                ).time
            ) <= Time.valueOf(
                "00:30:00"
            ).time
        ) {
            isDay=true
            drawer.rootView.setBackgroundResource(R.drawable.alarm_day)
        } else if (Time.valueOf(SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().timeInMillis)).time > Time.valueOf(
                user!!.goToBedTime
            ).time + Time.valueOf(
                "00:30:00"
            ).time && Time.valueOf(SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().timeInMillis)).time < Time.valueOf(
                user!!.wakeUpTime
            ).time + Time.valueOf(
                "00:30:00"
            ).time
        ) {
            isDay=false
            drawer.rootView.setBackgroundResource(R.drawable.alarm_night)
            sleepButton.isClickable = false
        } else {
            isDay=true
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

    @SuppressLint("SetTextI18n")
    fun showMusicWindow() {
        val popupView: View = View.inflate(this, R.layout.music_layout, null)
        val window = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            false
        )
       val view=window.contentView
        if(isDay)
        view.setBackgroundResource(R.drawable.music_background_day)
        view.findViewById<LinearLayout>(R.id.pickAlarm).setOnClickListener {
            chooseMusic(REQ_CODE_PICK_ALARM, "Choose music for your alarm")
        }
        view.findViewById<LinearLayout>(R.id.pickSleep).setOnClickListener {
            chooseMusic(REQ_CODE_PICK_SLEEP, "Choose music before sleep")
        }
        val alarmText = view.findViewById<TextView>(R.id.alarmMusicText)
        if (File(user?.alarmMusic).exists())
            alarmText.text = user?.alarmMusic
        else
            alarmText.text = "Default alarm"
        val sleepText = view.findViewById<TextView>(R.id.sleepMusicText)
        if (File(user?.sleepMusic).exists())
            sleepText.text = user?.alarmMusic
        else
            sleepText.text = "None"

        view.findViewById<ImageView>(R.id.closeMusic).setOnClickListener {
            window.dismiss()
        }

        val picker = view.findViewById<Spinner>(R.id.sleepMusicDuration)
        val adapter = ArrayAdapter<CharSequence>(
            this, android.R.layout.simple_spinner_dropdown_item,
            durationList
        )
        picker.adapter = adapter
        picker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setDuration(position)
                SleepingPetsService.updateUser(user!!,context = this@AlarmActivity)
            }
        }
        picker.setSelection(getUserDuration())

        window.showAtLocation(findViewById(R.id.alarm_layout), Gravity.CENTER, 0, 0)
    }

    fun setDuration(position: Int) {
        when (position) {
            0 -> user?.sleepDuration = Time.valueOf("00:10:00").time
            1 -> user?.sleepDuration = Time.valueOf("00:30:00").time
            2 -> user?.sleepDuration = Time.valueOf("01:00:00").time
            3 -> user?.sleepDuration = Time.valueOf("02:00:00").time
        }
    }

    private fun getUserDuration(): Int {
        return when (user?.sleepDuration) {
            Time.valueOf("00:10:00").time -> 0
            Time.valueOf("00:30:00").time -> 1
            Time.valueOf("01:00:00").time -> 2
            Time.valueOf("02:00:00").time -> 3
            else -> 0
        }
    }

    private fun chooseMusic(request: Int, title: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "audio/mpeg"
        startActivityForResult(
            Intent.createChooser(
                intent,
                title
            ), request
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_PICK_ALARM && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                user?.alarmMusic = data.data.toString()
                findViewById<TextView>(R.id.alarmMusicText).text = user?.alarmMusic
                SleepingPetsService.updateUser(user!!,context = this@AlarmActivity)
            }
        } else if (requestCode == REQ_CODE_PICK_SLEEP && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                user?.sleepMusic = data.data.toString()
                findViewById<TextView>(R.id.sleepMusicText).text = user?.sleepMusic
                SleepingPetsService.updateUser(user!!,context = this@AlarmActivity)
            }
        }
    }


    class CancelReceiver : BroadcastReceiver() {
        @ExperimentalTime
        override fun onReceive(context: Context?, intent: Intent?) {
            alarm?.cancelTimer()
            if (context != null) {
                NotificationManagerCompat.from(context.applicationContext).cancel(1)
            }

        }

    }
}

