package austral.ingsis.snippet.service

import austral.ingsis.snippet.factory.UserRulesFactory
import austral.ingsis.snippet.model.ConfigType
import austral.ingsis.snippet.model.Rule
import austral.ingsis.snippet.model.UserRules
import austral.ingsis.snippet.repository.RulesRepository
import austral.ingsis.snippet.repository.UserRulesRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.anyList
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.web.client.RestClient

class RulesServiceTest {
    private lateinit var rulesService: RulesService
    private lateinit var userRulesFactory: UserRulesFactory
    private lateinit var clientBuilder: RestClient.Builder
    private lateinit var userRulesRepository: UserRulesRepository
    private lateinit var rulesRepository: RulesRepository

    @BeforeEach
    fun setUp() {
        userRulesFactory = mock(UserRulesFactory::class.java)
        clientBuilder = mock(RestClient.Builder::class.java)
        userRulesRepository = mock(UserRulesRepository::class.java)
        rulesRepository = mock(RulesRepository::class.java)

        rulesService = RulesService(userRulesFactory, userRulesRepository, rulesRepository)
    }

    @Test
    fun `test getRules returns correct rules based on config type`() {
        val userId = "1L"
        val configType = ConfigType.FORMATTING
        val userRules = UserRules(0L, userId, "java", listOf(1L, 2L))
        val rule1 = Rule(1L, "spaceBeforeColon", false, null, ConfigType.FORMATTING)
        val rule2 = Rule(2L, "mandatory-variable-or-literal-in-println", false, null, ConfigType.LINTING)

        `when`(userRulesRepository.findByUserId(userId)).thenReturn(userRules)
        `when`(rulesRepository.findAllById(userRules.allRules)).thenReturn(listOf(rule1, rule2))

        val result = rulesService.getRules(userId, configType)
        assert(result == listOf(rule1)) // Only formatting rule should be returned
    }

    @Test
    fun `test getRules throws exception if user not found`() {
        val userId = "1L"
        `when`(userRulesRepository.findByUserId(userId)).thenReturn(null)

        assertThrows<Exception> { rulesService.getRules(userId, ConfigType.FORMATTING) }
    }

    @Test
    fun `test createRules creates and saves formatting and linting rules`() {
        val userId = "1L"
        val language = "java"
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
        val userRules = UserRules(0L, userId, language, listOf(1L, 2L, 3L, 4L))

        `when`(rulesRepository.saveAll(anyList())).thenReturn(formattingRules + lintingRules)
        `when`(userRulesFactory.buildUserRules(anyString(), anyString(), anyList())).thenReturn(userRules)

        rulesService.createRules(userId, language)

        verify(userRulesRepository).save(userRules)
    }

    @Test
    fun `test updateRules updates rules and returns JSON for formatting config type`() {
        val userId = "1L"
        val language = "java"
        val configType = ConfigType.FORMATTING
        val rule1 = Rule(1L, "spaceBeforeColon", false, null, configType)
        val userRules = UserRules(0L, userId, language, mutableListOf(1L))

        `when`(userRulesRepository.findByUserId(userId)).thenReturn(userRules)
        `when`(rulesRepository.saveAll(anyList())).thenReturn(listOf(rule1))
        `when`(rulesRepository.findAllById(userRules.allRules)).thenReturn(listOf(rule1))

        val result = rulesService.updateRules(userId, language, configType, listOf(rule1))
        assert(result.contains("spaceBeforeColon"))
    }

    @Test
    fun `test getFormatJson returns formatted JSON string for formatting rules`() {
        val userId = "1L"
        val userRules = UserRules(0L, userId, "java", listOf(1L, 2L))
        val rule1 = Rule(1L, "spaceBeforeColon", false, null, ConfigType.FORMATTING)
        val rule2 = Rule(2L, "spaceAfterColon", false, null, ConfigType.FORMATTING)

        `when`(userRulesRepository.findByUserId(userId)).thenReturn(userRules)
        `when`(rulesRepository.findAllById(userRules.allRules)).thenReturn(listOf(rule1, rule2))

        val result = rulesService.getFormatJson(userId)
        assert(result.contains("spaceBeforeColon") && result.contains("spaceAfterColon"))
    }
}
