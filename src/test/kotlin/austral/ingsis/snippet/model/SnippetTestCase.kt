package austral.ingsis.snippet.model

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlin.test.Test

class SnippetTestCase {
    @Test
    fun `test Snippet creation`() {
        val snippet =
            Snippet(
                id = 1L,
                name = "Test Snippet",
                language = "Kotlin",
                ownerId = "123L",
                extension = "kt",
                status = ComplianceEnum.COMPLIANT,
            )

        assertNotNull(snippet.id)
        assertEquals("Test Snippet", snippet.name)
        assertEquals("Kotlin", snippet.language)
        assertEquals("123L", snippet.ownerId)
        assertEquals("kt", snippet.extension)
        assertEquals(ComplianceEnum.COMPLIANT, snippet.status)
    }
}
