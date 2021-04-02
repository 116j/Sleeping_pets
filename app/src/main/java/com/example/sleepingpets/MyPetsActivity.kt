package com.example.sleepingpets

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.observe
import com.example.sleepingpets.adapters.GridAdapter
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.SleepingPetsService
import com.example.sleepingpets.models.db_models.Pet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ViewListener

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

        val menuName=findViewById<TextView>(R.id.bottom_menu_name)
        menuName.text="My pets"

        SleepingPetsService.updateUserPets(user!!.id)
        var pets:List<Pet> = listOf()
        SleepingPetsDatabase.getInstance(this).databaseDao.getUserPets(user!!.id).observe(this){
            pets=it
        }
        pets.toMutableList().add(Pet(userId = user!!.id, name = "", type = ""))
        val grid = findViewById<GridView>(R.id.bottom_grid)
        val adapter = GridAdapter(this, pets, true)
        grid.adapter = adapter
        grid.setOnItemClickListener { parent, view, position, id ->
            if (position == pets.size - 1) {
                val intent = Intent(this, BuyPetsActivity::class.java)
                startActivity(intent)
            } else {
                //chose for sleep
            }
        }

        val carouselView = findViewById<CarouselView>(R.id.my_pets_carousel_view)
        carouselView.pageCount = pets.size
        carouselView.setViewListener(object : ViewListener {
            override fun setViewForPosition(position: Int): View {
                val view = layoutInflater.inflate(R.layout.my_carousel_view_item, null)

                return  view
            }
        })

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