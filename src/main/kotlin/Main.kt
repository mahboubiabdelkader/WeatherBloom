import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import weatherBloom.*


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HoverableClickableText(
    text: String,
    normalColor: Color,
    hoverColor: Color,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = if (isHovered) hoverColor else normalColor,
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier
            .pointerMoveFilter(
                onEnter = {
                    isHovered = true
                    true
                },
                onExit = {
                    isHovered = false
                    true
                }
            )
            .clickable { onClick() }
    )
}

val hoverColor = Color(0xFF6E7584).copy(alpha = 0.5f) // 50% opacity
val textcolor = Color(0xFF6E7584)


@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = darkColors(
            primary = Color(0xFF1A237E),
            primaryVariant = Color(0xFF303F9F),
            secondary = Color(0xFF1DE9B6),
            onPrimary = Color.White,
            onSecondary = Color.Black,
            background = Color(0xFF131A26)
            ,
            surface =  Color(0xFF6E7584).copy(alpha = 0.28f), // 50% transparency
            onBackground = Color.White,
            onSurface = Color.White,
        ),
        typography = Typography(
            defaultFontFamily = FontFamily.Default,
            body1 = TextStyle(
                color = Color.White, // Text color for body1
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
            ),
            h1 = TextStyle(
                color = Color.White, // Text color for h1
                fontSize = 96.sp,
                fontWeight = FontWeight.Light
            ),
            h2 = TextStyle(
                color = Color.White, // Text color for h2
                fontSize = 60.sp,
                fontWeight = FontWeight.Light
            ),
            h4 = TextStyle(
                color = Color.White, // Text color for h4
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            ),
            h6 = TextStyle(
                color = Color.White, // Text color for h6
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            h5 = TextStyle(
                color = Color.White, // Text color for h6
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium
            ),
            subtitle1 = TextStyle(
                color = Color.White, // Text color for subtitle1
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            subtitle2 = TextStyle(
                color = Color.White, // Text color for subtitle1
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            // Add other text styles as needed
        ),
        shapes = Shapes(), // Optional: Use default or custom shapes
        content = content
    )
}


@Composable
fun WeatherApp(weatherViewModel: WeatherViewModel) {
    var weatherData by remember { mutableStateOf<List<City>>(emptyList()) }
    var selectedCity by remember { mutableStateOf<City?>(null) }
    var showCitySelection by remember { mutableStateOf(false) }  // Boolean to toggle the city selection screen
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch {
            weatherData = weatherViewModel.loadCSVData("weather_data.csv")
            selectedCity = weatherViewModel.defaultCity

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(25.dp)
    ) {

            // Main weather app screen
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Sidebar(
                    onCitiesClick = { showCitySelection = true },
                    onWeatherClick= { showCitySelection = false },
                    ) // Pass function to handle the click event of Cities

                Spacer(modifier = Modifier.width(25.dp)) // Add space between Sidebar and MainContent
                if (showCitySelection) {
                    // If showCitySelection is true, show the city selection screen
                    CitySelectionScreen(
                        weatherData, onCitySelected = { city ->
                        selectedCity = city
                        showCitySelection = false  }, // Close the city selection screen
                        selectedCity
                    ) // Pass the currently selected city)
                } else {
                // Main content area takes most space
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(2f) // Main content takes more space
                ) {
                    MainContent(selectedCity,
                         showCitySelection)
                }

                Spacer(modifier = Modifier.width(25.dp)) // Add space between MainContent and WeeklyForecast

                // Weekly forecast takes less space than MainContent
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f) // WeeklyForecast takes smaller space than MainContent but larger than Sidebar
                ) {
                    WeeklyForecast(selectedCity)
                }
            }
        }
    }
}


