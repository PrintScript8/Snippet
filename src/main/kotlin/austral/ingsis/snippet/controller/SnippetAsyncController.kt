package austral.ingsis.snippet.controller

import austral.ingsis.snippet.message.RedisMessageEmitter
import austral.ingsis.snippet.service.RulesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippets/config")
class SnippetAsyncController(
    @Autowired private val rulesService: RulesService,
    @Autowired private val messageEmitter: RedisMessageEmitter,
) {
    @PutMapping("/linting/{userId}")
    fun updatedLinting(
        @PathVariable userId: Long,
        @RequestParam lintingConfig: String,
        @RequestParam language: String,
    ) {
        rulesService.updateLintingRules(userId, lintingConfig, language)
        messageEmitter.publishEvent(userId, language, lintingConfig, "linting")
    }

    @PutMapping("/formatting/{userId}")
    fun updatedFormatting(
        @PathVariable userId: Long,
        @RequestParam formattingConfig: String,
        @RequestParam language: String,
    ) {
        rulesService.updateFormattingRules(userId, formattingConfig, language)
        messageEmitter.publishEvent(userId, language, formattingConfig, "formatting")
    }
}
