package austral.ingsis.snippet.model

data class Snippet(
    val id: Long,
    val name: String,
    val description: String,
    val code: String,
    val language: String,
    val ownerId: Long,
    val config: String,
)
