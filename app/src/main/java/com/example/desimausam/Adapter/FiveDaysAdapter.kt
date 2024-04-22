package com.example.desimausam.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.desimausam.Data.FiveDaysForcast.FiveDaysData
import com.example.desimausam.Data.FiveDaysForcast.WhichDay
import com.example.desimausam.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FiveDaysAdapter(private val feedData : FiveDaysData) : RecyclerView.Adapter<FiveDaysAdapter.FiveDaysViewHolder>() {

    class FiveDaysViewHolder(v : View) : RecyclerView.ViewHolder(v){
        val timeStamp = v.findViewById<TextView>(R.id.tv_hourly_now_text)
        val temp = v.findViewById<TextView>(R.id.tv_hourly_now_temp)
        val icon = v.findViewById<ImageView>(R.id.img_hourly_now_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiveDaysViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.each_row, parent, false)
        return FiveDaysViewHolder(view)
    }

    override fun getItemCount(): Int {
        return feedData.list.size
    }

    override fun onBindViewHolder(holder: FiveDaysViewHolder, position: Int) {
        val data = feedData.list[position]
        val realTemp = feedData.list[position].main

        val weather = feedData.list[position].weather
        // Check if weather data is available and not empty
        val weatherIcon = if (weather.isNotEmpty()) {
            weather[0].icon // Access the first weather description
        } else {
            "" // If no weather data available, set an empty string
        }
        fun convertUnixTimeToHourMinute(unixTime: Long): String {
            // Create a Date object from the Unix timestamp
            val date = Date(unixTime * 1000L)

            // Create a SimpleDateFormat object to format the time
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

            // Set the timezone to UTC
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            // Format the time and return it as a string
            return sdf.format(date)
        }
        holder.timeStamp.text = convertUnixTimeToHourMinute(data.dt)
        val rT = realTemp.temp_max.toInt() // Converted the temprature into int from double to make it simple
        holder.temp.text = ( rT.toString() + "°" ) // Shows the temprature as a string
        val icon = holder.icon
        when (weatherIcon){
            "03d","04d" -> icon.setImageResource(R.drawable.clouds)
            "02d" -> icon.setImageResource(R.drawable.sun_cloud)
            "10d" -> icon.setImageResource(R.drawable.sun_cloud_rain)
            "09d" -> icon.setImageResource(R.drawable.heavy_rain)
            "11d" -> icon.setImageResource(R.drawable.strom_main)
            "01n" -> icon.setImageResource(R.drawable.moon_night)
            "03n","04n" -> icon.setImageResource(R.drawable.clouds)
            "02n" -> icon.setImageResource(R.drawable.moon_cloud)
            "10n" -> icon.setImageResource(R.drawable.moon_cloud_rain)
            "09n" -> icon.setImageResource(R.drawable.heavy_rain)
            "11n" -> icon.setImageResource(R.drawable.storm)
            else -> icon.setImageResource(R.drawable.sun)
        }
    }

}