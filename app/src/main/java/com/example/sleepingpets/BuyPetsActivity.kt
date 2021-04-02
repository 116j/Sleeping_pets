package com.example.sleepingpets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ViewListener

class BuyPetsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_pets)
        val layout = findViewById<ConstraintLayout>(R.id.buy_pet_layout)
        layout.rootView.setBackgroundResource(R.drawable.standard)

        val carouselView = findViewById<CarouselView>(R.id.buy_pet_carousel_view)
      //  carouselView.pageCount = pets.size
        carouselView.setViewListener {
            val view = layoutInflater.inflate(R.layout.buy_carousel_view_item, null)

            view
        }

        val back=findViewById<LinearLayout>(R.id.petsBack)
        back.setOnClickListener {
            finish()
        }
    }
}