package austral.ingsis.snippet.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class ValidationService(
    @Autowired private val clientBuilder: RestClient.Builder,
) {
    private val validationClient = clientBuilder.baseUrl("http://permission-service:8080").build()

    fun canModify(
        userId: Long,
        snippetId: Long,
    ): Boolean {
        return exists(userId) &&
            validationClient.put()
                .uri("/validate/edit/{id}/{snippetId}", userId, snippetId)
                .retrieve()
                .toEntity(Boolean::class.java).body == true
    }

    fun canRead(
        userId: Long,
        snippetId: Long,
    ): Boolean {
        return exists(userId) &&
            validationClient.put()
                .uri("/validate/read/{id}/{snippetId}", userId, snippetId)
                .retrieve()
                .toEntity(Boolean::class.java).body == true
    }

    fun exists(userId: Long): Boolean {
        return validationClient.put()
            .uri("/validate/create/{id}", userId)
            .retrieve()
            .toEntity(Boolean::class.java).body == true
    }

    fun canDelete(
        userId: Long,
        snippetId: Long,
    ): Boolean {
        return exists(userId) &&
            validationClient.put()
                .uri("/validate/delete/{id}/{snippetId}", userId, snippetId)
                .retrieve()
                .toEntity(Boolean::class.java).body == true
    }
}
