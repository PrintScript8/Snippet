package austral.ingsis.snippet.service

import austral.ingsis.snippet.factory.UserRulesFactory
import austral.ingsis.snippet.model.ConfigType
import austral.ingsis.snippet.model.Rule
import austral.ingsis.snippet.model.UserRules
import austral.ingsis.snippet.repository.RulesRepository
import austral.ingsis.snippet.repository.UserRulesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.apache.logging.log4j.LogManager

@Service
class RulesService(
    @Autowired private val userRulesFactory: UserRulesFactory,
    @Autowired private val clientBuilder: RestClient.Builder,
    @Autowired private val userRulesRepository: UserRulesRepository,
    @Autowired private val rulesRepository: RulesRepository
) {
    private val bucketClient = clientBuilder.baseUrl("http://asset-service:8080").build()
    private val logger = LogManager.getLogger(RulesService::class.java)

    fun getRules(
        userId: Long,
        configType: ConfigType,
    ): List<Rule> {
        val userRules = userRulesRepository.findByUserId(userId) ?: throw Exception("User not found")
        return when (configType) {
            ConfigType.FORMATTING -> rulesRepository.findAllById(userRules.formattingRules)
            ConfigType.LINTING -> rulesRepository.findAllById(userRules.lintingRules)
        }
        // Todo: Modificar para filtrar con la coleccion de ambos tipos de reglas
    }

    fun createRules(userId: Long, language: String) {
        val formattingRules = listOf(
            Rule(0L, "spaceBeforeColon", false, null),
            Rule(0L, "spaceAfterColon", false, null),
            Rule(0L, "spaceAroundEquals", false, null),
            Rule(0L,"newlineBeforePrintln", false, "0")
        )
        val lintingRules = listOf(
            Rule(0L ,"identifier_format", false, "camel case")
            // todo: Agregar Linting rule que falta de PrintScript
        )
        val formatting: List<Rule> = rulesRepository.saveAll(formattingRules)
        val linting: List<Rule> = rulesRepository.saveAll(lintingRules)
        val formattingId: List<Long> = formatting.map { it.id }
        val lintingId: List<Long> = linting.map { it.id }
        val user: UserRules = userRulesFactory.buildUserRules(language, userId, formattingId, lintingId)
        logger.info(user)
        userRulesRepository.save(user)
    }

    fun updateRules(userId: Long, language: String, configType: ConfigType, rules: List<Rule>): String {
        val userRules = userRulesRepository.findByUserId(userId) ?: throw Exception("User not found")
        val rulesIds = rulesRepository.saveAll(rules).map { it.id }
        // todo: Hacer que se mantenga el orden de las reglas
        when (configType) {
            ConfigType.FORMATTING -> {
                userRules.formattingRules = rulesIds
            }
            ConfigType.LINTING -> {
                userRules.lintingRules = rulesIds
            }
        }
        userRulesRepository.save(userRules)
        // todo: Manejar que esto retorne el JSON en string
        return ""
    }

    private fun convertToJson() {
        // todo: Tener en cuenta que formating y linting reciben jsons distintos
    }
}