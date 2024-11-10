package austral.ingsis.snippet.factory

import austral.ingsis.snippet.model.CommunicationSnippet
import austral.ingsis.snippet.model.ComplianceEnum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SnippetFactoryTest {
    @Test
    fun `should create snippet`() {
        // Arrange
        val id: Long = 1L
        val name: String = "Test Snippet"
        val language: String = "PrintScript"
        val ownerId: Long = 1L
        val content: String = "let n:Number = 5"
        val extension: String = "ps"
        val status: ComplianceEnum = ComplianceEnum.COMPLIANT

        // Act
        val snippet =
            CommunicationSnippet(
                id, name, language, ownerId, content, extension, status
            )

        // Assert
        assertEquals(name, snippet.name)
        assertEquals(extension, snippet.extension)
        assertEquals(content, snippet.content)
        assertEquals(language, snippet.language)
        assertEquals(ownerId, snippet.ownerId)
    }

    @Test
    fun `should create empty snippet`() {
        val emptySnippet = CommunicationSnippet(1L, "", "", 1L, "", "", ComplianceEnum.COMPLIANT)

        val expectedId: Long = 1L
        val expectedName: String = ""
        val expectedLanguage: String = ""
        val expectedOwnerId: Long = 1L
        val expectedContent: String = ""
        val expectedExtension: String = ""
        val expectedStatus: ComplianceEnum = ComplianceEnum.COMPLIANT

        // Assert
        assertEquals(expectedName, emptySnippet.name)
        assertEquals(expectedLanguage, emptySnippet.language)
        assertEquals(expectedContent, emptySnippet.content)
        assertEquals(expectedOwnerId, emptySnippet.ownerId)
    }
}
