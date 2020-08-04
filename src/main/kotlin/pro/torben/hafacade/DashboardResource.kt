package pro.torben.hafacade

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/dashboard")
class DashboardResource {

  @Inject
  lateinit var dashboardService: DashboardService

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  fun getDasboard(): Response {
    return Response.ok(dashboardService.getDashboard()).build()
  }

}