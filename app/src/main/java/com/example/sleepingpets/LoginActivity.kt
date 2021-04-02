package com.example.sleepingpets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
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
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*


class LoginActivity : AppCompatActivity() {
    lateinit var callbackManager: CallbackManager

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        val layout = findViewById<ConstraintLayout>(R.id.login_layout)
        var signUpNameLayout: View? = null
        layout.rootView.setBackgroundResource(R.drawable.standard)

        val close = findViewById<ImageView>(R.id.closeLogin)
        close.setOnClickListener {
            finish()
        }
        SleepingPetsService.updateUsers()
        var users: List<User> = listOf()
        SleepingPetsDatabase.getInstance(this).databaseDao.getUsers().observe(this) {
            users = it
        }
        when (intent.getStringExtra("Auth")) {
            "signUp" -> {
                signUpNameLayout = View.inflate(this, R.layout.sign_up_name_layout, null)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                params.gravity = Gravity.CENTER_HORIZONTAL
                signUpNameLayout.layoutParams = params
                layout.addView(signUpNameLayout)
                val signUpName = findViewById<EditText>(R.id.signUpName)
                val signUpNameError = findViewById<TextView>(R.id.signUpNameError)
                signUpName.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        when {
                            s?.length!! < 3 -> signUpNameError.text =
                                "Name should have at least 3 symbols"
                            users.stream().anyMatch { u -> u.login == s } -> signUpNameError.text =
                                "This name is already taken"
                            else -> signUpNameError.text = ""
                        }
                    }
                })

                val signUpNextButton = findViewById<Button>(R.id.signUpNextButton)
                signUpNextButton.setOnClickListener {
                    layout.removeView(signUpNameLayout)
                    val info=View.inflate(this, R.layout.sign_up_info_layout, null)
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    params.gravity = Gravity.CENTER_HORIZONTAL
                    info.layoutParams=params
                    layout.addView(info)

                    val signUpEmail = findViewById<EditText>(R.id.signUpEmail)
                    val signUpPsw = findViewById<EditText>(R.id.signUpEmail)
                    val signUpRepeatPsw = findViewById<EditText>(R.id.signUpEmail)
                    val signUpEmailError = findViewById<TextView>(R.id.signUpEmailError)
                    val signUpPswError = findViewById<TextView>(R.id.signUpPswError)
                    val signUpRepeatPswError = findViewById<TextView>(R.id.signUpRepeatPswError)

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
                        if (signUpPsw.text.length > 5 && !signUpPsw.text.matches("[a-zA-Z0-9]*".toRegex())) {
                            signUpPswError.text =
                                "Password should has at least 5 symbols and has only numbers and letters"
                            isAlright = false
                        } else {
                            signUpPswError.text = ""
                            if (signUpRepeatPsw.text != signUpPsw.text) {
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
                            SleepingPetsService.addUser(user!!)
                            finishLogin()
                        }
                    }

                    findViewById<LinearLayout>(R.id.FBSignUp).setOnClickListener {
                        facebookLogin()
                        SleepingPetsService.updateUsers()
                        val usr =
                            users.find { u -> u.authType == "F" && u.socialNetId == user!!.socialNetId; }
                        if (usr != null)
                            user = usr
                        else {
                            user?.login = signUpName.text.toString()
                            SleepingPetsService.addUser(user!!)
                        }
                        finishLogin()
                    }

                    findViewById<LinearLayout>(R.id.VKSignUp).setOnClickListener {
                        VK.login(this)
                        val usr =
                            users.find { u -> u.authType == "V" && u.socialNetId == user!!.socialNetId; }
                        if (usr != null)
                            user = usr
                        else {
                            user?.login = signUpName.text.toString()
                            SleepingPetsService.addUser(user!!)
                        }
                        finishLogin()
                    }
                }
            }
            "logIn" -> {
                val login = View.inflate(this, R.layout.login_in_layout, null)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                params.gravity = Gravity.CENTER_HORIZONTAL
                login.layoutParams=params
                layout.addView(login)
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

                findViewById<LinearLayout>(R.id.FBLogIn).setOnClickListener {
                    facebookLogin()
                    user =
                        users.find { u -> u.authType == "F" && u.socialNetId == user!!.socialNetId; }
                    if (user == null) {
                        Toast.makeText(this, "No such user. Please, sign up", Toast.LENGTH_SHORT)
                        return@setOnClickListener
                    }
                    finishLogin()
                }

                findViewById<LinearLayout>(R.id.VKLogIn).setOnClickListener {
                    VK.login(this)
                    user =
                        users.find { u -> u.authType == "V" && u.socialNetId == user!!.socialNetId; }
                    if (user == null) {
                        Toast.makeText(this, "No such user. Please, sign up", Toast.LENGTH_SHORT)
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
                                authType = "F",
                                password = "",
                                login = ""
                            )
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
                                        login = "",
                                        authType = "V",
                                        password = ""
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
        val fos: FileOutputStream = openFileOutput(filesDir.path + "id.bin", Context.MODE_PRIVATE)
        val os = ObjectOutputStream(fos)
        os.writeObject(user?.id)
        os.close()
        fos.close()

    }
}