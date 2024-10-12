// File: SnippetService.kt
package austral.ingsis.snippet.service

import austral.ingsis.snippet.factory.SnippetFactory
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.repository.SnippetRepositoryInterface
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SnippetService(
    @Autowired private val snippetRepository: SnippetRepositoryInterface,
    @Autowired private val snippetFactory: SnippetFactory,
    private val restTemplate: RestTemplate,
) {
    fun createSnippet(
        name: String,
        description: String,
        code: String,
        language: String,
        ownerId: Long,
    ): Snippet {
        val snippet = snippetFactory.createSnippet(name, description, code, language, ownerId)
        try {
            val url = "http://localhost:8081/parser/validate"
            val requestBody = mapOf(
                "code" to code,
                "language" to language
            )

            // Hacemos la llamada al servicio Parser
            val response: ResponseEntity<String> = restTemplate.postForEntity(url, requestBody, String::class.java)

            // Lógica basada en la respuesta del servicio remoto (si es necesario)
            if (response.statusCode.is2xxSuccessful) {
                return snippetRepository.save(snippet)
            } else {
                throw Exception("Error al validar el código con el servicio Parser")
            }
        }
        catch (error: Exception) {
            throw error
        }
    }

    fun getAllSnippets(): List<Snippet> {
        return snippetRepository.findAll()
    }

    fun getSnippetById(id: Long): Snippet? {
        return snippetRepository.findById(id).orElse(null)
    }

    @Suppress("LongParameterList")
    fun updateSnippet(
        id: Long,
        name: String,
        description: String,
        code: String,
        language: String,
        ownerId: Long,
    ): Snippet? {
        val snippet = snippetRepository.findById(id).orElse(null) ?: return null
        val updatedSnippet =
            snippet.copy(
                name = name,
                description = description,
                code = code,
                language = language,
                ownerId = ownerId,
            )
        return snippetRepository.save(updatedSnippet)
    }

    fun deleteSnippet(id: Long) {
        snippetRepository.deleteById(id)
    }
}
