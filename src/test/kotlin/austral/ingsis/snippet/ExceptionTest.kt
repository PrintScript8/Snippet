package austral.ingsis.snippet

import austral.ingsis.snippet.exception.InvalidSnippetException
import austral.ingsis.snippet.exception.ServiceException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExceptionTest {
    @Test
    fun `test InvalidSnippetException`() {
        val message = "Invalid snippet"
        val exception = InvalidSnippetException(message)

        assertEquals(message, exception.message)
    }

    @Test
    fun `test ServiceException`() {
        val message = "Service error"
        val cause = RuntimeException("Cause of the error")
        val exception = ServiceException(message, cause)

        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
    }
}
