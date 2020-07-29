package pro.torben.hafacade.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import pro.torben.hafacade.HARequestFilter
import javax.ws.rs.*


@Path("states")
@RegisterRestClient()
@RegisterClientHeaders(HARequestFilter::class)
interface WeatherClient {

  companion object {
    const val WEATHER_ENTITY = "weather.weather"
  }

  @GET
  @Path("/$WEATHER_ENTITY")
  @Produces("application/json")
  fun getWeather(): Weather

}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Weather(
    val attributes: WeatherAttributes? = null,
    val state: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherAttributes(
    var humidity: Int? = null,
    var pressure: Int? = null,
    var temperature: Double? = null
)