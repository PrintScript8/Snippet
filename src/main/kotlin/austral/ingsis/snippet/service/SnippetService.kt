package austral.ingsis.snippet.service

import austral.ingsis.snippet.model.Snippet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class SnippetService(
    @Autowired final val restClient: RestClient.Builder,
) {
    var client: RestClient = restClient.baseUrl("http://asset-service:8080").build()

    fun getSnippetById(id: Long): Snippet? {
        return client.get()
            .uri("/v1/asset/{container}/{key}", "snippet", id)
            .retrieve()
            .body(Snippet::class.java)
    }

    fun updateSnippet(
        id: Long,
        name: String,
        creationDate: String,
    ) {
        val updatedSnippet = Snippet(id, name, creationDate)
        client.put()
            .uri("/v1/asset/{container}/{key}", "snippet", id)
            .body(updatedSnippet)
            .retrieve()
            .body(Snippet::class.java)
    }

    fun deleteSnippet(id: Long) {
        client.delete()
            .uri("/v1/asset/{container}/{key}", "snippet", id)
            .retrieve()
            .body(Void::class.java)
    }
}