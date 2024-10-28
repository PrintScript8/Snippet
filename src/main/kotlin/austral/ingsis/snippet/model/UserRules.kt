package austral.ingsis.snippet.model

data class UserRules(
    val userId: Long,
    var lintingConfig: String,
    var formattingConfig: String,
    val language: String,
)
