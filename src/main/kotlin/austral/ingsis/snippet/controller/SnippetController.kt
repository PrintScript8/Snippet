package austral.ingsis.snippet.controller

import austral.ingsis.snippet.exception.InvalidSnippetException
import austral.ingsis.snippet.message.RedisMessageEmitter
import austral.ingsis.snippet.model.CommunicationSnippet
import austral.ingsis.snippet.service.AuthService
import austral.ingsis.snippet.service.SnippetService
import austral.ingsis.snippet.service.ValidationService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import java.nio.file.AccessDeniedException

@RestController
@RequestMapping("/snippets")
class SnippetController(
    @Autowired private val snippetService: SnippetService,
    @Autowired private val clientBuilder: RestClient.Builder,
    @Autowired private val validationService: ValidationService,
    @Autowired private val messageEmitter: RedisMessageEmitter,
    @Autowired private val authService: AuthService,
) {
    private final var permissionClient: RestClient = clientBuilder.baseUrl("http://permission-service:8080").build()
    private val logger = LogManager.getLogger(SnippetController::class.java)

    private fun getIdByToken(token: String): String {
        val id: String? = authService.validateToken(token)
        if (id != null) {
            return id
        }
        // error, not authenticated
        throw AccessDeniedException("Could not validate user by it's token")
    }

    @GetMapping("/{id}")
    fun getSnippetById(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<CommunicationSnippet?> {
        if (!validationService.canRead(id, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
        }
        val snippet: CommunicationSnippet? = snippetService.getSnippetById(id)
        return if (snippet != null) {
            ResponseEntity.status(HttpStatus.OK).body(snippet)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }

    @PostMapping
    fun createSnippet(
        @RequestBody snippet: MessageSnippet,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<Long> {
        val userId = getIdByToken(token)
        if (!validationService.exists(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

        return try {
            val id =
                snippetService.createSnippet(
                    snippet.name,
                    snippet.content,
                    snippet.language,
                    userId,
                    snippet.extension,
                    token,
                )

            permissionClient
                .put()
                .uri("/users/snippets/{snippetId}", id)
                .headers { headers -> headers.set("Authorization", token) }
                .retrieve()
                .toBodilessEntity()

            ResponseEntity.status(HttpStatus.CREATED).body(id)
        } catch (e: InvalidSnippetException) {
            logger.error("Error creating snippet", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @Suppress("ReturnCount")
    @PutMapping("/{id}")
    fun updateSnippet(
        @PathVariable id: Long,
        @RequestBody content: Content,
        @RequestParam language: String,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<Void> {
        if (!validationService.canModify(id, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        try {
            logger.info("Updating snippet with id $id with content $content")
            snippetService.updateSnippet(
                id,
                content.content,
                language,
                token,
            )
            messageEmitter.publishEvent(token, language, "", "execute", id)
            return ResponseEntity.status(HttpStatus.OK).build()
        } catch (e: InvalidSnippetException) {
            logger.error("Error fetching snippet by id: $id", e)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteSnippet(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<String> {
        if (!validationService.canDelete(id, token)) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("User with token $token cannot delete snippet with id $id")
        }
        val snippet: CommunicationSnippet? = snippetService.getSnippetById(id)
        if (snippet != null) {
            permissionClient.delete()
                .uri("/users/snippets/$id")
                .headers { headers -> headers.set("Authorization", token) }
                .retrieve()
        }
        snippetService.deleteSnippet(id)
        return ResponseEntity.status(HttpStatus.OK).body("Snippet with id $id has been deleted")
    }

    @GetMapping("/paginated")
    fun paginatedSnippets(
        page: Int,
        pageSize: Int,
        snippetName: String?,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<PaginationSnippet> {
        if (!validationService.exists(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
        val snippets: List<CommunicationSnippet> =
            snippetService.paginatedSnippets(page, pageSize, snippetName ?: "")
        val paginatedSnippets =
            PaginationSnippet(
                page,
                pageSize,
                snippets.size,
                snippets.filter { validationService.canRead(it.id!!, token) },
            )
        logger.info("Returning paginated snippets: $paginatedSnippets")
        return ResponseEntity.status(HttpStatus.OK).body(paginatedSnippets)
    }

    @PutMapping("/status")
    fun setSnippetStatus(
        @RequestBody setStatus: SetStatus,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<Void> {
        if (!validationService.canModify(setStatus.id, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        snippetService.setSnippetStatus(setStatus.id, setStatus.status)
        return ResponseEntity.status(HttpStatus.OK).build()
    }
}

data class MessageSnippet(
    val name: String,
    val language: String,
    val content: String,
    val extension: String,
)

data class PaginationSnippet(
    val page: Int,
    val pageSize: Int,
    val count: Int,
    val snippets: List<CommunicationSnippet>,
)

data class Content(
    val content: String,
)

data class SetStatus(
    val id: Long,
    val status: String,
)
