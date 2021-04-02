package com.example.sleepingpets

import android.content.Intent
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk

open class Application: android.app.Application() {

    private val tokenTracker = object: VKAccessTokenTracker() {
        override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
            val intent=Intent(applicationContext,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }
    override fun onCreate() {
        super.onCreate()
        VKSdk.initialize(applicationContext)
    }
}