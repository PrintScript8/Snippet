package austral.ingsis.snippet.message

data class ExecuteRequest(
    val language: String,
    val code: String,
    val action: String,
    val inputs: String,
)