@Composable
fun Sidebar(
    onCitiesClick: () -> Unit,
    onWeatherClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth(Alignment.Start)

    ) {
        Column(
            modifier = Modifier
                .width(85.dp)
                .align(Alignment.Center)
                .fillMaxHeight(0.9f)
                .background(MaterialTheme.colors.surface, RoundedCornerShape(8.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource("app_icon.png"),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(top=15.dp, bottom = 25.dp)

            )
            Spacer(modifier = Modifier.height(38.dp))

            Image(
                painter = painterResource("weather.png"),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)

            )
            HoverableClickableText(
                text = "Weather",
                normalColor = MaterialTheme.colors.onSurface,
                hoverColor = hoverColor,
                onClick = onWeatherClick
            )

            Spacer(modifier = Modifier.height(22.dp))

// Cities button
            Image(
                painter = painterResource("liste.png"),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)

            )
            HoverableClickableText(
                text = "Cities",
                normalColor = MaterialTheme.colors.onSurface,
                hoverColor = hoverColor,
                onClick = onCitiesClick
            )
            Spacer(modifier = Modifier.height(22.dp))

            Image(
                painter = painterResource("settings.png"),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)

            )
            Text(
                text = "Settings",
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Composable
fun MainContent(selectedCity: City?,    showCitySelection: Boolean // Pass the mutable state for showing city selection // Pass the state directly
) {
    selectedCity?.let { city ->
        val datatoday = city.toDay()
        val datanow = datatoday?.getCurrentHourMeteo()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            SearchButton(showCitySelection )

            Spacer(modifier = Modifier.height(16.dp))

            CityWeatherDisplay(city, datanow)

            Spacer(modifier = Modifier.height(16.dp))

            HourlyForecast(city)

            Spacer(modifier = Modifier.height(16.dp))

            AirConditions(city)
        }
    }
}

@Composable
fun SearchButton(showCitySelection: Boolean) {
    Button(
        onClick = {   },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
        ),
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(start = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text("Search for cities")
        }
    }
}



@Composable
fun CityWeatherDisplay(city: City, datanow: MeteoData?) {
    Text(
        text = city.name,
        style = MaterialTheme.typography.h4,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        "Chance of rain: ${datanow?.chanceOfRain}%",
        style = MaterialTheme.typography.subtitle1
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "${datanow?.temperature}°",
        style = MaterialTheme.typography.h2,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun HourlyForecast(city: City) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface, RoundedCornerShape(8.dp))
            .padding(bottom = 30.dp , top = 16.dp)
    ) {
        Text(
            text = "Today's Forecast",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 20.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left navigation button as IconButton
            IconButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, // Use an arrow or appropriate icon
                    contentDescription = "Scroll to First",
                    tint = MaterialTheme.colors.onSurface

                )
            }

            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(25.dp),
                state = listState
            ) {
                val weatherPhoto = city.toDay()
                items(
                    city.days.firstOrNull()?.hourlyData?.toList() ?: emptyList()
                ) { (hour, meteoData) ->
                    HourlyCard(
                        hour = hour,
                        photoResource = painterResource(
                            weatherPhoto?.getHourMeteo(hour)?.weatherState() ?: "default_icon"
                        ),
                        meteoData = meteoData
                    )
                }
            }

            // Right navigation button as IconButton
            IconButton(
                onClick = {
                    scope.launch {
                        val lastIndex = city.days.firstOrNull()?.hourlyData?.size?.minus(1) ?: 0
                        listState.animateScrollToItem(lastIndex)
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward, // Use an arrow or appropriate icon
                    contentDescription = "Scroll to Last",
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}



@Composable
fun HourlyCard(hour: Int, photoResource: Painter, meteoData: MeteoData) {
    Column(
        modifier = Modifier
            .width(70.dp) // Adjust width to match the picture
            .padding(vertical = 8.dp), // Space between the cards
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Time Display
        Text(
            text = "$hour:00",
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface
        )

        // Weather Icon Placeholder
      /*  Icon(
            imageVector = Icons.Default.AccountCircle, // Replace with actual weather icons
            contentDescription = "Weather Icon",
            modifier = Modifier
                .size(36.dp)
                .padding(vertical = 4.dp), // Space around the icon
            tint = MaterialTheme.colors.onSurface
        )*/
        Image(
            painter = photoResource,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 14.dp)

        )

        // Temperature Display
        Text(
            text = "${meteoData.temperature}°",
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.h6,



            )
    }
}


@Composable
fun WeeklyForecast(city: City?) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(15.dp)
            .background(MaterialTheme.colors.surface, RoundedCornerShape(8.dp)) // Box avec un fond coloré
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart) // Alignement à gauche
                .fillMaxWidth()
                .padding(16.dp)

        ) {
            Text(
                text = "7-day forecast",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                city?.days?.forEach { day ->
                    WeeklyCard(
                        day=day
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyCard(day: Day) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp), // Set a fixed height
        backgroundColor = Color.Transparent, // Make the card transparent
        elevation = 0.dp // Remove the shadow
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp) // Add horizontal and vertical padding
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Center elements vertically
        ) {
            var dayName = day.name.take(3) // Take the first 3 letters of the day
            if (day.isToday()) {
                dayName = "Today" // Display "Today" if it's today
            }

            // Day name
            Text(
                text = dayName,
                style = MaterialTheme.typography.subtitle1.copy(color = Color.White),
                modifier = Modifier.weight(1f) // Left space for other elements
            )

            // Weather Icon
            Image(
                painter = painterResource(day.weatherPhoto()),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp) // Adjusted icon size for better consistency
                    .padding(end = 8.dp) // Padding to create space
                    .weight(1f)
            )

            // Weather state
            Text(
                text = day.weatherState,
                style = MaterialTheme.typography.subtitle1.copy(color = Color.White),
                modifier = Modifier.weight(2f)
            )

            // Temperature
            Text(
                text = "${day.minTemperature}/${day.maxTemperature}°",
                style = MaterialTheme.typography.subtitle1.copy(color = Color.White),
                modifier = Modifier
                    .wrapContentWidth(Alignment.End) // Align temperature to the end

            )
        }
    }
}

