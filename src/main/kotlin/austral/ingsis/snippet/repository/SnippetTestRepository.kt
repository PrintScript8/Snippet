package austral.ingsis.snippet.repository

import austral.ingsis.snippet.model.SnippetTest
import org.springframework.data.jpa.repository.JpaRepository

interface SnippetTestRepository : JpaRepository<SnippetTest, Long> {
    fun findAllBySnippetId(snippetId: Long): List<SnippetTest>

    fun findAllByOwnerId(ownerId: String): List<SnippetTest>

    fun deleteAllBySnippetId(snippetId: Long)
}
