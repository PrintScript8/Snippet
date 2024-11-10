package austral.ingsis.snippet.factory

import austral.ingsis.snippet.model.UserRules
import org.springframework.stereotype.Component

@Component
class UserRulesFactory {
    fun buildUserRules(
        language: String,
        userId: Long,
        allRules: List<Long>,
    ): UserRules {
        return UserRules(
            0L,
            userId,
            language,
            allRules,
        )
    }
}
