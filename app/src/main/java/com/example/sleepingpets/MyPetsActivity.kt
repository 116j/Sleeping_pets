package com.example.sleepingpets

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.sleepingpets.adapters.GridAdapter
import com.example.sleepingpets.adapters.PetsAdapter
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.SleepingPetsService
import com.example.sleepingpets.models.db_models.Pet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView

class MyPetsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var navigationView: NavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets)
        val toolbar: ImageView = findViewById<View>(R.id.menuButton) as ImageView
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.rootView.setBackgroundResource(R.drawable.standard)
        toolbar.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }


        val progress = findViewById<ProgressBar>(R.id.progress_my_pets)
        progress.visibility = View.VISIBLE
        SleepingPetsService.updateUserPets(user!!.id)
        val pets: List<Pet> =
            SleepingPetsDatabase.getInstance(this).databaseDao.getUserPets(user!!.id)
        progress.visibility = View.INVISIBLE
        val carouselView = findViewById<ViewPager2>(R.id.my_pets_carousel_view)
        carouselView.adapter = PetsAdapter(pets,R.layout.my_pets_item)

        findViewById<ImageView>(R.id.my_pets_left).setOnClickListener {
            if(carouselView.currentItem>0)
                carouselView.currentItem--
        }

        findViewById<ImageView>(R.id.my_pets_right).setOnClickListener {
            if(carouselView.currentItem<pets.size-1)
                carouselView.currentItem++
        }

        findViewById<ImageView>(R.id.plus).setOnClickListener {
            val intent = Intent(this, BuyPetsActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.coinsText).text= user?.balance.toString()
        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView!!.setNavigationItemSelectedListener(this)
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
            R.id.nav_alarm -> {
                val intent = Intent(this, AlarmActivity::class.java)
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