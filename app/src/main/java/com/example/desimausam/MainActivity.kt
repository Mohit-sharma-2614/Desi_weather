package com.example.desimausam
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.desimausam.Adapter.FiveDaysAdapter
import com.example.desimausam.Data.DailyForcast.DailyForcastData
import com.example.desimausam.Data.FiveDaysForcast.FiveDaysData
import com.example.desimausam.Interfaces.CurrentWeather
import com.example.desimausam.Interfaces.FiveDaysWeather
import com.example.desimausam.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    //    var myKey : String = "abd309d126d0ab6a54ca6b4ef80f63c9"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkInternetConnection(this)
        fetchWeatherData("Pilani")
        searchCity()
    }


    private fun searchCity() {
        val searchView = binding.svCity
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                fetchWeatherData(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {

        val url = "https://api.openweathermap.org/"
        val apiKey = "abd309d126d0ab6a54ca6b4ef80f63c9"

        val retrofitBuilderr = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FiveDaysWeather::class.java)
        retrofitBuilderr.getFiveDaysWeather(cityName, apiKey)
            .enqueue(object : retrofit2.Callback<FiveDaysData> {
                override fun onResponse(p0: Call<FiveDaysData>, p1: Response<FiveDaysData>) {
                    if(p1.isSuccessful){
                        val fiveDaysData = p1.body()
                        val fiveDaysAdapter = FiveDaysAdapter(fiveDaysData!!)
                        val recyclerView = binding.llHourlyEachRow
                        recyclerView.adapter = fiveDaysAdapter
                        recyclerView.layoutManager = LinearLayoutManager(
                            this@MainActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                    } else {
                        Log.d("fiveDaysonResponse","onResponse not successful")
                    }
                }
                override fun onFailure(p0: Call<FiveDaysData>, p1: Throwable) {
                    Log.d("getFiveDaysWeather", "Retrofit Fails $p1")
                }
            })
        val rtrofitDailyForcast = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrentWeather::class.java)
        rtrofitDailyForcast.getCurrentWeather(cityName, apiKey, "metric")
            .enqueue(object : retrofit2.Callback<DailyForcastData> {
                override fun onResponse(
                    p0: Call<DailyForcastData>,
                    p1: Response<DailyForcastData>
                ) {
                    if(p1.isSuccessful){
                        val dailyForcastData = p1.body()
                        val wind = dailyForcastData?.wind
                        val main = dailyForcastData?.main
                        val sys = dailyForcastData?.sys
                        val weather: String = dailyForcastData!!.weather[0].icon
                        binding.tvMainLocation.text = dailyForcastData.name
                        val mainTemp = main?.temp?.toInt()
                        binding.tvMainTemprature.text = (buildString {
                            append(mainTemp.toString())
                            append("°C")
                        })
                        binding.tvMainWindSpeed.text = (buildString {
                            append(wind?.speed.toString())
                            append("Km/h")
                        })
                        val animation = binding.lottieAnimation
                        when (weather) {

                            "01d" -> {
                                animation.setAnimation(R.raw.happy_sun)
                                binding.lottieBackground.setAnimation(R.raw.background_sun)
                            }

                            "02d","03d","04d" -> {
                                animation.setAnimation(R.raw.sun_cloud)
                                binding.lottieBackground.setAnimation(R.raw.background_sun)
                            }

                            "09d","10d" -> {
                                animation.setAnimation(R.raw.day_rain)
                                binding.lottieBackground.setAnimation(R.raw.rain_background)
                            }

                            "11d" -> {
                                animation.setAnimation(R.raw.rain_thunderstorm)
                                binding.lottieBackground.setAnimation(R.raw.rain_background)
                            }

                            "01n" -> {
                                animation.setAnimation(R.raw.moon)
                                binding.lottieBackground.setAnimation(R.raw.moon_backgroundd)
                            }

                            "02n","03n","04n" -> {
                                animation.setAnimation(R.raw.moon_cloud)
                                binding.lottieBackground.setAnimation(R.raw.moon_backgroundd)
                            }

                            "09n","10n","11n","13n","50n" -> {
                                animation.setAnimation(R.raw.moon_thunderstorm)
                                binding.lottieBackground.setAnimation(R.raw.rain_background)
                            }

                            else -> {
                                animation.setAnimation(R.raw.happy_sun)
                                binding.lottieBackground.setAnimation(R.raw.background_sun)
                            }
                        }
                        animation.playAnimation()

                        val sunrise = sys?.sunrise ?: 1
                        val sunset = sys?.sunset ?: 1
                        dayProgress(sunrise, sunset)
                        val realTimeSunRise = currentTime(sunrise)
                        val realTimeSunSet = currentTime(sunset)
                        binding.tvSunRiseTime.text = (buildString {
                            append(realTimeSunRise)
                            append(" AM")
                        })
                        binding.tvSunSetTime.text = (buildString {
                            append(realTimeSunSet)
                            append(" PM")
                        })
                        Log.d("dailyForcastData", dailyForcastData.name)
                    } else {
                        Log.d("getCurrentWeather", "Retrofit Fails $p1")
                        Toast.makeText(
                            this@MainActivity,
                            "City not found. Please enter a valid city name.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(p0: Call<DailyForcastData>, p1: Throwable) {
                    Log.d("getCurrentWeather", "Retrofit Fails $p1")
                    Toast.makeText(
                        this@MainActivity,
                        "Check Your Internet connection and ReOpen the app",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun currentTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(timestamp * 1000)
    }

    fun dayProgress(sunriseTimestamp: Long, sunsetTimestamp: Long) {
        val currentTime = System.currentTimeMillis() / 1000 // Current time in seconds
        val totalDayLength = sunsetTimestamp - sunriseTimestamp
        val elapsedTime = currentTime - sunriseTimestamp
        val progress = ((elapsedTime.toFloat() / totalDayLength.toFloat()) * 100).toInt()
        binding.sbSunTimeline.isEnabled = false
        binding.sbSunTimeline.progress = progress
    }

    private fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        val isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting

        if (!isConnected) {
            Toast.makeText(context, "Please turn on your internet connection", Toast.LENGTH_SHORT)
                .show()
            checkInternetConnection(context)
        } else {
            Toast.makeText(context, "Internet is connected", Toast.LENGTH_SHORT).show()
        }
        return isConnected
    }
}