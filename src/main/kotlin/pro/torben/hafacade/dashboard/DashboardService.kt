package pro.torben.hafacade.dashboard

import org.eclipse.microprofile.rest.client.inject.RestClient
import pro.torben.hafacade.calendar.CalendarClient
import pro.torben.hafacade.calendar.CalendarItem
import pro.torben.hafacade.weather.Weather
import pro.torben.hafacade.weather.WeatherAttributes
import pro.torben.hafacade.weather.WeatherClient
import java.time.DayOfWeek
import java.time.OffsetDateTime
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

    val weather = weatherClient.getWeather()

    val currentDate = OffsetDateTime.now()

    return Dashboard(
        events = calendarItems,
        weather = weather.toDashboardWeather(),
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
    val condition: String?
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
    condition = this.state
)