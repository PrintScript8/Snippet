package austral.ingsis.snippet.model

import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class UserRules(
    @Id
    @GeneratedValue
    var ruleId: Long,
    val userId: Long,
    val language: String,
    @ElementCollection
    var lintingRules: List<Long>,
    @ElementCollection
    var formattingRules: List<Long>,
) {
    constructor() : this(0L, 0L, "", emptyList(), emptyList())
}

// todo: Merge formating and linting rules to just one collection
