package pro.torben.hafacade

import io.quarkus.arc.config.ConfigProperties

@ConfigProperties(prefix = "hafacade")
class HaFacadeProperties {
  lateinit var calendar: CalendarProperties
  lateinit var wasteBinCalendar: WasteBinCalendarProperties
}

class CalendarProperties {
  lateinit var entityName: String
  var maxEventsToday: Int = 0
  var maxEventsTomorrow: Int = 0
}

class WasteBinCalendarProperties {
  lateinit var entityName: String
  var bygoneDays: Long = 0
  var daysToCome: Long = 0
}