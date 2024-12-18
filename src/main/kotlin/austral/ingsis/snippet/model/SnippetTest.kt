package austral.ingsis.snippet.model

import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class SnippetTest(
    @Id
    @GeneratedValue
    val testId: Long = 0,
    val snippetId: Long = 0,
    val ownerId: String = "",
    val name: String,
    @ElementCollection
    val input: List<String>,
    @ElementCollection
    val output: List<String>,
) {
    constructor() : this(0L, 0L, "", "", emptyList(), emptyList())
}
