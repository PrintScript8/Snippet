package austral.ingsis.snippet.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class Snippet(
    @Id
    @GeneratedValue
    val id: Long,
    val name: String,
    val language: String,
    val ownerId: String,
    val extension: String,
    var status: ComplianceEnum,
) {
    constructor() : this(0, "", "", "", "", ComplianceEnum.PENDING)
}
