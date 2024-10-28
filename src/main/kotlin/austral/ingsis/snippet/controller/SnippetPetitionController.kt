package austral.ingsis.snippet.controller

import austral.ingsis.snippet.exception.InvalidSnippetException
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.service.SnippetService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import kotlin.math.absoluteValue

@RestController
@RequestMapping("/snippets")
class SnippetPetitionController(
    @Autowired private val snippetService: SnippetService,
    @Autowired private val clientBuilder: RestClient.Builder,
) {
    var permissionClient: RestClient = clientBuilder.baseUrl("http://permission-service:8080").build()
    private val logger = LoggerFactory.getLogger(SnippetPetitionController::class.java)

    @GetMapping("/{id}", produces = ["application/json"])
    fun getSnippetById(
        @PathVariable id: Long,
    ): ResponseEntity<Snippet?> {
        val snippet: Snippet? = snippetService.getSnippetById(id)
        return if (snippet != null) {
            ResponseEntity.status(HttpStatus.OK).body(snippet)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping
    fun createSnippet(
        @RequestBody snippet: Snippet,
    ): ResponseEntity<Long> {
        val id: Long = (snippet.name + snippet.code + snippet.description).hashCode().toLong().absoluteValue
        try {
            snippetService.updateSnippet(
                id,
                snippet.name,
                snippet.description,
                snippet.code,
                snippet.language,
                snippet.ownerId,
            )
            permissionClient
                .put()
                .uri("/users/snippets/{id}/{snippetId}", snippet.ownerId, id)
                .retrieve()
                .toBodilessEntity()
            return ResponseEntity.status(HttpStatus.CREATED).body(id)
        } catch (e: InvalidSnippetException) {
            logger.error("Error fetching snippet by id: $id", e)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @PutMapping("/{id}")
    fun updateSnippet(
        @PathVariable id: Long,
        @RequestBody snippet: Snippet,
    ): ResponseEntity<Void> {
        try {
            snippetService.updateSnippet(
                id,
                snippet.name,
                snippet.description,
                snippet.code,
                snippet.language,
                snippet.ownerId,
            )
            return ResponseEntity.status(HttpStatus.OK).build()
        } catch (e: InvalidSnippetException) {
            logger.error("Error fetching snippet by id: $id", e)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteSnippet(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        val snippet: Snippet? = snippetService.getSnippetById(id)
        if (snippet != null) {
            permissionClient.delete().uri("/users/snippets/$snippet.ownerId/$id").retrieve()
        }
        snippetService.deleteSnippet(id)
        return ResponseEntity.status(HttpStatus.OK).build()
    }
}
