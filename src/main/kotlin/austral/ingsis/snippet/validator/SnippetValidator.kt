package austral.ingsis.snippet.validator

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.ConnectException

@Component
class SnippetValidator(private val restTemplate: RestTemplate) {

    fun validateSnippet(code: String, language: String, config: String): Boolean {
        val url = "http://parser-app:8081/parser/validate"


        val requestBody = mapOf(
            "code" to code,
            "language" to language,
            "config" to config
        )

        return try {
            val response: ResponseEntity<String> = restTemplate.postForEntity(url, requestBody, String::class.java)
            response.statusCode.is2xxSuccessful
        } catch (e: ConnectException) {
            throw Exception("Connection refused: Unable to reach the Parser service at $url", e)
        } catch (e: Exception) {
            throw Exception("Error making the request to the Parser service", e)
        }
    }
}
