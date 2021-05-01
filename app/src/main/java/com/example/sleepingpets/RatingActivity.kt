package com.example.sleepingpets

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.observe
import com.example.sleepingpets.adapters.RatingListAdapter
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.SleepingPetsService
import com.example.sleepingpets.models.db_models.User
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.net.URL

class RatingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var navigationView: NavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)
        val toolbar: ImageView = findViewById<View>(R.id.menuButton) as ImageView
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.rootView.setBackgroundResource(R.drawable.standard)
        toolbar.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }
        val progress=findViewById<ProgressBar>(R.id.progress_rating)
        progress.visibility=View.VISIBLE
        SleepingPetsService.updateUsers()
        val users:List<User> =  SleepingPetsDatabase.getInstance(this).databaseDao.getUsers()
        progress.visibility=View.INVISIBLE
        val rating = findViewById<ListView>(R.id.rating_list)
        val adapter = RatingListAdapter(this, users)
        rating.adapter = adapter
        rating.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, RatingActivity::class.java)
            intent.putExtra("user", users[position])
            intent.putExtra("position", position)
            startActivity(intent)
        }

        val image = findViewById<CircleImageView>(R.id.rating_my_image)
        val number = findViewById<TextView>(R.id.rating_my_number)
        val name = findViewById<TextView>(R.id.rating_my_name)
        val petScore = findViewById<TextView>(R.id.rating_my_pets_score)
        val sleepScore = findViewById<TextView>(R.id.rating_my_sleep_percent)

        image.setImageBitmap(getBitmapFromAssets(user!!.image))
        number.text = (users.indexOf(user!!) + 1).toString()
        name.text = user!!.login
        petScore.text = user!!.petScore.toString()
        sleepScore.text = user!!.sleepScore.toString()

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
            R.id.nav_alarm -> {
                val intent = Intent(this, AlarmActivity::class.java)
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