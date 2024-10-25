package austral.ingsis.snippet

import austral.ingsis.snippet.model.Snippet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SnippetFactoryTest {
    @Test
    fun `should create snippet`() {
        // Arrange
        val name = "Test Snippet"
        val description = "Test Description"
        val code = "Test Code"
        val language = "printscript"
        val ownerId = 1L
        val config = "{ \"identifier_format\": \"camel case\"}"

        // Act
        val snippet =
            Snippet(
                1L,
                name,
                description,
                code,
                language,
                ownerId,
                config,
            )

        // Assert
        assertEquals(name, snippet.name)
        assertEquals(description, snippet.description)
        assertEquals(code, snippet.code)
        assertEquals(language, snippet.language)
        assertEquals(ownerId, snippet.ownerId)
        assertEquals(config, snippet.config)
    }

    @Test
    fun `should create empty snippet`() {
        val emptySnippet = Snippet(1L, "", "", "", "", 0L, "")

        val expectedName = ""
        val expectedDescription = ""
        val expectedCode = ""
        val expectedLanguage = ""
        val expectedOwnerId = 0L
        val expectedConfig = ""

        // Assert
        assertEquals(expectedName, emptySnippet.name)
        assertEquals(expectedDescription, emptySnippet.description)
        assertEquals(expectedCode, emptySnippet.code)
        assertEquals(expectedLanguage, emptySnippet.language)
        assertEquals(expectedOwnerId, emptySnippet.ownerId)
        assertEquals(expectedConfig, emptySnippet.config)
    }
}
