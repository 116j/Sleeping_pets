package com.example.sleepingpets.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.sleepingpets.R
import com.example.sleepingpets.models.db_models.User
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.net.URL

class RatingListAdapter(private val context: Activity, private val items: List<User>) :
    ArrayAdapter<User>(context, R.layout.rating_list_item, items) {
    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val view: View = inflater.inflate(R.layout.rating_list_item, null)
        val image=view.findViewById<CircleImageView>(R.id.rating_user_image)
        val number=view.findViewById<TextView>(R.id.user_rating_number)
        val name=view.findViewById<TextView>(R.id.rating_user_image)
        val petScore=view.findViewById<TextView>(R.id.rating_user_pets_score)
        val sleepScore=view.findViewById<TextView>(R.id.rating_user_sleep_percent)

        val user=items[position]
        image.setImageBitmap(getBitmapFromAssets(user.image))
        number.text=(position+1).toString()
        name.text=user.login
        petScore.text= user.petScore.toString()
        sleepScore.text=user.sleepScore.toString()

        return view
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