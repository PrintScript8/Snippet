package austral.ingsis.snippet.controller

import austral.ingsis.snippet.exception.ServiceException
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.service.SnippetService
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

@RestController
@RequestMapping("/snippets")
class SnippetController(
    @Autowired private val snippetService: SnippetService,
) {
    @PostMapping
    @Suppress("SwallowedException")
    fun createSnippet(
        @RequestBody snippet: Snippet,
    ): ResponseEntity<Snippet> {
        return try {
            val createdSnippet =
                snippetService.createSnippet(
                    snippet.name,
                    snippet.description,
                    snippet.code,
                    snippet.language,
                    snippet.ownerId,
                    snippet.config,
                )
            ResponseEntity.status(HttpStatus.CREATED).body(createdSnippet)
        } catch (e: ServiceException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @GetMapping
    fun getAllSnippets(): List<Snippet> {
        return snippetService.getAllSnippets()
    }

    @GetMapping("/{id}")
    fun getSnippetById(
        @PathVariable id: Long,
    ): Snippet? {
        return snippetService.getSnippetById(id)
    }

    @PutMapping("/{id}")
    fun updateSnippet(
        @RequestBody snippet: Snippet,
        @PathVariable id: Long,
    ): Snippet? {
        return snippetService.updateSnippet(
            id,
            snippet.name,
            snippet.description,
            snippet.code,
            snippet.language,
            snippet.ownerId,
            snippet.config,
        )
    }

    @DeleteMapping("/{id}")
    fun deleteSnippet(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        snippetService.deleteSnippet(id)
        return ResponseEntity.noContent().build() // Retorna 204
    }
}
