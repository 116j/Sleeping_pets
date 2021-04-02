package com.example.sleepingpets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.observe
import com.example.sleepingpets.adapters.GridAdapter
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.SleepingPetsService
import com.example.sleepingpets.models.db_models.Pet
import com.example.sleepingpets.models.db_models.User
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ViewListener
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.net.URL

class RatingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating_page)
        findViewById<ConstraintLayout>(R.id.rating_page_layout).rootView.setBackgroundResource(R.drawable.standard)

        val user=intent.getSerializableExtra("user") as? User

        val image=findViewById<CircleImageView>(R.id.rating_image)
        val number=findViewById<TextView>(R.id.rating_number)
        val name=findViewById<TextView>(R.id.rating_name)
        val petScore=findViewById<TextView>(R.id.rating_pets_score)
        val sleepScore=findViewById<TextView>(R.id.rating_sleep_percent)

        image.setImageBitmap(getBitmapFromAssets(user!!.image))
        number.text=intent.getIntExtra("position",0).toString()
        name.text= user.login
        petScore.text= user.petScore.toString()
        sleepScore.text= user.sleepScore.toString()

        val bottomMenu = findViewById<LinearLayout>(R.id.bottom_menu)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomMenu)
        val closeMenu = findViewById<ImageView>(R.id.open_bottom_menu)
        closeMenu.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        closeMenu.setImageDrawable(resources.getDrawable(R.mipmap.menu_arrow_up))
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        closeMenu.setImageDrawable(resources.getDrawable(R.mipmap.menu_arrow_down))
                    }
                }
            }
        })

        val menuName=findViewById<TextView>(R.id.bottom_menu_name)
        menuName.text="Choose a pet"

        SleepingPetsService.updateUserPets(user!!.id)
        var pets:List<Pet> = listOf()
        SleepingPetsDatabase.getInstance(this).databaseDao.getUserPets(user!!.id).observe(this){
            pets=it
        }
        val grid = findViewById<GridView>(R.id.bottom_grid)
        val adapter = GridAdapter(this, pets, true)
        grid.adapter = adapter
        grid.setOnItemClickListener { parent, view, position, id ->

            //chose for sleep
        }

        val carouselView = findViewById<CarouselView>(R.id.rating_carousel_view)
        carouselView.pageCount = pets.size
        carouselView.setViewListener(object : ViewListener {
            override fun setViewForPosition(position: Int): View {
                val view = layoutInflater.inflate(R.layout.user_carousel_view_item, null)

                return  view
            }
        })

        val ratingBack=findViewById<LinearLayout>(R.id.ratingBack)
        ratingBack.setOnClickListener {
            finish()
        }
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