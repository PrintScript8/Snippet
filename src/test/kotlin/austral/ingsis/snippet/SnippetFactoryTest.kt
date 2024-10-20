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
        val description = "Test Description"
        val code = "Test Code"
        val language = "python"
        val ownerId = 1L
        val config = "{ \"identifier_format\": \"camel case\"}"

        // Act
        val snippet =
            snippetFactory.createSnippet(
                name = name,
                description = description,
                code = code,
                language = language,
                ownerId = ownerId,
                config = config,
            )

        // Assert
        assertEquals(name, snippet.name)
        assertEquals(description, snippet.description)
        assertEquals(code, snippet.code)
        assertEquals(language, snippet.language)
        assertEquals(ownerId, snippet.ownerId)
    }

    @Test
    fun `should create empty snippet`() {
        val emptySnippet = Snippet()

        val expectedName = ""
        val expectedDescription = ""
        val expectedCode = ""
        val expectedLanguage = "python"
        val expectedOwnerId = 0L

        // Assert
        assertEquals(expectedName, emptySnippet.name)
        assertEquals(expectedDescription, emptySnippet.description)
        assertEquals(expectedCode, emptySnippet.code)
        assertEquals(expectedLanguage, emptySnippet.language)
        assertEquals(expectedOwnerId, emptySnippet.ownerId)
    }
}
