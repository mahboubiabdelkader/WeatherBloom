package weatherBloom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



import textcolor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*Column(
modifier = Modifier
.fillMaxSize()
.weight(2f) // Main content takes more space
) {
    MainContent(selectedCity)
}

Spacer(modifier = Modifier.width(25.dp)) // Add space between MainContent and WeeklyForecast

// Weekly forecast takes less space than MainContent
Column(
modifier = Modifier
.fillMaxHeight()
.weight(1f) // WeeklyForecast takes smaller space than MainContent but larger than Sidebar
) {
    WeeklyForecast(selectedCity)
}*/
/*
@Composable
fun CitySelectionScreen(cities: List<City>, onCitySelected: (City) -> Unit, selectedCity: City?) {
    Row(
        modifier = Modifier.fillMaxSize() // Ensure the Row fills the entire width and height
    ) {
        // First part takes 70% of the width
        Column(
            modifier = Modifier
                .fillMaxHeight() // Fill the height
                .weight(0.6f) // 70% of the width
        ) {
            CitiesPart(cities, onCitySelected,selectedCity)
        }

        // Spacer between the parts (optional)
        Spacer(modifier = Modifier.width(16.dp))

        // Second part takes 30% of the width
        Column(
            modifier = Modifier
                .fillMaxHeight() // Fill the height
                .weight(0.4f) // 30% of the width
        ) {
             WeeklyForecast(selectedCity) // Uncomment and customize
        }
    }
}


    @Composable
    fun CityItem(city: City, onClick: () -> Unit, selectedCity: City?) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Example: you can add an icon for each city here if needed
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }

    @Composable
    fun CitiesPart(cities: List<City>, onCitySelected: (City) -> Unit, selectedCity: City?) {
        Column(
             modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Select a City",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cities) { city ->
                    CityItem(city = city, onClick = { onCitySelected(city) },selectedCity)
                }
            }
        }
    }
*/

@Composable
fun CitySelectionScreen(cities: List<City>, onCitySelected: (City) -> Unit, selectedCity: City?) {
    Row(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)
    ) {
        // Cities List Section
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.6f)
                .padding(16.dp)
        ) {
            Text(
                text = "Select a City",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 35.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                items(cities) { city ->
                    CityCard(
                        city = city,
                        weatherState = city.toDay()?.getCurrentHourMeteo()?.weatherState()?:"cloudy.png",
                        onClick = { onCitySelected(city) },
                        isSelected = selectedCity == city
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(25.dp))

        // Detailed Weather Section
        selectedCity?.let {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.4f)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxHeight(0.8f)
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(50.dp))

                    // Top Row for City Details
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp )
                    ) {
                        // Text Section
                        Column(
                            modifier = Modifier
                                .weight(0.7f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = selectedCity.name,
                                style = MaterialTheme.typography.h4,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Chance of rain: ${selectedCity.toDay()?.getCurrentHourMeteo()?.chanceOfRain}%",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(start = 10.dp, bottom = 25.dp)
                            )
                            Text(
                                text = "${selectedCity.toDay()?.getCurrentHourMeteo()?.temperature}°",
                                style = MaterialTheme.typography.h4,
                                modifier = Modifier.padding(start = 20.dp, bottom = 30.dp)
                            )
                        }

                        // Image Section
                        Image(
                            painter = painterResource(selectedCity.toDay()?.getCurrentHourMeteo()?.weatherState() ?:"cloudy.png"),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(0.3f)
                                .size(140.dp) // Increased size for better visibility
                                .align(Alignment.CenterVertically) // Vertically align with the text
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3-Day Forecast Section
                    Text(
                        text = "TODAY'S FORECAST",
                        style = MaterialTheme.typography.h6.copy(color = textcolor),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val  time=java.time.LocalTime.now().hour
                        val t1 = if ( time< 13) 6 else if (time<19) 14 else 19
                        val t2 = if ( time< 13) 9 else if (time<19) 16 else 21
                        val t3 = if ( time< 13) 12 else if (time<19) 18 else 23
                        val s1=selectedCity.toDay()?.getHourMeteo(t1)
                        val s2=selectedCity.toDay()?.getHourMeteo(t2)
                        val s3=selectedCity.toDay()?.getHourMeteo(t3)


                        WeatherForecastCard("${t1}:00", "${s1?.temperature}°", painterResource(s1?.weatherState() ?: "cloudy.png"))
                        WeatherForecastCard("${t2}:00", "${s2?.temperature}°", painterResource(s2?.weatherState() ?: "cloudy.png"))
                        WeatherForecastCard("${t3}:00", "${s3?.temperature}°", painterResource(s3?.weatherState() ?: "cloudy.png"))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // 3-Day Forecast Section
                    Text(
                        text = "3-DAY FORECAST",
                        style = MaterialTheme.typography.h6.copy(color = textcolor),
                        modifier = Modifier
                            .padding(bottom = 8.dp),

                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ForecastRow(day = selectedCity.days[0].name, forecast = selectedCity.days[0].weatherState, temp = "${selectedCity.days[0].minTemperature}/${selectedCity.days[0].maxTemperature}°")
                        ForecastRow(day = selectedCity.days[1].name, forecast = selectedCity.days[1].weatherState, temp = "${selectedCity.days[1].minTemperature}/${selectedCity.days[1].maxTemperature}°")
                        ForecastRow(day = selectedCity.days[2].name, forecast = selectedCity.days[2].weatherState, temp = "${selectedCity.days[2].minTemperature}/${selectedCity.days[2].maxTemperature}°")
                    }
                }
            }
        }
    }

}

