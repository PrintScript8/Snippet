package austral.ingsis.snippet.factory

import austral.ingsis.snippet.model.UserRules
import org.springframework.stereotype.Component

@Component
class UserRulesFactory {
    fun buildUserRules(
        userId: Long,
        language: String,
        lintingConfig: String,
        formattingConfig: String,
    ): UserRules {
        return UserRules(
            userId = userId,
            language = language,
            lintingConfig = lintingConfig,
            formattingConfig = formattingConfig,
        )
    }
}
