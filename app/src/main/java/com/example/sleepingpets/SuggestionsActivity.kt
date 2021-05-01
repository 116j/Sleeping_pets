package com.example.sleepingpets

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.observe
import com.example.sleepingpets.adapters.RatingListAdapter
import com.example.sleepingpets.adapters.SAdapter
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.SleepingPetsService
import com.example.sleepingpets.models.db_models.Suggestion
import com.example.sleepingpets.models.db_models.User
import com.google.android.material.navigation.NavigationView

class SuggestionsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var navigationView: NavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestions)
        val toolbar: ImageView = findViewById<View>(R.id.menuButton) as ImageView
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.rootView.setBackgroundResource(R.drawable.standard)
        toolbar.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }
        SleepingPetsService.updateUserSuggestions(user!!.id)
        var suggestions:List<Suggestion> = listOf()
           suggestions= SleepingPetsDatabase.getInstance(this).databaseDao.getUserSuggestions(user!!.id)
        val users = List<User>(suggestions.size) { pos ->
            var user:User=User(authType = "",login = "",password = "")
           user= SleepingPetsDatabase.getInstance(this).databaseDao.getUser(suggestions.elementAt(pos).userId)
            user
        }

        val list = findViewById<ListView>(R.id.suggestions_list)
        val adapter = SAdapter(this, users)
        list.adapter = adapter
        list.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, SuggestionActivity::class.java)
            intent.putExtra("suggestion", suggestions[position])
            startActivity(intent)
        }
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
            R.id.nav_alarm -> {
                val intent = Intent(this, AlarmActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(Gravity.LEFT)
        return true
    }
}