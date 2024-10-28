package austral.ingsis.snippet.message

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExecuteRequestTest {
    @Test
    fun `should create ExecuteRequest with correct properties`() {
        // Arrange
        val ownerId = 1L
        val language = "python"
        val rules = "some-rules"
        val action = "execute"

        // Act
        val executeRequest = ExecuteRequest(ownerId, language, rules, action)

        // Assert
        assertEquals(ownerId, executeRequest.ownerId)
        assertEquals(language, executeRequest.language)
        assertEquals(rules, executeRequest.rules)
        assertEquals(action, executeRequest.action)
    }
}
