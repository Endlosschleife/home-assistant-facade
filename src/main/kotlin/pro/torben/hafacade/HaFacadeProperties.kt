package pro.torben.hafacade

import io.quarkus.arc.config.ConfigProperties

@ConfigProperties(prefix = "hafacade")
class HaFacadeProperties {
  lateinit var calendar: CalendarProperties
}

class CalendarProperties {
  lateinit var entityName: String
}