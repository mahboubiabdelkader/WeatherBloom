package weatherBloom

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

data class MeteoData(
    val temperature: Int,
    val wind: Int,
    val realFeel: Int,
    val chanceOfRain: Int,
    val visibility: Int,
    val pressure: Int,
    val humidity: Int
)
fun MeteoData.weatherState(): String{
    var state=""
    state= when{
        wind > 30 -> "windy.png"
        chanceOfRain> 60 ->"rainy.png"
        temperature> 20 && temperature<25 ->"normal.png"
        temperature> 25 ->"sunny.png"
        else ->"cloudy.png"
    }
    return state
}
data class Day(
    val name: String,
    val sunrise: String,
    val sunset: String,
    val hourlyData: Map<Int, MeteoData>, // Map of hour to MeteoData
    var minTemperature: Int = Int.MAX_VALUE,  // Default to a very high value
    var maxTemperature: Int = Int.MIN_VALUE,  // Default to a very low value
    var weatherState: String = "Unknown"      // New property to store weather state
)
fun Day.selectMood():Boolean{
    val time=java.time.LocalTime.now().hour
    var parts = sunset.split(":") // Split the time string by ":"
     val st= parts[0].toInt()
    parts = sunrise.split(":")
    val sr= parts[0].toInt()

    var mood=false //lile
    if (    time> sr && time< st){
        println(sunrise.toInt())
        mood=true
    }

    return mood
}
// Determine the weather state based on certain criteria
fun Day.updateWeatherState() {
    // Example weather conditions based on MeteoData
    val avgTemperature = (minTemperature + maxTemperature) / 2
    val avgWindSpeed = hourlyData.values.map { it.wind }.average()
    val avgChanceOfRain = hourlyData.values.map { it.chanceOfRain }.average()    // Set the weather state based on conditions
    weatherState = when {
        avgWindSpeed > 30 -> "Windy"
        avgChanceOfRain > 60 -> "Rainy"
        avgTemperature > 20 -> "Sunny"
        avgTemperature < 0 -> "Snowy"
        else -> "Cloudy"
    }
}
fun Day.weatherPhoto():String{
    val photo = when{
        weatherState=="Windy" -> "windy.png"
        weatherState=="Rainy" -> "windy.png"
        weatherState=="Sunny" -> "windy.png"
        else -> "cloudy.png"
    }
    return photo
}
fun Day.getCurrentHourMeteo(): MeteoData? {
    // Get the current hour from the system
    val currentHour = java.time.LocalTime.now().hour
    return hourlyData[currentHour]
}
fun Day.getHourMeteo( h: Int):MeteoData? {
    return hourlyData[h]
}

fun Day.isToday(): Boolean {
    val todayName = LocalDate.now()
        .dayOfWeek
        .getDisplayName(TextStyle.FULL, Locale.ENGLISH) // Get today's full name, e.g., "Monday"
    return this.name.equals(todayName, ignoreCase = true) // Compare day name with today's name
}




data class City(
    val name: String,
    val days: List<Day> // List of Day objects
)
fun City.toDay(): Day?{
    this.days.forEach{
        day: Day -> if (day.isToday()) return  day
    }
return  null
}
fun City.time(): String{
    // Get the current time
    var currentTime = LocalDateTime.now()

    // Check if the city name is "New York" and subtract 6 hours
    if (this.name.equals("New York", ignoreCase = true)) {
        currentTime = currentTime.minusHours(6) // Subtract 6 hours
    }

    // Format the current time
    return   currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))

}


class WeatherViewModel {
    var defaultCity: City? = null // Holds the default city
    suspend fun loadCSVData(filePath: String): List<City> {
        return withContext(Dispatchers.IO) {
            val cityMap = mutableMapOf<String, MutableList<Day>>() // Map cityName -> List<Day>
            println("Reading CSV file...")

            csvReader().readAllWithHeader(File(filePath)).forEach { row ->
                val cityName = row["City"] ?: ""
                val dayName = row["Day"] ?: ""
                val hour = row["Hour"]?.toInt() ?: 0
                val sunrise = row["Sunrise"] ?: ""
                val sunset = row["Sunset"] ?: ""

                // Create MeteoData for the row, parsing decimal values as Double and converting to Int
                val meteoData = MeteoData(
                    temperature = row["Temperature"]?.toDouble()?.toInt() ?: 0,
                    wind = row["Wind"]?.toDouble()?.toInt() ?: 0,
                    realFeel = row["RealFeel"]?.toDouble()?.toInt() ?: 0,
                    chanceOfRain = row["ChanceOfRain"]?.toDouble()?.toInt() ?: 0,
                    visibility = row["Visibility"]?.toDouble()?.toInt() ?: 0,
                    pressure = row["Pressure"]?.toDouble()?.toInt() ?: 0,
                    humidity = row["Humidity"]?.toDouble()?.toInt() ?: 0
                )

                // Get or create city
                val cityDays = cityMap.getOrPut(cityName) { mutableListOf() }

                // Find or create the day
                val day = cityDays.find { it.name == dayName }
                    ?: Day(name = dayName, sunrise = sunrise, sunset = sunset, hourlyData = mutableMapOf<Int, MeteoData>().also {
                        cityDays.add(Day(name = dayName, sunrise = sunrise, sunset = sunset, hourlyData = it))
                    })

                // Add the hour and its MeteoData
                (day.hourlyData as MutableMap)[hour] = meteoData
                // Update the min and max temperatures for the day (as Int)
                day.minTemperature = minOf(day.minTemperature, meteoData.temperature)
                day.maxTemperature = maxOf(day.maxTemperature, meteoData.temperature)
                // Update the weather state based on the data
                day.updateWeatherState()
            }

            println("Total cities loaded: ${cityMap.size}")
            val cities = cityMap.map { (cityName, days) -> City(name = cityName, days = days) }
            // Reorder days for each city
            val reorderedCities = reorderCitiesByCurrentDay(cities)
            // Set the default city to the first city in the list (or any other logic)
            defaultCity = reorderedCities.firstOrNull()
            println("Default city: ${defaultCity?.name}")

            reorderedCities
        }
    }
}

private fun reorderCitiesByCurrentDay(cities: List<City>): List<City> {
    // Get the current day of the week as an integer (1 = Monday, ..., 7 = Sunday)
    val currentDayIndex = LocalDate.now().dayOfWeek.value

    return cities.map { city ->
        // Reorder days so that the current day comes first and days follow in natural order
        val reorderedDays = city.days.sortedBy { day ->
            // Map day names to DayOfWeek indices
            val dayIndex = DayOfWeek.valueOf(day.name.uppercase()).value
            // Calculate circular order based on the current day
            (dayIndex - currentDayIndex + 7) % 7
        }
        // Return a new City instance with reordered days
        city.copy(days = reorderedDays)
    }
}

