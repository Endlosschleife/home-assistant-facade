package pro.torben.hafacade

import org.eclipse.microprofile.rest.client.inject.RestClient
import pro.torben.hafacade.apiclient.CalendarClient
import pro.torben.hafacade.apiclient.WeatherClient
import pro.torben.hafacade.apiclient.WeatherForecast
import java.time.*
import java.time.format.DateTimeFormatter
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
    val weather = weatherClient.getWeather()
    val dashboardWeather = DashboardWeather(
        temperature = weather.attributes?.temperature,
        humidity = weather.attributes?.humidity,
        pressure = weather.attributes?.pressure,
        condition = WeatherCondition.fromHAValue(weather.state!!),
        conditionText = WeatherCondition.fromHAValue(weather.state!!).value,
        forecast = weather.attributes?.forecast?.filter { isFutureForecast(it) }?.take(4)?.map {
          DashboardWeatherForecastItem(
              condition = WeatherCondition.fromHAValue(it.condition!!),
              conditionText = WeatherCondition.fromHAValue(it.condition!!).value,
              temperature = it.temperature,
              datetime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it?.datetime ?: 0), ZoneId.systemDefault())
          )
        } ?: emptyList()
    )

    val currentDate = OffsetDateTime.now()

    return Dashboard(
        events = buildUpcomingEvents(),
        weather = dashboardWeather,
        currentDate = DashboardCurrentDate(
            day = currentDate.dayOfMonth.toString(),
            dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.GERMAN),
            month = currentDate.month.getDisplayName(TextStyle.FULL, Locale.GERMAN),
            time = currentDate.format(DateTimeFormatter.ofPattern("HH:mm"))
        )
    )
  }

  private fun isFutureForecast(forecastItem: WeatherForecast): Boolean = Instant.ofEpochMilli(forecastItem.datetime
      ?: 0).isAfter(Instant.now())

  private fun buildUpcomingEvents(): Collection<DashboardCalendarGroup> {
    val calendarItems = calendarClient.getCalendar(
        "calendar.familienkalender",
        OffsetDateTime.now(),
        OffsetDateTime.now().plusDays(2)
    ).map {
      DashboardCalendarEvent(
          title = it.summary,
          start = if (it.start.dateTime != null) LocalDateTime.ofInstant(it.start.dateTime, ZoneId.systemDefault()) else null,
          end = if (it.end.dateTime != null) LocalDateTime.ofInstant(it.end.dateTime, ZoneId.systemDefault()) else null,
          startDate = it.start.date ?: LocalDateTime.ofInstant(it.start.dateTime, ZoneId.systemDefault()).toLocalDate(),
          endDate = it.end.date ?:  LocalDateTime.ofInstant(it.end.dateTime, ZoneId.systemDefault()).toLocalDate(),
          isFullDayEvent = it.end.date != null
      )
    }

    val today = OffsetDateTime.now().toLocalDate()
    val tomorrow = OffsetDateTime.now().plusDays(1).toLocalDate()

    val groups = mutableListOf<DashboardCalendarGroup>()

    //  today events
    groups.add(
        DashboardCalendarGroup(
            title = "Heute",
            events = calendarItems
                .filter { it.startDate != null && it.endDate != null }
                .filter { it.startDate!! == today }
        )
    )

    // tomorrow events
    groups.add(
        DashboardCalendarGroup(
            title = "Morgen",
            events = calendarItems
                .filter { it.startDate != null && it.endDate != null }
                .filter { it.startDate!! == tomorrow }
        )
    )

    return groups
  }

}

data class Dashboard(
    val events: Collection<DashboardCalendarGroup>,
    val weather: DashboardWeather,
    val currentDate: DashboardCurrentDate
)

data class DashboardCalendarGroup(
    val title: String,
    val events: Collection<DashboardCalendarEvent>
)

data class DashboardCalendarEvent(
    val title: String,
    val start: LocalDateTime?,
    val end: LocalDateTime?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val isFullDayEvent: Boolean
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
  EXCEPTIONAL("exceptional", "?");

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
    val month: String,
    val time: String
)