package austral.ingsis.snippet.controller

import austral.ingsis.snippet.exception.InvalidSnippetException
import austral.ingsis.snippet.model.CommunicationSnippet
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.service.SnippetService
import austral.ingsis.snippet.service.ValidationService
import jakarta.servlet.http.HttpServletRequest
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

@RestController
@RequestMapping("/snippets")
class SnippetController(
    @Autowired private val snippetService: SnippetService,
    @Autowired private val clientBuilder: RestClient.Builder,
    @Autowired private val validationService: ValidationService,
) {
    private final var permissionClient: RestClient = clientBuilder.baseUrl("http://permission-service:8080").build()
    private val logger = LogManager.getLogger(SnippetController::class.java)

    @GetMapping("/{id}")
    fun getSnippetById(
        @PathVariable id: Long,
        request: HttpServletRequest
    ): ResponseEntity<CommunicationSnippet?> {
        val userId = request.getHeader("id").toLong()
        if(!validationService.canRead(userId, id)) {
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
        request: HttpServletRequest
    ): ResponseEntity<Long> {
        val userId = request.getHeader("id").toLong()
        if (!validationService.exists(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
        try {
            val id = snippetService.createSnippet(
                snippet.name,
                snippet.content,
                snippet.language,
                userId,
                snippet.extension
            )
            permissionClient
                .put()
                .uri("/users/snippets/{snippetId}", id)
                .headers { httpHeaders -> httpHeaders.set("id", userId.toString()) }
                .retrieve()
                .toBodilessEntity()
            return ResponseEntity.status(HttpStatus.CREATED).body(id)
        } catch (e: InvalidSnippetException) {
            logger.error("Error creating snippet", e)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @PutMapping("/{id}")
    fun updateSnippet(
        @PathVariable id: Long,
        @RequestBody content: Content,
        @RequestParam language: String,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        val userId = request.getHeader("id").toLong()
        if (!validationService.canModify(userId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        try {
            logger.info("Updating snippet with id $id with content $content")
            snippetService.updateSnippet(
                id,
                content.content,
                language
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
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val userId = request.getHeader("id").toLong()
        if(!validationService.canDelete(userId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User with id $userId cannot delete snippet with id $id")
        }
        val snippet: CommunicationSnippet? = snippetService.getSnippetById(id)
        if (snippet != null) {
            permissionClient.delete().uri("/users/snippets/$snippet.ownerId/$id").retrieve()
        }
        snippetService.deleteSnippet(id)
        return ResponseEntity.status(HttpStatus.OK).body("Snippet with id $id has been deleted")
    }

    @GetMapping("/paginated")
    fun paginatedSnippets(
        page: Int,
        pageSize: Int,
        snippetName: String?,
        request: HttpServletRequest
    ): ResponseEntity<PaginationSnippet> {
        val userId = request.getHeader("id").toLong()
        if (!validationService.exists(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
        val snippets: List<CommunicationSnippet> = snippetService.paginatedSnippets(page, pageSize, snippetName ?: "")
        val paginatedSnippets = PaginationSnippet(page, pageSize, snippets.size, snippets.filter { validationService.canRead(userId, it.id!!) })
        logger.info("Returning paginated snippets: $paginatedSnippets")
        return ResponseEntity.status(HttpStatus.OK).body(paginatedSnippets)
    }

}
data class MessageSnippet(
    val name: String,
    val language: String,
    val content: String,
    val extension: String
)

data class PaginationSnippet(
    val page: Int,
    val pageSize: Int,
    val count: Int,
    val snippets: List<CommunicationSnippet>
)

data class Content(
    val content: String
)