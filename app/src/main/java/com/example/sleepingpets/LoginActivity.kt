package com.example.sleepingpets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.observe
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.SleepingPetsService
import com.example.sleepingpets.models.db_models.User
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.vk.api.sdk.*
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.security.MessageDigest
import kotlin.math.log


class LoginActivity : AppCompatActivity() {
    lateinit var callbackManager: CallbackManager
lateinit var users:List<User>
lateinit var signUpName:EditText
    @SuppressLint("ShowToast", "PackageManagerGetSignatures")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)
        val layout=findViewById<ConstraintLayout>(R.id.login_main_layout)
layout.rootView.setBackgroundResource(R.drawable.standard)
        val close = findViewById<ImageView>(R.id.closeLogin)
        close.setOnClickListener {
            finish()
        }
        val progress=findViewById<ProgressBar>(R.id.progress_login)
        progress.visibility=View.VISIBLE
        SleepingPetsService.updateUsers()
         users= SleepingPetsDatabase.getInstance(this).databaseDao.getUsers()
        progress.visibility=View.INVISIBLE
        when (intent.getStringExtra("Auth")) {
            "signUp" -> {
                val nameLayout= View.inflate(this,R.layout.sign_up_name_layout,null)
                layout.addView(nameLayout)
                 signUpName = findViewById(R.id.signUpName)
                val signUpNameError = findViewById<TextView>(R.id.signUpNameError)

                val signUpNextButton = findViewById<Button>(R.id.signUpNextButton)
                signUpNextButton.setOnClickListener {
                    when {
                        signUpName.text.isEmpty() -> {
                            signUpNameError.text = "Name should have at least 3 symbols"
                            return@setOnClickListener
                        }
                        signUpName.text.length<3 -> {
                            signUpNameError.text =
                                "Name should have at least 3 symbols"
                            return@setOnClickListener
                        }
                        users.stream().anyMatch { u -> u.login == signUpName.text.toString() } -> {
                            signUpNameError.text =
                                "This name is already taken"
                            return@setOnClickListener
                        }
                        else -> {
                            layout.removeView(nameLayout)
                            layout.addView(View.inflate(this,R.layout.sign_up_info_layout,null))

                            val signUpEmail = findViewById<EditText>(R.id.signUpEmail)
                            val signUpPsw = findViewById<EditText>(R.id.signUpPsw)
                            val signUpRepeatPsw = findViewById<EditText>(R.id.signUpRepeatPsw)
                            val signUpEmailError = findViewById<TextView>(R.id.signUpEmailError)
                            val signUpPswError = findViewById<TextView>(R.id.signUpPswError)
                            val signUpRepeatPswError =
                                findViewById<TextView>(R.id.signUpRepeatPswError)

                            val signUpButtonN = findViewById<Button>(R.id.signUpButtonN)
                            signUpButtonN.setOnClickListener {
                                var isAlright = true
                                if (signUpEmail.text.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(
                                        signUpEmail.text
                                    ).matches()
                                ) {
                                    signUpEmailError.text = "Enter your email"
                                    isAlright = false
                                } else
                                    signUpEmailError.text = ""
                                if (signUpPsw.text.length < 5 || !signUpPsw.text.matches("^[a-zA-Z0-9]+\$".toRegex())) {
                                    signUpPswError.text =
                                        "Password should has at least 5 symbols and has only numbers and letters"
                                    isAlright = false
                                } else {
                                    signUpPswError.text = ""
                                    if (signUpRepeatPsw.text.toString() != signUpPsw.text.toString()) {
                                        signUpRepeatPswError.text = "Password doesn't match"
                                        isAlright = false
                                    } else
                                        signUpRepeatPswError.text = ""
                                }
                                if (isAlright) {
                                    user = User(
                                        login = signUpName.text.toString(),
                                        email = signUpEmail.text.toString(),
                                        password = signUpPsw.text.toString(),
                                        authType = "N"
                                    )
                                    SleepingPetsService.addUser(user!!, this)
                                    finishLogin()
                                }
                            }

                            findViewById<LinearLayout>(R.id.FBSignUp).setOnClickListener {
                                facebookLogin()
                            }

                            findViewById<LinearLayout>(R.id.VKSignUp).setOnClickListener {
                                VK.initialize(this)
                                VK.login(this)
                            }
                        }
                    }
                }
            }
            "logIn" -> {
                val loginLayout= View.inflate(this,R.layout.login_in_layout,null)
                layout.addView(loginLayout)
                val logInEmail = findViewById<EditText>(R.id.logInEmail)
                val logInPsw = findViewById<EditText>(R.id.logInPsw)
                val loginEmailError = findViewById<TextView>(R.id.loginEmailError)
                val loginPswError = findViewById<TextView>(R.id.loginPswError)
                findViewById<Button>(R.id.loginButton).setOnClickListener {
                    user =
                        users.find { u -> u.email == logInEmail.text.toString() || u.login == logInEmail.text.toString(); }
                    if (user == null)
                        loginEmailError.text = "User with such email or username doesn't exists"
                    else
                        loginEmailError.text = ""

                    if (user != null && logInPsw.text.toString() != user?.password) {
                        loginPswError.text = "Incorrect password"
                        user = null
                    } else
                        loginPswError.text = ""

                    if (user != null) {
                        finishLogin()
                    }
                }
                findViewById<TextView>(R.id.LogInForgotPsw).setOnClickListener{
                   val emailRestoreLayout=View.inflate(this,R.layout.send_code,null)
                    layout.removeView(loginLayout)
                    layout.addView(emailRestoreLayout)
                    val email=findViewById<EditText>(R.id.restoreEmail)
                    findViewById<Button>(R.id.restorePsw).setOnClickListener{
                        val emailError=findViewById<TextView>(R.id.restoreEmailError)
                        user =
                            users.find { u -> u.email == email.text.toString(); }
                        if (user == null)
                            emailError.text = "User with such email doesn't exists"
                        else {
                            emailError.text = ""
                        }
                    }
                }

                findViewById<LinearLayout>(R.id.FBLogIn).setOnClickListener {
                    facebookLogin()
                    user =
                        users.find { u -> u.authType == "F" && u.socialNetId == user!!.socialNetId; }
                    if (user == null) {
                        Toast.makeText(this, "No such user. Please, sign up", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    finishLogin()
                }

                findViewById<LinearLayout>(R.id.VKLogIn).setOnClickListener {
                    VK.initialize(this)
                     VK.login(this)
                    user =
                        users.find { u -> u.authType == "V" && u.socialNetId == user!!.socialNetId; }
                    if (user == null) {
                        Toast.makeText(this, "No such user. Please, sign up", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    finishLogin()
                }
            }
        }
    }

    private fun facebookLogin() {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance()
            .logInWithReadPermissions(this, listOf("public_profile", "email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { `object`, _ ->
                        try {
                            user = User(
                                socialNetId = `object`.getString("id"),
                                email = `object`.getString("email"),
                                image = `object`.getString("picture.type(large)"),
                                authType = "F"
                            )
                            SleepingPetsService.updateUsers()
                            val usr =
                                users.find { u -> u.authType == "F" && u.socialNetId == user!!.socialNetId; }
                            if (usr != null)
                                user = usr
                            else {
                                user?.login = signUpName.text.toString()
                                SleepingPetsService.addUser(user!!,this@LoginActivity)
//                            }
                                finishLogin()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,email,picture.type(large)")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                VK.execute(object : ApiCommand<User>() {
                    override fun onExecute(manager: VKApiManager): User {
                        val call = VKMethodCall.Builder()
                            .method("users.get")
                            .args("fields", "photo_200")
                            .version(manager.config.version)
                            .build()
                        return manager.execute(call, object : VKApiResponseParser<User> {
                            override fun parse(response: String): User {
                                try {
                                    val ja = JSONObject(response).getJSONArray("response")

                                    return User(
                                        socialNetId = ja.getJSONObject(0).optString("id", ""),
                                        image = ja.getJSONObject(0).optString("photo_200", ""),
                                        authType = "V"
                                    )
                                } catch (ex: JSONException) {
                                    throw VKApiIllegalResponseException(ex)
                                }
                            }
                        })
                    }
                }, object : VKApiCallback<User> {
                    override fun fail(error: Exception) {
                    }

                    override fun success(result: User) {
                        user = result
                        val usr =
                            users.find { u -> u.authType == "V" && u.socialNetId == user!!.socialNetId; }
                        if (usr != null)
                            user = usr
                        else {
                            user?.login = signUpName.text.toString()
                            SleepingPetsService.addUser(user!!,this@LoginActivity)
                        }
                        finishLogin()
                    }
                })
            }

            override fun onLoginFailed(errorCode: Int) {
                // User didn't pass authorization
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun finishLogin() {
        saveUserId()
        val intent = Intent(this, AlarmActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun saveUserId() {
        val fos: FileOutputStream = FileOutputStream(filesDir.path + "/id.bin")
        val os = ObjectOutputStream(fos)
        os.writeObject(user?.id)
        os.close()
        fos.close()

    }
}