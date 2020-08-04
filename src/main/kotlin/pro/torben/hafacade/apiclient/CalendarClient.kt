package pro.torben.hafacade.apiclient

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import pro.torben.hafacade.HARequestFilter
import java.time.LocalDate
import java.time.OffsetDateTime
import javax.ws.rs.*


@Path("calendars")
@RegisterRestClient(configKey = "home-assistant")
@RegisterClientHeaders(HARequestFilter::class)
interface CalendarClient {

  @GET
  @Path("/{entityId}")
  @Produces("application/json")
  fun getCalendar(@PathParam("entityId") entityId: String,
                  @QueryParam("start") start: OffsetDateTime,
                  @QueryParam("end") end: OffsetDateTime): Collection<CalendarItem>

}

@JsonIgnoreProperties(ignoreUnknown = true)
data class CalendarItem(
    val description: String?,
    val summary: String,
    val start: DateTime,
    val end: DateTime
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DateTime(
    var dateTime: OffsetDateTime? = null,
    val date: LocalDate? = null
)