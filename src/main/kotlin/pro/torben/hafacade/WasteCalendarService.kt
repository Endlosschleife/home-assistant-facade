package pro.torben.hafacade

import org.eclipse.microprofile.rest.client.inject.RestClient
import org.slf4j.LoggerFactory
import pro.torben.hafacade.apiclient.CalendarClient
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.Period
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.validation.constraints.Null

@ApplicationScoped
class WasteCalendarService {

  @Inject
  @RestClient
  lateinit var calendarClient: CalendarClient

  @Inject
  lateinit var haFacadeProperties: HaFacadeProperties

  companion object {
    val logger = LoggerFactory.getLogger(WasteCalendarService::class.simpleName)
  }

  fun getWasteCalendar(): Collection<WasteCalendarItem> {

    val events = calendarClient.getCalendar(
        entityId = haFacadeProperties.wasteBinCalendar.entityName,
        start = OffsetDateTime.now().minusDays(haFacadeProperties.wasteBinCalendar.bygoneDays),
        end = OffsetDateTime.now().plusDays(haFacadeProperties.wasteBinCalendar.daysToCome)
    )

    // find next date per wastebin
    val wasteCalendar = mutableMapOf<WasteBin, WasteCalendarItem>()
    events.forEach { event ->
      val wasteBin = WasteBin.fromCalendarValue(event.summary)
      wasteCalendar.putIfAbsent(wasteBin, WasteCalendarItem(
          name = wasteBin.value,
          date = event.start.dateTime!!.toLocalDate(),
          dateString = getDateString(event.start.dateTime!!.toLocalDate())
      ))
    }

    return wasteCalendar.values
  }

  private fun getDateString(date: LocalDate): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val tomorrow = today.plusDays(1)
    return when (date) {
      today -> "heute"
      yesterday -> "gestern"
      tomorrow -> "morgen"
      else -> "${Period.between(today, date).days} Tage"
    }
  }

}

enum class WasteBin(val calendarValue: String, val value: String) {
  BIO("Bioabfall", "Bio"),
  YELLOW("Gelber Sack/Tonne", "Gelb"),
  RESIDUAL_WASTE("Restabfall", "Rest"),
  PAPER("Altpapier", "Papier"),
  UNKNOWN("unknown", "unknown");

  companion object {
    fun fromCalendarValue(value: String): WasteBin {
      for (v in values()) {
        if (v.calendarValue == value) {
          return v
        }
      }
      return UNKNOWN
    }
  }
}

data class WasteCalendarItem(
    val name: String,
    val date: LocalDate,
    val dateString: String
)