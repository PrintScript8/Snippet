package austral.ingsis.snippet

import austral.ingsis.snippet.factory.SnippetFactory
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.repository.SnippetRepositoryInterface
import austral.ingsis.snippet.service.SnippetService
import austral.ingsis.snippet.validator.SnippetValidator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SnippetServiceTest {
    private val snippetRepository = mockk<SnippetRepositoryInterface>()
    private val snippetFactory = mockk<SnippetFactory>()
    private val snippetValidator = mockk<SnippetValidator>()
    private val snippetService = SnippetService(snippetRepository, snippetFactory, snippetValidator)

    @Test
    fun `should return all snippets`() {
        // Arrange
        val snippets =
            listOf(
                Snippet(
                    1,
                    "First Snippet",
                    "Description",
                    "Code",
                    "python",
                    1L,
                    "{ \"identifier_format\": \"camel case\"}",
                ),
            )
        every { snippetRepository.findAll() } returns snippets

        // Act
        val result = snippetService.getAllSnippets()

        // Assert
        assertEquals(snippets, result)
        verify(exactly = 1) { snippetRepository.findAll() }
    }

    @Test
    fun `should return snippet by id`() {
        // Arrange
        val snippet =
            Snippet(
                1,
                "First Snippet",
                "Description",
                "Code",
                "python",
                1L,
                "{ \"identifier_format\": \"camel case\"}",
            )
        every { snippetRepository.findById(1) } returns java.util.Optional.of(snippet)

        // Act
        val result = snippetService.getSnippetById(1)

        // Assert
        assertEquals(snippet, result)
        verify(exactly = 1) { snippetRepository.findById(1) }
    }

    @Test
    fun `should create new snippet`() {
        // Arrange
        val newSnippet =
            Snippet(
                2,
                "New Snippet",
                "Description",
                "Code",
                "python",
                1L,
                "{ \"identifier_format\": \"camel case\"}",
            )
        every {
            snippetFactory.createSnippet(
                "New Snippet",
                "Description",
                "Code",
                "python",
                1L,
                "{ \"identifier_format\": \"camel case\"}",
            )
        } returns newSnippet
        every { snippetRepository.save(newSnippet) } returns newSnippet
        every {
            snippetValidator.validateSnippet(
                "Code",
                "python",
                "{ \"identifier_format\": \"camel case\"}",
            )
        } returns true

        // Act
        val result =
            snippetService.createSnippet(
                "New Snippet",
                "Description",
                "Code",
                "python",
                1L,
                "{ \"identifier_format\": \"camel case\"}",
            )

        // Assert
        assertEquals(newSnippet, result)
        verify(exactly = 1) {
            snippetFactory.createSnippet(
                "New Snippet",
                "Description",
                "Code",
                "python",
                1L,
                "{ \"identifier_format\": \"camel case\"}",
            )
        }
        verify(exactly = 1) { snippetRepository.save(newSnippet) }
        verify(exactly = 1) {
            snippetValidator.validateSnippet(
                "Code",
                "python",
                "{ \"identifier_format\": \"camel case\"}",
            )
        }
    }

    @Test
    fun `should delete snippet by id`() {
        // Arrange
        every { snippetRepository.deleteById(1) } returns Unit

        // Act
        snippetService.deleteSnippet(1)

        // Assert
        verify(exactly = 1) { snippetRepository.deleteById(1) }
    }

    @Test
    fun `should update snippet`() {
        // Arrange - Mock the service to return an existing snippet and then an updated snippet
        val existingSnippet =
            Snippet(
                1,
                "Old Snippet",
                "Old Description",
                "Old Code",
                "python",
                1L,
                "{ \"identifier_format\": \"camel case\"}",
            )
        val updatedSnippet =
            Snippet(
                1,
                "Updated Snippet",
                "Updated Description",
                "Updated Code",
                "python",
                1L,
                "{ \"identifier_format\": \"camel case\"}",
            )
        every { snippetRepository.findById(1) } returns java.util.Optional.of(existingSnippet)
        every { snippetRepository.save(any()) } returns updatedSnippet
        every {
            snippetValidator.validateSnippet(
                "Updated Code",
                "python",
                "{ \"identifier_format\": \"camel case\"}",
            )
        } returns true
        // Act
        val result =
            snippetService.updateSnippet(
                1,
                "Updated Snippet",
                "Updated Description",
                "Updated Code",
                "python",
                1L,
                "{ \"identifier_format\": \"camel case\"}",
            )

        // Assert
        assertEquals(updatedSnippet, result)
        verify(exactly = 1) { snippetRepository.findById(1) }
        verify(exactly = 1) { snippetRepository.save(updatedSnippet) }
        verify(exactly = 1) {
            snippetValidator.validateSnippet(
                "Updated Code",
                "python",
                "{ \"identifier_format\": \"camel case\"}",
            )
        }
    }

    @Test
    fun `should return null updating non-existing snippet`() {
        // Arrange
        every { snippetRepository.findById(1) } returns java.util.Optional.empty()

        // Act
        val result =
            snippetService.updateSnippet(
                1,
                "Updated Snippet",
                "Updated Description",
                "Updated Code",
                "python",
                1L,
                "{ \"identifier_format\": \"camel case\"}",
            )

        // Assert
        assertEquals(null, result)
        verify(exactly = 1) { snippetRepository.findById(1) }
    }
}
