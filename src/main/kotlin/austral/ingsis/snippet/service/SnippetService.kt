package austral.ingsis.snippet.service

import austral.ingsis.snippet.factory.SnippetFactory
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.repository.SnippetRepositoryInterface
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SnippetService(
    @Autowired private val snippetRepository: SnippetRepositoryInterface,
    @Autowired private val snippetFactory: SnippetFactory,
) {
    fun createSnippet(
        name: String,
        creationDate: String,
    ): Snippet {
        val snippet = snippetFactory.createSnippet(name, creationDate)
        return snippetRepository.save(snippet)
    }

    fun getAllSnippets(): List<Snippet> {
        return snippetRepository.findAll()
    }

    fun getSnippetById(id: Long): Snippet? {
        return snippetRepository.findById(id).orElse(null)
    }

    fun updateSnippet(
        id: Long,
        name: String,
        creationDate: String,
    ): Snippet? {
        val snippet = snippetRepository.findById(id).orElse(null) ?: return null
        val updatedSnippet = snippet.copy(name = name, creationDate = creationDate)
        return snippetRepository.save(updatedSnippet)
    }

    fun deleteSnippet(id: Long) {
        snippetRepository.deleteById(id)
    }
}