@Composable
fun CityCard(city: City,weatherState: String, onClick: () -> Unit, isSelected: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp) // Set a fixed height of 200 dp
            .clickable(onClick = onClick)
            .border(
                width = 4.dp,
                color = if (isSelected) MaterialTheme.colors.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
        backgroundColor = if (!isSelected) MaterialTheme.colors.surface else MaterialTheme.colors.background,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(30.dp)
                .fillMaxWidth(), // Ensures the Row takes the full width of the screen

        verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(weatherState),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight() // Ensure it takes up the height
                    .weight(0.2f) // Make the image take 10% of the row width
                    .wrapContentWidth(Alignment.Start) // Align image to the start
                    .size(100.dp)

            )
            Spacer(modifier = Modifier.width(45.dp)) // Add space between MainContent and WeeklyForecast

            Column (
                modifier = Modifier
                    .weight(2f)
                    .padding(top = 15.dp)
                    .fillMaxHeight(9f) // Ensure the Column takes up full height within the Row

            ){

                Text(
                    text = city.name,
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier
                )

                Text(
                    text = city.time(),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                )
            }

            Text(
                text = "${city.toDay()?.getCurrentHourMeteo()?.temperature}°",
                style = MaterialTheme.typography.h5,
                        modifier = Modifier
                        .weight(1f)
                            .wrapContentWidth(Alignment.End) // Align text to the end of its allocated width
                            .padding(end = 25.dp)

            )
        }
    }
}

@Composable
fun WeatherForecastCard(time: String, temp: String, photoResource: Painter) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = time,
            fontSize = 16.sp,
            modifier = Modifier
        )
        Image(
            painter = photoResource,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 14.dp)

        )
        Text(
            text = temp,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ForecastRow(day: String, forecast: String, temp: String) {
    Box(
        modifier = Modifier
            .height(40.dp)

    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {

            Text(
                text = day,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(0.2f)
            )
            Image(
                painter = painterResource("cloudy.png"),
                contentDescription = null,
                modifier = Modifier
                    .weight(0.2f) // Make the image take 10% of the row width
                    .size(60.dp)

            )
            Text(
                text = forecast,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(0.2f)
            )
            Text(
                text = temp,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(0.2f)
            )
        }
    }

}
