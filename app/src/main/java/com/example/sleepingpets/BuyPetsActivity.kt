package com.example.sleepingpets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.sleepingpets.adapters.PetsAdapter
import com.example.sleepingpets.models.db_models.Pet

class BuyPetsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_pets)
        val layout = findViewById<ConstraintLayout>(R.id.buy_pet_layout)
        layout.rootView.setBackgroundResource(R.drawable.standard)

        val pets= listOf(Pet(name="",type="Shena",obj="sampledata/models/shena.obj",userId = 0),
            Pet(name="",type="Luna",obj="sampledata/models/luna.obj",userId = 0),
            Pet(name="",type="Lupin",obj="sampledata/models/lupin.obj",userId = 0),
            Pet(name="",type="Artoo",obj="sampledata/models/artoo.obj",userId = 0))

        val carouselView = findViewById<ViewPager2>(R.id.buy_pet_carousel_view)
        carouselView.adapter = PetsAdapter(pets,R.layout.buy_pets_item)


        val back=findViewById<LinearLayout>(R.id.petsBack)
        back.setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.coinsText).text= user?.balance.toString()
    }
}