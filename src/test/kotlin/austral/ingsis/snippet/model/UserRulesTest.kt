package austral.ingsis.snippet.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UserRulesTest {
    @Test
    fun `test UserRules default constructor`() {
        val userRules = UserRules()
        assertEquals(0L, userRules.ruleId)
        assertEquals("", userRules.userId)
        assertEquals("", userRules.language)
        assertTrue(userRules.allRules.isEmpty())
    }

    @Test
    fun `test UserRules parameterized constructor`() {
        val allRules = listOf(1L, 2L, 3L)
        val userRules = UserRules(1L, "123L", "English", allRules)
        assertEquals(1L, userRules.ruleId)
        assertEquals("123L", userRules.userId)
        assertEquals("English", userRules.language)
        assertEquals(allRules, userRules.allRules)
    }
}
