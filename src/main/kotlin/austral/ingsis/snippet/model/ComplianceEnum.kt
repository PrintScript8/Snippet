package austral.ingsis.snippet.model

enum class ComplianceEnum {
    COMPLIANT,
    NON_COMPLIANT,
    PENDING,
    FAILED;

    override fun toString(): String {
        return name.lowercase()
    }
}
