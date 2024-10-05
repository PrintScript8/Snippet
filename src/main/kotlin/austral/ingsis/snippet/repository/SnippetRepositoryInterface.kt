package austral.ingsis.snippet.repository

import austral.ingsis.snippet.model.Snippet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SnippetRepositoryInterface : JpaRepository<Snippet, Long>
