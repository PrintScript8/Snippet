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
    val type: ConfigType
) {
    constructor() : this(0L, "", false, "", ConfigType.FORMATTING)

    override fun toString(): String {
        return if (value == null) {
            "\"${name}\": ${isActive}"
        } else {
            "\"${name}\": \"$value\""
        }
    }
}

// todo: Agregar to String

/*
Formmating:
{ "rules": { "spaceBeforeColon": false, "spaceAfterColon": true, "spaceAroundEquals": true, "newlineBeforePrintln": 2 } }

Linting:

{ "identifier_format": "camel case" , "mandatory-variable-or-literal-in-println": "true" }

 */