package austral.ingsis.snippet.controller

import austral.ingsis.snippet.service.AuthService
import austral.ingsis.snippet.service.RulesService
import jakarta.servlet.http.HttpServletRequest
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import java.nio.file.AccessDeniedException

@RestController
@RequestMapping("/actions")
class ActionsController(
    @Autowired private val rulesService: RulesService,
    @Autowired private val clientBuilder: RestClient.Builder,
    @Autowired private val authService: AuthService,
) {
    private val parserClient = clientBuilder.baseUrl("http://parser-service:8080").build()
    private val logger = LogManager.getLogger(SnippetController::class.java)

    private fun getIdByToken(token: String): String {
        val id: String? = authService.validateToken(token)
        if (id != null) {
            return id
        }
        // error, not authenticated
        throw AccessDeniedException("Could not validate user by it's token")
    }

    @PutMapping("/format")
    fun formatSnippet(
        @RequestBody action: FormatAction,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<String> {
        val userId = getIdByToken(token)
        val json = rulesService.getFormatJson(userId)
        parserClient.put().uri("/parser/format")
            .body(FormatRequest(action.code, action.language, action.id.toLong(), json))
            .headers { headers -> headers.set("Authorization", token) }
            .retrieve()
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/type")
    fun getType(
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<List<FileType>> {
        val userId = getIdByToken(token)
        logger.info("Returning list of supported file types")
        return ResponseEntity.ok().body(listOf(FileType("printscript", "prs")))
    }
}

data class FileType(val language: String, val extension: String)

data class FormatRequest(val code: String, val language: String, val id: Long, val config: String)

data class FormatAction(val code: String, val language: String, val id: String)
