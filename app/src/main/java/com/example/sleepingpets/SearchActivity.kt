package com.example.sleepingpets

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.drawerlayout.widget.DrawerLayout
import com.example.sleepingpets.adapters.SAdapter
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.db_models.User
import com.google.android.material.navigation.NavigationView
import java.util.*
import java.util.stream.Collectors

class SearchActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var navigationView: NavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val toolbar: ImageView = findViewById<View>(R.id.menuButton) as ImageView
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.rootView.setBackgroundResource(R.drawable.standard)
        toolbar.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }
        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView!!.setNavigationItemSelectedListener(this)
        val progress = findViewById<ProgressBar>(R.id.progress_search)
        progress.visibility = View.VISIBLE
        val users = SleepingPetsDatabase.getInstance(this).databaseDao.getUsers()
        progress.visibility = View.INVISIBLE
        var searchResult = listOf<User>()
        val searchList = findViewById<ListView>(R.id.search_list)
        val adapter = SAdapter(this, searchResult)
        searchList.adapter = adapter

        val search = findViewById<SearchView>(R.id.search_field)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //
                return false
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onQueryTextChange(newText: String?): Boolean {
                searchResult = users.stream().filter { u ->
                    u.login
                        .contains(newText!!,true)
                }.collect(Collectors.toList())
                adapter.notifyDataSetChanged()
                searchList.adapter = adapter
                return false
            }
        })

        searchList.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, SearchPageActivity::class.java)
            intent.putExtra("user", searchResult[position])
            startActivity(intent)
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
            R.id.nav_alarm -> {
                val intent = Intent(this, AlarmActivity::class.java)
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