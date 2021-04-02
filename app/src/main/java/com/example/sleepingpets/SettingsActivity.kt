package com.example.sleepingpets

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.observe
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.db_models.User
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.vk.api.sdk.VK
import java.io.File


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViewById<ConstraintLayout>(R.id.settings_layout).rootView.setBackgroundResource(R.drawable.standard)
        val back = findViewById<View>(R.id.settingsBack)
        back.setOnClickListener {
            finish()
        }

        val change = findViewById<Button>(R.id.changePicture)
        change.setOnClickListener {

        }
        val name = findViewById<EditText>(R.id.settingsName)
        var users:List<User> =listOf()
        SleepingPetsDatabase.getInstance(this).databaseDao.getUsers().observe(this){
            users=it
        }
        name.setText(user?.login)
        val settingsNameError = findViewById<TextView>(R.id.settingsNameError)
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != user?.login) {
                    when {
                        s?.length!! < 3 -> settingsNameError.text =
                            "Name should have at least 3 symbols"
                        users.stream().anyMatch { u -> u.login == s } -> settingsNameError.text =
                            "This name is already taken"
                        else -> settingsNameError.text = ""
                    }
                } else if (settingsNameError.text.isNotEmpty())
                    settingsNameError.text = ""
            }

        })
        val email = findViewById<EditText>(R.id.settingsEmail)
        val settingsEmailError = findViewById<TextView>(R.id.settingsEmailError)
        email.setText(user?.email)
        val oldPsw = findViewById<EditText>(R.id.settingsOldPsw)
        val settingsOldPswError = findViewById<TextView>(R.id.settingsOldPswError)
        val newPsw = findViewById<EditText>(R.id.settingsNewPsw)
        val settingsNewPswError = findViewById<TextView>(R.id.settingsNewPswError)
        val done = findViewById<Button>(R.id.doneSettings)
        done.setOnClickListener {
            var isAlright = settingsNameError.text.isEmpty()
            if (email.text.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.text)
                    .matches()
            ) {
                settingsEmailError.text = ""
                isAlright = false
            } else
                settingsEmailError.text = ""
            if (oldPsw.text.toString() != user?.password) {
                settingsOldPswError.text = "Incorrect password"
                isAlright = false
            } else
                settingsOldPswError.text = ""
            if (newPsw.text.length > 5 && !newPsw.text.matches("[a-zA-Z0-9]*".toRegex())) {
                settingsNewPswError.text =
                    "Password should has at least 5 symbols and has only numbers and letters"
                isAlright = false
            } else
                settingsNewPswError.text = ""
            if (isAlright) {
                user?.login = name.text.toString()
                user?.password = newPsw.text.toString()
                user?.email = email.text.toString()
                // save user info
                finish()
            }
        }
        val logOut = findViewById<Button>(R.id.logOut)
        logOut.setOnClickListener {
            logOut()
        }
        val delete = findViewById<Button>(R.id.delete_profile)
        delete.setOnClickListener {
            user?.let { it1 -> SleepingPetsDatabase.getInstance(this).databaseDao.deleteUser(it1) }
            logOut()
        }
    }

    fun logOut() {
        when (user?.authType) {
            "F" -> {
                if (AccessToken.getCurrentAccessToken() != null) {
                    GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/permissions/",
                        null,
                        HttpMethod.DELETE,
                        GraphRequest.Callback {
                            LoginManager.getInstance().logOut()
                        }).executeAsync()
                }
            }
            "V" -> VK.logout()
        }
        user = null
        File(filesDir.path + "id.bin").delete()
        val intent = Intent(this, LogInStartActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }
}