package austral.ingsis.snippet.factory

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserRulesFactoryTest {
    @Test
    fun `test buildUserRules`() {
        val factory = UserRulesFactory()
        val language = "en"
        val userId = 123L
        val allRules = listOf(1L, 2L, 3L)

        val userRules = factory.buildUserRules(language, userId, allRules)

        assertEquals(0L, userRules.ruleId)
        assertEquals(userId, userRules.userId)
        assertEquals(language, userRules.language)
        assertEquals(allRules, userRules.allRules)
    }
}
