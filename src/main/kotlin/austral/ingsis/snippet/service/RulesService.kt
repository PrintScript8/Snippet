package austral.ingsis.snippet.service

import austral.ingsis.snippet.factory.UserRulesFactory
import austral.ingsis.snippet.model.UserRules
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class RulesService(
    @Autowired private val userRulesFactory: UserRulesFactory,
    @Autowired private val clientBuilder: RestClient.Builder,
) {
    private val bucketClient = clientBuilder.baseUrl("http://asset-service:8080").build()

    fun updateLintingRules(
        userId: Long,
        lintingConfig: String,
        language: String,
    ) {
        var userRules =
            bucketClient.get()
                .uri("/v1/asset/{container}/{key}", language, userId)
                .retrieve()
                .body(UserRules::class.java)

        if (userRules != null) {
            userRules.lintingConfig = lintingConfig
        } else {
            userRules = userRulesFactory.buildUserRules(userId, lintingConfig, "", language)
        }
        bucketClient.put()
            .uri("/v1/asset/{container}/{key}", language, userId)
            .body(userRules)
            .retrieve()
            .body(UserRules::class.java)
    }

    fun updateFormattingRules(
        userId: Long,
        formattingConfig: String,
        language: String,
    ) {
        var userRules =
            bucketClient.get()
                .uri("/v1/asset/{container}/{key}", language, userId)
                .retrieve()
                .body(UserRules::class.java)

        if (userRules != null) {
            userRules.formattingConfig = formattingConfig
        } else {
            userRules = userRulesFactory.buildUserRules(userId, "", formattingConfig, language)
        }
        bucketClient.put()
            .uri("/v1/asset/{container}/{key}", language, userId)
            .body(userRules)
            .retrieve()
            .body(UserRules::class.java)
    }
}
