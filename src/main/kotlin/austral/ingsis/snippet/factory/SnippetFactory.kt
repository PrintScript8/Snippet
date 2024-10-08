package austral.ingsis.snippet.factory

import austral.ingsis.snippet.model.Snippet
import org.springframework.stereotype.Component

@Component
class SnippetFactory {
    fun createSnippet(
        name: String,
        description: String,
        code: String,
        languageId: Long,
        ownerId: Long,
        usersWithReadPermission: List<Long> = emptyList(),
        usersWithWritePermission: List<Long> = emptyList()
    ): Snippet {
        return Snippet(
            name = name,
            description = description,
            code = code,
            languageId = languageId,
            ownerId = ownerId,
            usersWithReadPermission = usersWithReadPermission,
            usersWithWritePermission = usersWithWritePermission
        )
    }
}
