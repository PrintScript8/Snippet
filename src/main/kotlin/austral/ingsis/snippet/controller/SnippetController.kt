package austral.ingsis.snippet.controller

import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.service.SnippetService
import org.springframework.beans.factory.annotation.Autowired
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
    fun createSnippet(
        @RequestBody snippet: Snippet,
    ): Snippet {
        return snippetService.createSnippet(snippet.name, snippet.creationDate)
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
    ): Snippet? {
        return snippetService.updateSnippet(snippet.id, snippet.name, snippet.creationDate)
    }

    @DeleteMapping("/{id}")
    fun deleteSnippet(
        @PathVariable id: Long,
    ) {
        snippetService.deleteSnippet(id)
    }
}
