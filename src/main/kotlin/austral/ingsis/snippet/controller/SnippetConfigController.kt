package austral.ingsis.snippet.controller

import austral.ingsis.snippet.message.RedisMessageEmitter
import austral.ingsis.snippet.model.ConfigType
import austral.ingsis.snippet.model.Rule
import austral.ingsis.snippet.service.AuthService
import austral.ingsis.snippet.service.RulesService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.nio.file.AccessDeniedException

@RestController
@RequestMapping("/snippets/config")
class SnippetConfigController(
    @Autowired private val rulesService: RulesService,
    @Autowired private val messageEmitter: RedisMessageEmitter,
    @Autowired private val authService: AuthService,
) {

    private fun getIdByToken(token: String): String {
        val id: String? = authService.validateToken(token)
        if (id != null) {
            return id
        }
        // error, not authenticated
        throw AccessDeniedException("Could not validate user by it's token")
    }

    @GetMapping("/linting")
    fun getLintingConfig(
        @RequestHeader("Authorization") token: String,
        @RequestParam language: String,
    ): ResponseEntity<List<Rule>> {
        val userId = getIdByToken(token)
        println(language)
        val rules = rulesService.getRules(userId, ConfigType.LINTING)
        return ResponseEntity.ok(rules)
    }

    @GetMapping("/formatting")
    fun getFormattingConfig(
        @RequestHeader("Authorization") token: String,
        @RequestParam language: String,
    ): ResponseEntity<List<Rule>> {
        val userId = getIdByToken(token)
        val rules = rulesService.getRules(userId, ConfigType.FORMATTING)
        println(language)
        return ResponseEntity.ok(rules)
    }

    @PutMapping("/formatting")
    fun updateFormattingRules(
        @RequestHeader("Authorization") token: String,
        @RequestBody rules: List<Rule>,
        @RequestParam language: String,
    ) {
        val userId = getIdByToken(token)
        val jsonRules = rulesService.updateRules(userId, language, ConfigType.FORMATTING, rules)
        // Tener en cuenta que la linea de arriba tiene que devolver la
        messageEmitter.publishEvent(token, language, jsonRules, "format")
        // todo: Manejar el publicar a redis
    }

    @PutMapping("/linting")
    fun updateLintingRules(
        @RequestHeader("Authorization") token: String,
        @RequestBody rules: List<Rule>,
        @RequestParam language: String,
    ) {
        val userId = getIdByToken(token)
        val jsonRules = rulesService.updateRules(userId, language, ConfigType.LINTING, rules)
        messageEmitter.publishEvent(token, language, jsonRules, "lint")
    }

    @PutMapping("/initialize/{userId}")
    fun initializeRules(
        @PathVariable userId: String,
        @RequestParam language: String,
    ) {
        rulesService.createRules(userId, language)
    }
}
