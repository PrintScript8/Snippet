package austral.ingsis.snippet.model

data class CommunicationSnippet (
    val id: Long?,
    val name: String,
    val language: String,
    val ownerId: Long,
    val content: String,
    val extension: String,
    val status: ComplianceEnum
)
