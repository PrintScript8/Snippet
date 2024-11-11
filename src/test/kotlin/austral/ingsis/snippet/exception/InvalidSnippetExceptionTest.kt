package austral.ingsis.snippet.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class InvalidSnippetExceptionTest {
    @Test
    fun `test InvalidSnippetException message`() {
        val message = "Invalid snippet"
        val exception =
            assertThrows(InvalidSnippetException::class.java) {
                throw InvalidSnippetException(message)
            }
        assertEquals(message, exception.message)
    }
}
