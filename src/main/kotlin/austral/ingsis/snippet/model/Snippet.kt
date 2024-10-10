package austral.ingsis.snippet.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Snippet(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val name: String,
    val description: String,
    val code: String,
    val languageId: Long,
    val ownerId: Long,
) {
    constructor() : this(0, "", "", "", 0, 0)
}
