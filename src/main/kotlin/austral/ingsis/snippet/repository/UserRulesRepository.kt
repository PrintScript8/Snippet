package austral.ingsis.snippet.repository

import austral.ingsis.snippet.model.UserRules
import org.springframework.data.jpa.repository.JpaRepository

interface UserRulesRepository : JpaRepository<UserRules, Long> {
    fun findByUserIdAndLanguage(
        userId: String,
        language: String,
    ): UserRules?

    fun findByUserId(userId: String): UserRules?
}
