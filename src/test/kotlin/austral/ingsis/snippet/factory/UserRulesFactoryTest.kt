package austral.ingsis.snippet.factory

import austral.ingsis.snippet.model.UserRules
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserRulesFactoryTest {
    private lateinit var userRulesFactory: UserRulesFactory

    @BeforeEach
    fun setUp() {
        userRulesFactory = UserRulesFactory()
    }

    @Test
    fun `should build UserRules correctly`() {
        // Arrange
        val userId = 1L
        val language = "kotlin"
        // val formattingConfig = "some-formatting-config"

        // Act
        val userRules: UserRules =
            userRulesFactory.buildUserRules(
                userId = userId,
                language = language,
                allRules = emptyList<Long>(),
            )

        // Assert
        assertEquals(userId, userRules.userId)
        assertEquals(language, userRules.language)
        assertEquals(emptyList<Long>(), userRules.allRules)
    }
}
