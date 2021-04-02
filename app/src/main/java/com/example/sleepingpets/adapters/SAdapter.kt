package com.example.sleepingpets.adapters

import android.annotation.SuppressLint
import android.app.Activity
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

class SAdapter(private val context: Activity, private val items: List<User>) :
    ArrayAdapter<User>(context, R.layout.search_list_item, items) {
    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val view: View = inflater.inflate(R.layout.search_list_item, null)
        val image = view.findViewById<CircleImageView>(R.id.search_user_image)
        val name = view.findViewById<TextView>(R.id.search_user_name)
        val petScore = view.findViewById<TextView>(R.id.search_pets_score)

        val user = items[position]
        image.setImageBitmap(getBitmapFromAssets(user.image))
        name.text = user.login
        petScore.text = user.petScore.toString()

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