package austral.ingsis.snippet.factory

import austral.ingsis.snippet.model.Snippet
import org.springframework.stereotype.Component

@Component
class SnippetFactory {
    fun createSnippet(name: String, creationDate: String): Snippet {
        return Snippet(name = name, creationDate = creationDate)
    }
}
