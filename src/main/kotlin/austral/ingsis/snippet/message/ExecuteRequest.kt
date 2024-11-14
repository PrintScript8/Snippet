package austral.ingsis.snippet.message

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ExecuteRequest
    @JsonCreator
    constructor(
        @JsonProperty("token") val token: String,
        @JsonProperty("language") val language: String,
        @JsonProperty("rules") val rules: String,
        @JsonProperty("action") val action: String,
        @JsonProperty("snippetId") val snippetId: Long? = null,
    )
