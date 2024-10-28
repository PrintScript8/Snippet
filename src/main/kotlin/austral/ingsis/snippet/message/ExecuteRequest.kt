package austral.ingsis.snippet.message

data class ExecuteRequest(
    val ownerId: Long,
    val language: String,
    val rules: String,
    val action: String,
)
