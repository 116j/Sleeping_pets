package com.example.sleepingpets.adapters

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.sleepingpets.R
import com.example.sleepingpets.models.db_models.Pet
import com.example.sleepingpets.models.db_models.User
import com.example.sleepingpets.user
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.net.URL

class GridAdapter(private val context: Activity, private val items: List<Pet>) :
    ArrayAdapter<Pet>(context, R.layout.bottom_menu_item, items) {
    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val view: View = inflater.inflate(R.layout.bottom_menu_item, null)
        val image=view.findViewById<CircleImageView>(R.id.bottom_menu_image)

        val pet=items[position]
        return convertView ?: view
    }

}