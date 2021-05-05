package com.example.sleepingpets

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.sleepingpets.adapters.WeekStatisticAdapter
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.net.URL

class MyPageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var navigationView: NavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)
        val toolbar: ImageView = findViewById<View>(R.id.menuButton) as ImageView
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.rootView.setBackgroundResource(R.drawable.standard)
        toolbar.setOnClickListener{
            drawer.openDrawer(Gravity.LEFT)
        }

        val settings = findViewById<ImageView>(R.id.settingsButton)
        settings.setOnClickListener {
            val intent=Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }

        val image=findViewById<CircleImageView>(R.id.pageImage)
        if(user?.image!!.startsWith("@drawable")) {
            val id = resources
                .getIdentifier(user?.image!!.substring(10), "drawable", packageName)
            image.setImageDrawable(ContextCompat.getDrawable(this,id))
        }
        else
            image.setImageBitmap(getBitmapFromAssets(user?.image!!))
        val login=findViewById<TextView>(R.id.pageLogin)
        login.text= user?.login
        val progress=findViewById<ProgressBar>(R.id.progress_my_page)
        progress.visibility=View.VISIBLE
        val weeks= user?.id?.let {
            SleepingPetsDatabase.getInstance(this).databaseDao.getUserWeeks(it)
        }
        progress.visibility=View.INVISIBLE
        val statistic=findViewById<ViewPager2>(R.id.weekStatisticCW)
        statistic.adapter=WeekStatisticAdapter(weeks!!)
        val weekName=findViewById<TextView>(R.id.weekName)
        statistic.currentItem=weeks.size-1
        val prev=findViewById<ImageView>(R.id.prev_week)
        prev.setOnClickListener {
            if(statistic.currentItem>0) {
                statistic.currentItem--
            }
        }

        val next=findViewById<ImageView>(R.id.next_week)
        next.setOnClickListener {
            if(statistic.currentItem!=weeks.size-1)
                statistic.currentItem++
        }


        findViewById<TextView>(R.id.pagecoins).text= user?.balance.toString()
        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView!!.setNavigationItemSelectedListener(this)
    }
    private fun getBitmapFromAssets(fileName: String): Bitmap? {
        return try {
            BitmapFactory.decodeStream(URL(fileName).openStream())
        } catch (e: IOException) {
            e.printStackTrace()
            null
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
            R.id.nav_alarm -> {
                val intent = Intent(this, AlarmActivity::class.java)
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