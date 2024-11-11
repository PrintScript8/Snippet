package austral.ingsis.snippet.controller

import austral.ingsis.snippet.service.RulesService
import jakarta.servlet.http.HttpServletRequest
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

@RestController
@RequestMapping("/actions")
class ActionsController(
    @Autowired private val rulesService: RulesService,
    @Autowired private val clientBuilder: RestClient.Builder,
) {
    private val parserClient = clientBuilder.baseUrl("http://parser-service:8080").build()
    private val logger = LogManager.getLogger(SnippetController::class.java)

    @PutMapping("/format")
    fun formatSnippet(
        @RequestBody action: FormatAction,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val userId = request.getHeader("id").toLong()
        val json = rulesService.getFormatJson(userId)
        parserClient.put().uri("/parser/format")
            .body(FormatRequest(action.code, action.language, action.id.toLong(), json))
            .headers({ headers -> headers.set("id", userId.toString()) })
            .retrieve()
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/type")
    fun getType(request: HttpServletRequest): ResponseEntity<List<FileType>> {
        val userId = request.getHeader("id").toLong()
        logger.info("Returning list of supported file types")
        return ResponseEntity.ok().body(listOf(FileType("printscript", "prs")))
    }
}

data class FileType(val language: String, val extension: String)

data class FormatRequest(val code: String, val language: String, val id: Long, val config: String)

data class FormatAction(val code: String, val language: String, val id: String)
