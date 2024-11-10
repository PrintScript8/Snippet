package austral.ingsis.snippet.controller

import austral.ingsis.snippet.model.CommunicationSnippet
import jakarta.servlet.http.HttpServletRequest
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/actions")
class ActionsController {
    private val logger = LogManager.getLogger(SnippetController::class.java)

    @PutMapping("/format")
    fun formatSnippet(
        @RequestBody code: CommunicationSnippet,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val userId = request.getHeader("id").toLong()
        logger.error("Not implemented $code $userId")
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not implemented")
    }

    @GetMapping("/type")
    fun getType(request: HttpServletRequest): ResponseEntity<List<FileType>> {
        val userId = request.getHeader("id").toLong()
        logger.info("Returning list of supported file types $userId")
        return ResponseEntity.ok().body(listOf(FileType("printscript", "prs")))
    }
}

data class FileType(val language: String, val extension: String)
