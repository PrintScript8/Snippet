package austral.ingsis.snippet.factory

import austral.ingsis.snippet.model.ConfigType
import austral.ingsis.snippet.model.UserRules
import org.springframework.stereotype.Component

@Component
class UserRulesFactory {
    fun buildUserRules(
        language: String,
        userId: Long,
        formatting: List<Long>,
        linting: List<Long>,
    ): UserRules {
        return UserRules(
            0L,
            userId,
            language,
            formatting,
            linting,
        )
    }
}
