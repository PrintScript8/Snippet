package austral.ingsis.snippet.factory

import austral.ingsis.snippet.model.Snippet
import org.springframework.stereotype.Component

@Component
class SnippetFactory {
    fun createSnippet(
        name: String,
        description: String,
        code: String,
        language: String,
        ownerId: Long,
        config: String,
    ): Snippet {
        return Snippet(
            name = name,
            description = description,
            code = code,
            language = language,
            ownerId = ownerId,
            config = config,
        )
    }
}
