package com.example.sleepingpets.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sleepingpets.R
import com.example.sleepingpets.models.db_models.Pet
import com.example.sleepingpets.models.db_models.WeekStatistics

class WeekStatisticAdapter(private val weeks: List<WeekStatistics>) :RecyclerView.Adapter<WeekStatisticAdapter.WeekStatisticViewHolder>(){
    inner class  WeekStatisticViewHolder(view: View): RecyclerView.ViewHolder(view){
        @SuppressLint("SetTextI18n")
        fun bind(week: WeekStatistics){
            val monPer=itemView.findViewById<TextView>(R.id.mon_per)
            val monView=itemView.findViewById<TextView>(R.id.mon_view)
            monPer.text=week.monday.toString()+"%"
            monView.layoutParams.height=(week.monday/100)*250
            val tuePer=itemView.findViewById<TextView>(R.id.tue_per)
            val tueView=itemView.findViewById<TextView>(R.id.tue_view)
            tuePer.text=week.tuesday.toString()+"%"
            tueView.layoutParams.height=(week.tuesday/100)*250
            val wedPer=itemView.findViewById<TextView>(R.id.wed_per)
            val wedView=itemView.findViewById<TextView>(R.id.wed_view)
            wedPer.text=week.wednesday.toString()+"%"
            wedView.layoutParams.height=(week.wednesday/100)*250
            val thuPer=itemView.findViewById<TextView>(R.id.thu_per)
            val thuView=itemView.findViewById<TextView>(R.id.thu_view)
            thuPer.text=week.thursday.toString()+"%"
            thuView.layoutParams.height=(week.thursday/100)*250
            val friPer=itemView.findViewById<TextView>(R.id.fri_per)
            val friView=itemView.findViewById<TextView>(R.id.fri_view)
            friPer.text=week.friday.toString()+"%"
            friView.layoutParams.height=(week.friday/100)*250
            val satPer=itemView.findViewById<TextView>(R.id.sat_per)
            val satView=itemView.findViewById<TextView>(R.id.sat_view)
            satPer.text=week.saturday.toString()+"%"
            satView.layoutParams.height=(week.saturday/100)*250
            val sunPer=itemView.findViewById<TextView>(R.id.sun_per)
            val sunView=itemView.findViewById<TextView>(R.id.sun_view)
            sunPer.text=week.friday.toString()+"%"
            sunView.layoutParams.height=(week.friday/100)*250
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekStatisticViewHolder {
        return WeekStatisticViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.week_statistic_item,parent,false))
    }

    override fun getItemCount(): Int {
        return weeks.size
    }

    override fun onBindViewHolder(holder: WeekStatisticViewHolder, position: Int) {
        holder.bind(weeks[position])
    }
}