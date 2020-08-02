package pro.torben.hafacade.dashboard

import org.eclipse.microprofile.rest.client.inject.RestClient
import pro.torben.hafacade.calendar.CalendarClient
import pro.torben.hafacade.weather.Weather
import pro.torben.hafacade.weather.WeatherClient
import java.time.*
import java.time.ZoneOffset.UTC
import java.time.format.TextStyle
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class DashboardService {

  @Inject
  @RestClient
  lateinit var calendarClient: CalendarClient

  @Inject
  @RestClient
  lateinit var weatherClient: WeatherClient

  fun getDashboard(): Dashboard {
    val calendarItems = calendarClient.getCalendar(
        "calendar.familienkalender",
        OffsetDateTime.now(),
        OffsetDateTime.now().plusDays(7)
    ).map {
      DashboardCalendarEvent(
          title = it.summary,
          start = it.start.dateTime,
          end = it.end.dateTime
      )
    }

    val weather = weatherClient.getWeather().toDashboardWeather()

    val currentDate = OffsetDateTime.now()

    return Dashboard(
        events = calendarItems,
        weather = weather,
        currentDate = DashboardCurrentDate(
            day = currentDate.dayOfMonth.toString(),
            dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.GERMAN),
            month = currentDate.month.getDisplayName(TextStyle.FULL, Locale.GERMAN)
        )
    )
  }

}

data class Dashboard(
    val events: Collection<DashboardCalendarEvent>,
    val weather: DashboardWeather,
    val currentDate: DashboardCurrentDate
)

data class DashboardCalendarEvent(
    val title: String,
    val start: OffsetDateTime?,
    val end: OffsetDateTime?
)

data class DashboardWeather(
    val temperature: Double?,
    val humidity: Int?,
    val pressure: Int?,
    val condition: WeatherCondition,
    val conditionText: String,
    val forecast: Collection<DashboardWeatherForecastItem>
)

enum class WeatherCondition(val homeAssistantValue: String, val value: String) {
  CLEAR_NIGHT("clear-night", "Klare Nacht"),
  CLOUDY("cloudy", "Wolkig"),
  FOG("fog", "Nebel"),
  HAIL("hail", "Hagel"),
  LIGHTNING("lightning", "Blitze"),
  LIGHTNING_RAINY("ligthning-rainy", "Gewitter"),
  PARTLY_CLOUDY("partlycloudy", "Leicht wolkig"),
  POURING("pouring", "Starkregen"),
  RAINY("rainy", "Regen"),
  SNOWY("snowy", "Schnee"),
  SNOWY_RAINY("snowy-rainy", "Schneeregen"),
  SUNNY("sunny", "Sonnig"),
  WINDY("windy", "Windig"),
  WINDY_VARIANT("windy-variant", "Windig"),
  EXCEPTIONAL("exceptional" , "?");

  companion object {
    fun fromHAValue(value: String): WeatherCondition {
      for (v in values()) {
        if (v.homeAssistantValue == value) {
          return v
        }
      }
      return EXCEPTIONAL;
    }
  }
}

data class DashboardWeatherForecastItem(
    val condition: WeatherCondition?,
    val conditionText: String?,
    val temperature: Double?,
    val datetime: LocalDateTime?
)

data class DashboardCurrentDate(
    val dayOfWeek: String,
    val day: String,
    val month: String
)

fun Weather.toDashboardWeather(): DashboardWeather = DashboardWeather(
    temperature = this.attributes?.temperature,
    humidity = this.attributes?.humidity,
    pressure = this.attributes?.pressure,
    condition = WeatherCondition.fromHAValue(this.state!!),
    conditionText = WeatherCondition.fromHAValue(this.state!!).value,
    forecast = this.attributes?.forecast?.take(4)?.map {
      DashboardWeatherForecastItem(
          condition = WeatherCondition.fromHAValue(it.condition!!),
          conditionText = WeatherCondition.fromHAValue(it.condition!!).value,
          temperature = it.temperature,
          datetime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it?.datetime ?: 0), ZoneId.systemDefault())
      )
    } ?: emptyList()
)