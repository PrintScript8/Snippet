package austral.ingsis.snippet.service

import austral.ingsis.snippet.exception.InvalidSnippetException
import austral.ingsis.snippet.exception.ServiceException
import austral.ingsis.snippet.factory.SnippetFactory
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.repository.SnippetRepositoryInterface
import austral.ingsis.snippet.validator.SnippetValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SnippetService(
    @Autowired private val snippetRepository: SnippetRepositoryInterface,
    @Autowired private val snippetFactory: SnippetFactory,
    @Autowired private val snippetValidator: SnippetValidator,
) {
    @Suppress("ThrowsCount", "LongParameterList")
    fun createSnippet(
        name: String,
        description: String,
        code: String,
        language: String,
        ownerId: Long,
        config: String,
    ): Snippet {
        val snippet = snippetFactory.createSnippet(name, description, code, language, ownerId, config)

        return try {
            if (snippetValidator.validateSnippet(code, language, config)) {
                snippetRepository.save(snippet)
            } else {
                throw InvalidSnippetException("Snippet is invalid")
            }
        } catch (e: InvalidSnippetException) {
            throw e
        } catch (e: ServiceException) {
            throw ServiceException("Error creating snippet", e)
        }
    }

    fun createSnippetFromFile(name: String, code: String): Snippet {
        val snippet = snippetFactory.createSnippet(name, "", code, "printscript", 0, "")

        return try {
            if (snippetValidator.validateSnippet(snippet.code, snippet.language, snippet.config)) {
                snippetRepository.save(snippet)
            } else {
                throw InvalidSnippetException("Snippet is invalid")
            }
        } catch (e: InvalidSnippetException) {
            throw e
        } catch (e: ServiceException) {
            throw ServiceException("Error creating snippet", e)
        }
    }

    fun getAllSnippets(): List<Snippet> {
        return snippetRepository.findAll()
    }

    fun getSnippetById(id: Long): Snippet? {
        return snippetRepository.findById(id).orElse(null)
    }

    @Suppress("LongParameterList")
    fun updateSnippet(
        id: Long,
        name: String,
        description: String,
        code: String,
        language: String,
        ownerId: Long,
        config: String,
    ): Snippet? {
        val snippet = snippetRepository.findById(id).orElse(null) ?: return null
        val updatedSnippet =
            snippet.copy(
                name = name,
                description = description,
                code = code,
                language = language,
                ownerId = ownerId,
                config = config,
            )

        return try {
            snippetValidator.validateSnippet(code, language, config)
            snippetRepository.save(updatedSnippet)
        } catch (e: ServiceException) {
            throw ServiceException("Error updating snippet", e)
        }
    }

    fun deleteSnippet(id: Long) {
        snippetRepository.deleteById(id)
    }
}
