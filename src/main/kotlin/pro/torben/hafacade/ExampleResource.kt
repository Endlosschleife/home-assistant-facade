package pro.torben.hafacade

import org.eclipse.microprofile.rest.client.inject.RestClient
import pro.torben.hafacade.calendar.CalendarClient
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/hello")
class ExampleResource {

  @Inject
  @RestClient
  lateinit var calendarClient: CalendarClient

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  fun hello(): Response {
    val calendarItems = calendarClient.getCalendar("calendar.familienkalender",
        OffsetDateTime.now(), OffsetDateTime.now().plusDays(7))
    return Response.ok(calendarItems).build()
  }
}