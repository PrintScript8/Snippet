package austral.ingsis.snippet.repository

import austral.ingsis.snippet.model.Snippet
import org.springframework.data.jpa.repository.JpaRepository

interface SnippetRepository : JpaRepository<Snippet, Long>
