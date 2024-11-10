package austral.ingsis.snippet.service

import austral.ingsis.snippet.factory.UserRulesFactory
import austral.ingsis.snippet.model.ConfigType
import austral.ingsis.snippet.model.Rule
import austral.ingsis.snippet.model.UserRules
import austral.ingsis.snippet.repository.RulesRepository
import austral.ingsis.snippet.repository.UserRulesRepository
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RulesService(
    @Autowired private val userRulesFactory: UserRulesFactory,
//    @Autowired private val clientBuilder: RestClient.Builder,
    @Autowired private val userRulesRepository: UserRulesRepository,
    @Autowired private val rulesRepository: RulesRepository,
) {
//    private val bucketClient = clientBuilder.baseUrl("http://asset-service:8080").build()
    private val logger = LogManager.getLogger(RulesService::class.java)

    fun getRules(
        userId: Long,
        configType: ConfigType,
    ): List<Rule> {
        val userRules =
            userRulesRepository.findByUserId(userId) ?: throw NoSuchElementException(
                "User with ID $userId not found",
            )
        val rules: List<Rule> = rulesRepository.findAllById(userRules.allRules)
        val output = rules.filter { it.type == configType }
        return output
    }

    fun createRules(
        userId: Long,
        language: String,
    ) {
        val formattingRules =
            listOf(
                Rule(0L, "spaceBeforeColon", false, null, ConfigType.FORMATTING),
                Rule(0L, "spaceAfterColon", false, null, ConfigType.FORMATTING),
                Rule(0L, "spaceAroundEquals", false, null, ConfigType.FORMATTING),
                Rule(0L, "newlineBeforePrintln", false, "0", ConfigType.FORMATTING),
            )
        val lintingRules =
            listOf(
                Rule(0L, "identifier_format", false, "camel case", ConfigType.LINTING),
                Rule(
                    0L,
                    "mandatory-variable-or-literal-in-println",
                    false,
                    null,
                    ConfigType.LINTING,
                ),
                Rule(
                    0L,
                    "mandatory-variable-or-literal-in-readInput",
                    false,
                    null,
                    ConfigType.LINTING,
                ),
            )
        val formatting: List<Rule> = rulesRepository.saveAll(formattingRules)
        val linting: List<Rule> = rulesRepository.saveAll(lintingRules)
        val formattingId: List<Long> = formatting.map { it.id }
        val lintingId: List<Long> = linting.map { it.id }
        val user: UserRules = userRulesFactory.buildUserRules(language, userId, formattingId + lintingId)
        logger.info(user)
        userRulesRepository.save(user)
    }

    fun updateRules(
        userId: Long,
        language: String,
        configType: ConfigType,
        rules: List<Rule>,
    ): String {
        val userRules =
            userRulesRepository.findByUserId(userId) ?: throw NoSuchElementException(
                "User with ID $userId not found and $language",
            )
        val rulesIds = rulesRepository.saveAll(rules).map { it.id }
        for (id in rulesIds) {
            if (!userRules.allRules.contains(id))
                {
                    userRules.allRules.plus(id)
                }
        }
        userRulesRepository.save(userRules)
        val ruleList: List<Rule> = rulesRepository.findAllById(userRules.allRules)
        return when (configType) {
            ConfigType.FORMATTING -> {
                val formatRules = ruleList.filter { it.type == configType }
                convertFormatToJson(formatRules)
            }
            ConfigType.LINTING -> {
                val lintRules = ruleList.filter { it.type == configType }
                convertLintingToJson(lintRules)
            }
        }
    }

    private fun convertFormatToJson(rules: List<Rule>): String {
        // Use StringBuilder for efficient string concatenation
        val result = StringBuilder("{ \"rules\": { ")

        // Iterate over rules and append each rule's JSON string
        rules.forEachIndexed { index, rule ->
            result.append(rule.toString())
            if (index < rules.size - 1) result.append(", ")
        }

        result.append(" } }")
        return result.toString()
    }

    private fun convertLintingToJson(rules: List<Rule>): String {
        val result = StringBuilder("{ ")

        rules.forEachIndexed { index, rule ->
            result.append(rule.toString())
            if (index < rules.size - 1) result.append(", ")
        }

        result.append(" }")
        return result.toString()
    }
}
