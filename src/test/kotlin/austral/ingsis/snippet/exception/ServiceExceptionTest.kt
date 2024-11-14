package austral.ingsis.snippet.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ServiceExceptionTest {
    @Test
    fun `test ServiceException with message`() {
        val message = "Service exception occurred"
        val exception = ServiceException(message, Throwable())
        assertNotNull(exception)
        assertEquals(message, exception.message)
    }

    @Test
    fun `test ServiceException with message and cause`() {
        val message = "Service exception occurred"
        val cause = Throwable("Cause of the exception")
        val exception = ServiceException(message, cause)
        assertNotNull(exception)
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
    }
}
