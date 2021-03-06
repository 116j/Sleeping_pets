package com.example.sleepingpets

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.sleepingpets.adapters.PetsAdapter
import com.example.sleepingpets.models.SleepingPetsDatabase
import com.example.sleepingpets.models.SleepingPetsService
import com.example.sleepingpets.models.db_models.Pet
import com.example.sleepingpets.models.db_models.User
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.net.URL

class SearchPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_page)
        findViewById<CoordinatorLayout>(R.id.search_page_layout).rootView.setBackgroundResource(R.drawable.standard)

        val user=intent.getSerializableExtra("user")as? User
        val image=findViewById<CircleImageView>(R.id.search_user_image)
        val name=findViewById<TextView>(R.id.search_user_name)
        val petScore=findViewById<TextView>(R.id.search_pets_score)

        if(user?.image!!.startsWith("@drawable")) {
            val id = resources
                .getIdentifier(user?.image!!.substring(10), "drawable", packageName)
            image.setImageDrawable(ContextCompat.getDrawable(this,id))
        }
        else
            image.setImageBitmap(getBitmapFromAssets(user?.image!!))
        name.text= user.login
        petScore.text= user.petScore.toString()


        val progress=findViewById<ProgressBar>(R.id.progress_search_pets)
        progress.visibility=View.VISIBLE
        SleepingPetsService.updateUserPets(user.id)
        val pets:List<Pet> =SleepingPetsDatabase.getInstance(this).databaseDao.getUserPets(user.id)
        progress.visibility=View.INVISIBLE
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

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED;
                }
            }
        })

        val menuName = findViewById<TextView>(R.id.bottom_menu_name)
        menuName.text = "Choose your pet for sleep"

        val carouselView = findViewById<ViewPager2>(R.id.search_carousel_view)
        carouselView.adapter= PetsAdapter(pets,R.layout.user_pets_item)
        findViewById<Button>(R.id.search_sleep_together_button).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED;
        }
        val back=findViewById<LinearLayout>(R.id.searchBack)
        back.setOnClickListener {
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