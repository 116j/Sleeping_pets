package com.example.sleepingpets

import android.annotation.SuppressLint
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
import com.example.sleepingpets.models.SleepingPetsService
import com.example.sleepingpets.models.db_models.User
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.vk.api.sdk.VK
import java.io.File


class SettingsActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val layout=findViewById<ConstraintLayout>(R.id.settings_layout)
        layout.rootView.setBackgroundResource(R.drawable.standard)
        val back = findViewById<View>(R.id.settingsBack)
        back.setOnClickListener {
            finish()
        }

        val change = findViewById<Button>(R.id.changePicture)
        change.setOnClickListener {

        }
        if(user?.authType=="N") {
            layout.addView(View.inflate(this, R.layout.native_settings, null))
            val name = findViewById<EditText>(R.id.settingsName)
            val users:List<User> =SleepingPetsDatabase.getInstance(this).databaseDao.getUsers()
            name.setText(user?.login)
            val settingsNameError = findViewById<TextView>(R.id.settingsNameError)
            val email = findViewById<EditText>(R.id.settingsEmail)
            val settingsEmailError = findViewById<TextView>(R.id.settingsEmailError)
            email.setText(user?.email)
            val oldPsw = findViewById<EditText>(R.id.settingsOldPsw)
            val settingsOldPswError = findViewById<TextView>(R.id.settingsOldPswError)
            val newPsw = findViewById<EditText>(R.id.settingsNewPsw)
            val settingsNewPswError = findViewById<TextView>(R.id.settingsNewPswError)

            val done = findViewById<Button>(R.id.doneSettings)
            done.setOnClickListener {
                var isAlright = true
                when {
                    name.text.toString().length!! < 3 ->{
                        settingsNameError.text = "Name should have at least 3 symbols"
                        isAlright=false
                    }
                    users.stream().anyMatch { u -> u.login == name.text.toString() } -> {
                        settingsNameError.text = "This name is already taken"
                        isAlright=false
                    }
                    else -> settingsNameError.text = ""
                }
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
                    if (newPsw.text.length < 5 || !newPsw.text.matches("^[a-zA-Z0-9]+\$".toRegex())) {
                        settingsNewPswError.text =
                            "Password should has at least 5 symbols and has only numbers and letters"
                        isAlright = false
                    } else
                        settingsNewPswError.text = ""

                if (isAlright) {
                    user?.login = name.text.toString()
                    if(user?.authType!="N") {
                        user?.password = newPsw.text.toString()
                        user?.email = email.text.toString()
                    }
                    SleepingPetsService.updateUser(user!!,this)
                    // save user info
                    finish()
                }
            }
        }
        else {
            layout.addView(View.inflate(this, R.layout.social_nets_settings, null))
            val name = findViewById<EditText>(R.id.settingsName)
            val users:List<User> =SleepingPetsDatabase.getInstance(this).databaseDao.getUsers()
            name.setText(user?.login)
            val settingsNameError = findViewById<TextView>(R.id.settingsNameError)
            val done = findViewById<Button>(R.id.doneSettings)
            done.setOnClickListener {
                var isAlright = true
                when {
                    name.text.toString().length!! < 3 ->{
                        settingsNameError.text = "Name should have at least 3 symbols"
                        isAlright=false
                    }
                    users.stream().anyMatch { u -> u.login == name.text.toString() } -> {
                        settingsNameError.text = "This name is already taken"
                        isAlright=false
                    }
                    else -> settingsNameError.text = ""
                }

                if (isAlright) {
                    user?.login = name.text.toString()
                    SleepingPetsService.updateUser(user!!,this)
                    finish()
                }
            }
        }



        val logOut = findViewById<Button>(R.id.logOut)
        logOut.setOnClickListener {
            logOut()
        }
        val delete = findViewById<Button>(R.id.delete_profile)
        delete.setOnClickListener {
            SleepingPetsService.deleteUser(user!!,this)
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
        File(filesDir.path + "/id.bin").delete()
        val intent = Intent(this, LogInStartActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }
}