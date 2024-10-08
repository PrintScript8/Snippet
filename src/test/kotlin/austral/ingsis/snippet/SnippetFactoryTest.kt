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
        val languageId = 1L
        val ownerId = 1L
        val usersWithReadPermission = listOf<Long>()
        val usersWithWritePermission = listOf<Long>()

        // Act
        val snippet = snippetFactory.createSnippet(
            name = name,
            description = description,
            code = code,
            languageId = languageId,
            ownerId = ownerId,
            usersWithReadPermission = usersWithReadPermission,
            usersWithWritePermission = usersWithWritePermission
        )

        // Assert
        assertEquals(name, snippet.name)
        assertEquals(description, snippet.description)
        assertEquals(code, snippet.code)
        assertEquals(languageId, snippet.languageId)
        assertEquals(ownerId, snippet.ownerId)
        assertEquals(usersWithReadPermission, snippet.usersWithReadPermission)
        assertEquals(usersWithWritePermission, snippet.usersWithWritePermission)
    }

    @Test
    fun `should create empty snippet`() {
        val emptySnippet = Snippet()

        val expectedName = ""
        val expectedDescription = ""
        val expectedCode = ""
        val expectedLanguageId = 0L
        val expectedOwnerId = 0L
        val expectedUsersWithReadPermission = emptyList<Long>()
        val expectedUsersWithWritePermission = emptyList<Long>()

        // Assert
        assertEquals(expectedName, emptySnippet.name)
        assertEquals(expectedDescription, emptySnippet.description)
        assertEquals(expectedCode, emptySnippet.code)
        assertEquals(expectedLanguageId, emptySnippet.languageId)
        assertEquals(expectedOwnerId, emptySnippet.ownerId)
        assertEquals(expectedUsersWithReadPermission, emptySnippet.usersWithReadPermission)
        assertEquals(expectedUsersWithWritePermission, emptySnippet.usersWithWritePermission)
    }
}