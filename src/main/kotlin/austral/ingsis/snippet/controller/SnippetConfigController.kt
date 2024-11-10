package austral.ingsis.snippet.controller

import austral.ingsis.snippet.message.RedisMessageEmitter
import austral.ingsis.snippet.model.ConfigType
import austral.ingsis.snippet.model.Rule
import austral.ingsis.snippet.service.RulesService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippets/config")
class SnippetConfigController(
    @Autowired private val rulesService: RulesService,
    @Autowired private val messageEmitter: RedisMessageEmitter,
) {
    @GetMapping("/linting")
    fun getLintingConfig(
        request: HttpServletRequest,
        @RequestParam language: String,
    ): ResponseEntity<List<Rule>> {
        val userId = request.getHeader("id").toLong()
        val rules = rulesService.getRules(userId, ConfigType.LINTING)
        println("Requested linting config for language: $language")
        return ResponseEntity.ok(rules)
    }

    @GetMapping("/formatting")
    fun getFormattingConfig(
        request: HttpServletRequest,
        @RequestParam language: String,
    ): ResponseEntity<List<Rule>> {
        val userId = request.getHeader("id").toLong()
        val rules = rulesService.getRules(userId, ConfigType.FORMATTING)
        println("Requested formatting config for language: $language")
        return ResponseEntity.ok(rules)
    }

    @PutMapping("/formatting")
    fun updateFormattingRules(
        request: HttpServletRequest,
        @RequestBody rules: List<Rule>,
        @RequestParam language: String,
    ) {
        val userId = request.getHeader("id").toLong()
        val jsonRules = rulesService.updateRules(userId, language, ConfigType.FORMATTING, rules)
        // Tener en cuenta que la linea de arriba tiene que devolver la
        messageEmitter.publishEvent(userId, language, jsonRules, "format")
        // todo: Manejar el publicar a redis
    }

    @PutMapping("/linting")
    fun updateLintingRules(
        request: HttpServletRequest,
        @RequestBody rules: List<Rule>,
        @RequestParam language: String,
    ) {
        val userId = request.getHeader("id").toLong()
        val jsonRules = rulesService.updateRules(userId, language, ConfigType.LINTING, rules)
        messageEmitter.publishEvent(userId, language, jsonRules, "lint")
    }

    @PutMapping("/initialize/{userId}")
    fun initializeRules(
        @PathVariable userId: Long,
        @RequestParam language: String,
    ) {
        rulesService.createRules(userId, language)
    }
}
