package austral.ingsis.snippet.controller

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
        val id: Long = (snippet.name + snippet.code + snippet.description).hashCode().toLong()
        snippetService.updateSnippet(
            id,
            snippet.name,
            snippet.description,
            snippet.code,
            snippet.language,
            snippet.ownerId,
            snippet.config,
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(id)
    }

    @PutMapping("/{id}")
    fun updateSnippet(
        @PathVariable id: Long,
        @RequestBody snippet: Snippet,
    ): ResponseEntity<Void> {
        snippetService.updateSnippet(
            id,
            snippet.name,
            snippet.description,
            snippet.code,
            snippet.language,
            snippet.ownerId,
            snippet.config,
        )
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @DeleteMapping("/{id}")
    fun deleteSnippet(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        snippetService.deleteSnippet(id)
        return ResponseEntity.status(HttpStatus.OK).build()
    }
}
