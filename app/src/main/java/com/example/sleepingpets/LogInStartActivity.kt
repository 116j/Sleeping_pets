package com.example.sleepingpets

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.security.MessageDigest

class LogInStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        findViewById<LinearLayout>(R.id.login_start_layout).rootView.setBackgroundResource(R.drawable.standard)

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("Auth", "signUp")
            startActivity(intent)
        }

        findViewById<Button>(R.id.loginInButton).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("Auth", "logIn")
            startActivity(intent)
        }
    }

}