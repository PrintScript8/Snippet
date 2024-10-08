package austral.ingsis.snippet

import austral.ingsis.snippet.factory.SnippetFactory
import austral.ingsis.snippet.model.Snippet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SnippetFactoryTest {
    private val snippetFactory = SnippetFactory()

    @Test
    fun `should create snippet`() {
        // Arrange
        val name = "Test Snippet"
        val content = "Test Content"

        // Act
        val snippet = snippetFactory.createSnippet(name, content)

        // Assert
        assertEquals(name, snippet.name)
        assertEquals(content, snippet.creationDate)
    }

    @Test
    fun `should create empty snippet`() {
        val emptySnippet = Snippet()

        val expectedName = ""
        val expectedContent = ""

        // Assert
        assertEquals(expectedName, emptySnippet.name)
        assertEquals(expectedContent, emptySnippet.creationDate)
    }
}
