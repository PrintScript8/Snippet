package austral.ingsis.snippet.service

import austral.ingsis.snippet.server.CorrelationIdInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class ValidationService(
    @Autowired private val clientBuilder: RestClient.Builder,
) {
    private val interceptor = CorrelationIdInterceptor()
    private val validationClient =
        clientBuilder.baseUrl("http://permission-service:8080")
            .requestInterceptor(interceptor).build()

    fun canModify(
        snippetId: Long,
        token: String,
    ): Boolean {
        return exists(token) &&
            validationClient.put()
                .uri("/validate/edit/{snippetId}", snippetId)
                .headers { headers -> headers.set("Authorization", token) }
                .retrieve()
                .toEntity(Boolean::class.java).body == true
    }

    fun canRead(
        snippetId: Long,
        token: String,
    ): Boolean {
        return exists(token) &&
            validationClient.put()
                .uri("/validate/read/{snippetId}", snippetId)
                .headers { headers -> headers.set("Authorization", token) }
                .retrieve()
                .toEntity(Boolean::class.java).body == true
    }

    fun exists(token: String): Boolean {
        return validationClient.put()
            .uri("/validate/create")
            .headers { headers -> headers.set("Authorization", token) }
            .retrieve()
            .toEntity(Boolean::class.java).body == true
    }

    fun canDelete(
        snippetId: Long,
        token: String,
    ): Boolean {
        return exists(token) &&
            validationClient.put()
                .uri("/validate/delete/{snippetId}", snippetId)
                .headers { headers -> headers.set("Authorization", token) }
                .retrieve()
                .toEntity(Boolean::class.java).body == true
    }
}
