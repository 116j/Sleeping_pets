package com.example.sleepingpets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.db_models.Suggestion
import com.example.sleepingpets.models.db_models.User
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.net.URL

class SuggestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestion)
        val layout = findViewById<ConstraintLayout>(R.id.suggestion_layout)
        layout.rootView.setBackgroundResource(R.drawable.standard)

        val suggestion = intent.getSerializableExtra("suggestion") as? Suggestion
        var suggester:User =User(login = "",password = "",authType = "")
          suggester=  SleepingPetsDatabase.getInstance(this).databaseDao.getUser(suggestion!!.userId)
        val userImage = findViewById<CircleImageView>(R.id.suggestion_user_image)
        val userName = findViewById<TextView>(R.id.suggestion_user_name)
        val userPetScore = findViewById<TextView>(R.id.suggestion_user_pet_score)

        if(suggester?.image!!.startsWith("@drawable")) {
            val id = resources
                .getIdentifier(user?.image!!.substring(10), "drawable", packageName)
            userImage.setImageDrawable(ContextCompat.getDrawable(this,id))
        }
        else
            userImage.setImageBitmap(getBitmapFromAssets(suggester?.image!!))
        userName.text = suggester!!.login
        userPetScore.text = suggester!!.petScore.toString()

        val image = findViewById<CircleImageView>(R.id.suggestion_image)
        val name = findViewById<TextView>(R.id.suggestion_name)
        val petScore = findViewById<TextView>(R.id.suggestion_pet_score)

        image.setImageBitmap(getBitmapFromAssets(user!!.image))
        name.text = user!!.login
        petScore.text = user!!.petScore.toString()

        val back=findViewById<LinearLayout>(R.id.suggestionsBack)

        back.setOnClickListener{
            finish()
        }

        findViewById<TextView>(R.id.coinsText).text= user?.balance.toString()
    }

    private fun getBitmapFromAssets(fileName: String): Bitmap? {
        return try {
            BitmapFactory.decodeStream(URL(fileName).openStream())
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}