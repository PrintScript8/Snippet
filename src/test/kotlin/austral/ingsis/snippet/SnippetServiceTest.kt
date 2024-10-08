package austral.ingsis.snippet

import austral.ingsis.snippet.factory.SnippetFactory
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.repository.SnippetRepositoryInterface
import austral.ingsis.snippet.service.SnippetService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SnippetServiceTest {
    private val snippetRepository = mockk<SnippetRepositoryInterface>()
    private val snippetFactory = mockk<SnippetFactory>()
    private val snippetService = SnippetService(snippetRepository, snippetFactory)

    @Test
    fun `should return all snippets`() {
        // Arrange
        val snippets = listOf(Snippet(1, "First Snippet", "Content of the first snippet"))
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
        val snippet = Snippet(1, "First Snippet", "Content of the first snippet")
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
        val newSnippet = Snippet(2, "New Snippet", "New Content")
        every { snippetFactory.createSnippet("New Snippet", "New Content") } returns newSnippet
        every { snippetRepository.save(newSnippet) } returns newSnippet

        // Act
        val result = snippetService.createSnippet("New Snippet", "New Content")

        // Assert
        assertEquals(newSnippet, result)
        verify(exactly = 1) { snippetFactory.createSnippet("New Snippet", "New Content") }
        verify(exactly = 1) { snippetRepository.save(newSnippet) }
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
        // Arrange
        val existingSnippet = Snippet(1, "Old Snippet", "Old Content")
        val updatedSnippet = Snippet(1, "Updated Snippet", "Updated Content")
        every { snippetRepository.findById(1) } returns java.util.Optional.of(existingSnippet)
        every { snippetRepository.save(any()) } returns updatedSnippet

        // Act
        val result = snippetService.updateSnippet(1, "Updated Snippet", "Updated Content")

        // Assert
        assertEquals(updatedSnippet, result)
        verify(exactly = 1) { snippetRepository.findById(1) }
        verify(exactly = 1) { snippetRepository.save(updatedSnippet) }
    }

    @Test
    fun `should return null updating non-existing snippet`() {
        // Arrange
        every { snippetRepository.findById(1) } returns java.util.Optional.empty()

        // Act
        val result = snippetService.updateSnippet(1, "Updated Snippet", "Updated Content")

        // Assert
        assertEquals(null, result)
        verify(exactly = 1) { snippetRepository.findById(1) }
    }
}
