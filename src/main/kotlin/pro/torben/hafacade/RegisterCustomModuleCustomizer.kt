package pro.torben.hafacade

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.jackson.ObjectMapperCustomizer
import java.text.SimpleDateFormat
import javax.inject.Singleton

@Singleton
class RegisterCustomModuleCustomizer : ObjectMapperCustomizer {

  override fun customize(mapper: ObjectMapper) {
    mapper.dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
  }

}