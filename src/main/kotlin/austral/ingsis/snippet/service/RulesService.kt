package austral.ingsis.snippet.service

import austral.ingsis.snippet.factory.UserRulesFactory
import austral.ingsis.snippet.model.ConfigType
import austral.ingsis.snippet.model.Rule
import austral.ingsis.snippet.model.UserRules
import austral.ingsis.snippet.repository.RulesRepository
import austral.ingsis.snippet.repository.UserRulesRepository
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service

@Service
class RulesService(
    @Autowired private val userRulesFactory: UserRulesFactory,
    @Autowired private val userRulesRepository: UserRulesRepository,
    @Autowired private val rulesRepository: RulesRepository,
) {
    private val logger = LogManager.getLogger(RulesService::class.java)

    fun getRules(
        userId: String,
        configType: ConfigType,
    ): List<Rule> {
        val userRules = userRulesRepository.findByUserId(userId) ?: throw NotFoundException()
        val rules: List<Rule> = rulesRepository.findAllById(userRules.allRules)
        val output = rules.filter { it.type == configType }
        return output
    }

    fun createRules(
        userId: String,
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
                Rule(0L, "mandatory-variable-or-literal-in-println", false, null, ConfigType.LINTING),
                Rule(0L, "mandatory-variable-or-literal-in-readInput", false, null, ConfigType.LINTING),
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
        userId: String,
        language: String,
        configType: ConfigType,
        rules: List<Rule>,
    ): String {
        val userRules = userRulesRepository.findByUserId(userId) ?: throw NotFoundException()
        println(language)
        val rulesIds = rulesRepository.saveAll(rules).map { it.id }
        for (id in rulesIds) {
            if (!userRules.allRules.contains(id)) {
                userRules.allRules.plus(id)
            }
        }
        userRulesRepository.save(userRules)
        val retrievedRules: List<Rule> = rulesRepository.findAllById(userRules.allRules)
        when (configType) {
            ConfigType.FORMATTING -> {
                val formatRules = retrievedRules.filter { it.type == configType }
                return convertFormatToJson(formatRules)
            }
            ConfigType.LINTING -> {
                val lintRules = retrievedRules.filter { it.type == configType }
                return convertLintingToJson(lintRules)
            }
            else -> {
                error("Invalid config type")
            }
        }
    }

    fun getFormatJson(userId: String): String {
        val userRules = userRulesRepository.findByUserId(userId) ?: throw NotFoundException()
        val rules: List<Rule> = rulesRepository.findAllById(userRules.allRules)
        val formatRules = rules.filter { it.type == ConfigType.FORMATTING }
        return convertFormatToJson(formatRules)
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
