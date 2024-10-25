package austral.ingsis.snippet.service

import austral.ingsis.snippet.exception.InvalidSnippetException
import austral.ingsis.snippet.model.Snippet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class SnippetService(
    @Autowired final val restClientBuilder: RestClient.Builder,
) {
    var bucketClient: RestClient = restClientBuilder.baseUrl("http://asset-service:8080").build()
    var parserClient: RestClient = restClientBuilder.baseUrl("http://parser-service:8081").build()

    fun getSnippetById(id: Long): Snippet? {
        return bucketClient.get()
            .uri("/v1/asset/{container}/{key}", "snippet", id)
            .retrieve()
            .body(Snippet::class.java)
    }

    @Suppress("LongParameterList")
    fun updateSnippet(
        id: Long,
        name: String,
        description: String,
        code: String,
        language: String,
        ownerId: Long,
    ) {
        val updatedSnippet = Snippet(id, name, description, code, language, ownerId)
        val result = parserClient.post()
            .uri("/parser/validate")
            .body(updatedSnippet)
            .retrieve()
            .toBodilessEntity()

        if (result.statusCode.is2xxSuccessful) {
            bucketClient.put()
                .uri("/v1/asset/{container}/{key}", "snippet", id)
                .body(updatedSnippet)
                .retrieve()
                .body(Snippet::class.java)
        }
        else {
            throw InvalidSnippetException("Invalid snippet")
        }
    }

    fun deleteSnippet(id: Long) {
        bucketClient.delete()
            .uri("/v1/asset/{container}/{key}", "snippet", id)
            .retrieve()
            .body(Void::class.java)
    }
}
