package com.example.agroeasy

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.agroeasy.databinding.ActivityWeatherBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Weather : AppCompatActivity() {

    private val binding: ActivityWeatherBinding by lazy {
        ActivityWeatherBinding.inflate(layoutInflater)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize FusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Fetch weather data for the current location
        getCurrentLocation()

        // Setup the SearchView listener
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                fetchWeatherDataByLocation(latitude, longitude)
            } else {
                Log.e("WeatherApp", "Location is null")
            }
        }
    }

    private fun fetchWeatherDataByLocation(latitude: Double, longitude: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiInterface::class.java)
        val response = api.getWeatherDataByCoordinates(
            lat = latitude,
            lon = longitude,
            appid = "668368df8e66e5f5d4fda427c8c0ae06",
            units = "metric"
        )

        response.enqueue(object : Callback<agroeasy> {
            override fun onResponse(call: Call<agroeasy>, response: Response<agroeasy>) {
                if (response.isSuccessful && response.body() != null) {
                    updateUI(response.body()!!)
                } else {
                    Log.e("WeatherApp", "Response not successful")
                }
            }

            override fun onFailure(call: Call<agroeasy>, t: Throwable) {
                Log.e("WeatherApp", "Error: ${t.message}")
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiInterface::class.java)
        val response = api.getWeatherData(
            city = cityName,
            appid = "668368df8e66e5f5d4fda427c8c0ae06",
            units = "metric"
        )

        response.enqueue(object : Callback<agroeasy> {
            override fun onResponse(call: Call<agroeasy>, response: Response<agroeasy>) {
                if (response.isSuccessful && response.body() != null) {
                    updateUI(response.body()!!)
                } else {
                    Log.e("WeatherApp", "Response not successful")
                }
            }

            override fun onFailure(call: Call<agroeasy>, t: Throwable) {
                Log.e("WeatherApp", "Error: ${t.message}")
            }
        })
    }

    private fun updateUI(weatherData: agroeasy) {
        val temperature = weatherData.main.temp.toString()
        val humidity = weatherData.main.humidity
        val windSpeed = weatherData.wind.speed
        val sunRise = weatherData.sys.sunrise.toLong()
        val sunSet = weatherData.sys.sunset.toLong()
        val seaLevel = weatherData.main.pressure
        val condition = weatherData.weather.firstOrNull()?.main ?: "Unknown"
        val maxTemp = weatherData.main.temp_max
        val minTemp = weatherData.main.temp_min

        binding.temp.text = "$temperature °C"
        binding.weather.text = condition
        binding.maxTemp.text = "Max Temp: $maxTemp °C"
        binding.minTemp.text = "Min Temp: $minTemp °C"
        binding.humidity.text = "$humidity %"
        binding.windSpeed.text = "$windSpeed m/s"
        binding.sunrise.text = time(sunRise)
        binding.sunset.text = time(sunSet)
        binding.sea.text = "$seaLevel hPa"
        binding.condition.text = condition
        binding.day.text = dayName(System.currentTimeMillis())
        binding.date.text = date()
        binding.cityName.text = weatherData.name

        changeImages(condition)
    }

    private fun changeImages(conditions: String) {
        when (conditions.lowercase()) {
            "clear sky", "sunny", "clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "clouds", "overcast", "mist", "haze" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "rain", "drizzle", "showers" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "snow", "blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timeStamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timeStamp * 1000))
    }

    private fun dayName(timeStamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timeStamp * 1000))
    }
}
