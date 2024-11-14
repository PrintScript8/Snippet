package austral.ingsis.snippet.service

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AuthService {
    var restTemplate = RestTemplate()

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun validateToken(token: String): String? {
        return try {
            // Prepare headers with the Authorization token
            val headers = HttpHeaders()
            headers.set("Authorization", token)
            val requestEntity = HttpEntity<String>(headers)

            // Send request to authorization service
            val response =
                restTemplate.exchange(
                    "http://authorization-service:8080/authorize/auth0",
                    HttpMethod.POST,
                    requestEntity,
                    Map::class.java,
                )

            // Check response status
            return when (response.statusCode) {
                HttpStatus.UNAUTHORIZED -> null
                HttpStatus.OK -> {
                    val responseBody = response.body
                    responseBody?.get("id") as? String
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
