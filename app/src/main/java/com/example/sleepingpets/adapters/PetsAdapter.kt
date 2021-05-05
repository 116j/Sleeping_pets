package com.example.sleepingpets.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sleepingpets.R
import com.example.sleepingpets.models.PetsGLRenderer
import com.example.sleepingpets.models.PetsGLSurfaceView
import com.example.sleepingpets.models.db_models.Pet

class PetsAdapter(private val pets: List<Pet>,private val layoutId:Int) :RecyclerView.Adapter<PetsAdapter.PetsViewHolder>(){
    inner class  PetsViewHolder(view: View):RecyclerView.ViewHolder(view){
        fun bind(pet:Pet){
          //  val renderer = PetsGLRenderer(pet.obj,itemView.context)
           // val view=PetsGLSurfaceView(itemView.context,renderer)
           // view.setRenderer(renderer)
            when(layoutId) {
                R.layout.user_pets_item -> {
                    itemView.findViewById<TextView>(R.id.user_pet_name).text = pet.name
                    itemView.findViewById<TextView>(R.id.user_pet_type).text = pet.type
                    val frame=itemView.findViewById<FrameLayout>(R.id.user_pet_frame)
                   // frame.addView(view)
                }
                R.layout.my_pets_item -> {
                    itemView.findViewById<TextView>(R.id.my_pet_name).text = pet.name
                    val frame=itemView.findViewById<FrameLayout>(R.id.my_pet_frame)
                   // frame.addView(view)

                }
                R.layout.buy_pets_item -> {
                   itemView.findViewById<TextView>(R.id.buy_pet_name).text = pet.type
                    val frame=itemView.findViewById<FrameLayout>(R.id.buy_pet_frame)
                 //   frame.addView(view)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
return PetsViewHolder(LayoutInflater.from(parent.context).inflate(layoutId,parent,false))
    }

    override fun getItemCount(): Int {
        return pets.size
    }

    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {
        holder.bind(pets[position])
    }
}