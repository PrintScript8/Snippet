package austral.ingsis.snippet.message

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class ExecuteRequestTest {
    @Test
    fun `test ExecuteRequest creation`() {
        val request =
            ExecuteRequest(
                token = "1L",
                language = "Kotlin",
                rules = "Some rules",
                action = "Execute",
                snippetId = 123L,
            )

        assertEquals("1L", request.token)
        assertEquals("Kotlin", request.language)
        assertEquals("Some rules", request.rules)
        assertEquals("Execute", request.action)
        assertEquals(123L, request.snippetId)
    }
}
