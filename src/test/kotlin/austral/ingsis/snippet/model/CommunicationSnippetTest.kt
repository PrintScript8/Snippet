package austral.ingsis.snippet.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CommunicationSnippetTest {
    @Test
    fun `test CommunicationSnippet creation`() {
        val snippet =
            CommunicationSnippet(
                id = 1L,
                name = "Test Snippet",
                language = "Kotlin",
                ownerId = 123L,
                content = "fun main() { println(\"Hello, World!\") }",
                extension = "kt",
                status = ComplianceEnum.COMPLIANT,
            )

        assertNotNull(snippet.id)
        assertEquals("Test Snippet", snippet.name)
        assertEquals("Kotlin", snippet.language)
        assertEquals(123L, snippet.ownerId)
        assertEquals("fun main() { println(\"Hello, World!\") }", snippet.content)
        assertEquals("kt", snippet.extension)
        assertEquals(ComplianceEnum.COMPLIANT, snippet.status)
    }
}
