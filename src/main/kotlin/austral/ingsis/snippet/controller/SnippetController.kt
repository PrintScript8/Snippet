package austral.ingsis.snippet.controller

import austral.ingsis.snippet.exception.ServiceException
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.service.SnippetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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

    // Upload snippet with txtFile directly from MultipartFile
    @PostMapping("/upload")
    fun uploadSnippet(
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<Any> {
        return try {
            // Leer el contenido del archivo directamente desde MultipartFile
            val content = file.inputStream.bufferedReader().use { it.readText() }

            // Crear el snippet usando el contenido leído
            val createdSnippet = snippetService.createSnippetFromFile(
                name = file.originalFilename ?: "unknown.txt",
                code = content
            )

            ResponseEntity.status(HttpStatus.CREATED).body(createdSnippet)
        } catch (e: ServiceException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to "An unexpected error occurred: ${e.message}"))
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
