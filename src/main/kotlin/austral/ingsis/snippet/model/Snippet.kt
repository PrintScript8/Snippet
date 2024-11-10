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
    val ownerId: Long,
    val extension: String,
    var status: ComplianceEnum,
) {
    constructor() : this(0, "", "", 0, "", ComplianceEnum.PENDING)
}
