package austral.ingsis.snippet.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class Rule(
    @Id
    @GeneratedValue
    val id: Long,
    val name: String,
    val isActive: Boolean,
    val value: String?,
//    val type: ConfigType
) {
    constructor() : this(0L, "", false, "")
}

// todo: Agregar to String
// todo: Manejar cambio de rule a type