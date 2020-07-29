package pro.torben.hafacade

import org.eclipse.microprofile.config.ConfigProvider
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory
import javax.ws.rs.core.MultivaluedMap


class HARequestFilter : ClientHeadersFactory {

  private val token = ConfigProvider.getConfig().getValue("hafacade.home-assistant-auth-token", String::class.java)

  override fun update(inbound: MultivaluedMap<String, String>, outbound: MultivaluedMap<String, String>): MultivaluedMap<String, String> {
    outbound.putSingle("Authorization", "Bearer ${token}")
    return outbound
  }

}