@Composable
fun AirConditions(city: City) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            // Header
            Text(
                text = "Air Conditions",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Row for Air Condition Details
            Row(
                modifier = Modifier
                    .padding(top= 40.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ColumnAirCondition(
                    value = "${city.toDay()?.getCurrentHourMeteo()?.chanceOfRain ?: 0}%",
                    icon = "goutte_eau.png",
                    label = "Chance of rain"
                )
                ColumnAirCondition(
                    value = "${city.toDay()?.getCurrentHourMeteo()?.wind ?: 0} km/h",
                    icon = "windy.png",
                    label = " Wind"
                )
                ColumnAirCondition(
                    value = "${city.toDay()?.getCurrentHourMeteo()?.temperature ?: 0}°",
                    icon = "temp.png",
                    label = "Temperature"
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 100.dp, bottom = 25.dp)

                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ColumnAirCondition(
                    value = "${city.toDay()?.getCurrentHourMeteo()?.chanceOfRain ?: 0}%",
                    icon = "humidity.png",
                    label = "Humidity"
                )
                ColumnAirCondition(
                    value = city.toDay()?.sunrise?:"",
                    icon = "sunrise.png",
                    label = " Sunrise"
                )
                ColumnAirCondition(
                    value = city.toDay()?.sunset?:"",
                    icon = "sundown.png",
                    label = "Sundown"
                )
            }
        }
    }
}

@Composable
fun ColumnAirCondition(value: String, icon: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        // Icon

        Row(

        ){
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp) // Adjust size to match the design
                    .padding(bottom = 8.dp)
            )
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        }
        // Value
        Text(
            text = value,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}



fun main() = application {
    val weatherViewModel = WeatherViewModel()

    Window(
        onCloseRequest = ::exitApplication,
        title = "WeatherBloom"
    ) {
        AppTheme {

            WeatherApp(weatherViewModel = weatherViewModel)
        }
    }
}