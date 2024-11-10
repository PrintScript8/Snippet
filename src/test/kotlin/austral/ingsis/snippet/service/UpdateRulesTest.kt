package austral.ingsis.snippet.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.any
import org.mockito.Mockito.eq
import org.mockito.Mockito.verify
import austral.ingsis.snippet.model.ConfigType
import austral.ingsis.snippet.model.UserRules
import austral.ingsis.snippet.model.Rule
import austral.ingsis.snippet.service.RulesService
import austral.ingsis.snippet.repository.RulesRepository
import austral.ingsis.snippet.repository.UserRulesRepository
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UpdateRulesTest {

    @Mock
    private lateinit var userRulesRepository: UserRulesRepository

    @Mock
    private lateinit var rulesRepository: RulesRepository

    @InjectMocks
    private lateinit var rulesService: RulesService

    private val userId = 1L
    private val language = "kotlin"

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should return JSON formatted string for formatting rules`() {
        // Arrange
        val userRules = UserRules(0L, userId, language, mutableListOf(1L, 2L))
        val formattingRules = listOf(
            Rule(1L, "spaceBeforeColon", true, null, ConfigType.FORMATTING),
            Rule(2L, "newlineBeforePrintln", true, "2", ConfigType.FORMATTING)
        )

        `when`(userRulesRepository.findByUserId(userId)).thenReturn(userRules)
        `when`(rulesRepository.saveAll(any<List<Rule>>())).thenReturn(formattingRules)
        `when`(rulesRepository.findAllById(userRules.allRules)).thenReturn(formattingRules)

        // Act
        val result = rulesService.updateRules(userId, language, ConfigType.FORMATTING, formattingRules)

        // Assert
        val expectedJson = """{ "rules": { "spaceBeforeColon": true, "newlineBeforePrintln": "2" } }"""
        assertEquals(expectedJson, result)

        verify(userRulesRepository).save(eq(userRules))
        // verify(rulesRepository).saveAll(any())
    }

    @Test
    fun `should return JSON linting formatted string for linting rules`() {
        // Arrange
        val userRules = UserRules(0L, userId, language, mutableListOf(1L, 2L))
        val lintingRules = listOf(
            Rule(1L, "identifier_format", true, "camel case", ConfigType.LINTING),
            Rule(2L, "mandatory-variable-or-literal-in-println", true, "true", ConfigType.LINTING)
        )

        `when`(userRulesRepository.findByUserId(userId)).thenReturn(userRules)
        `when`(rulesRepository.saveAll(any<List<Rule>>())).thenReturn(lintingRules)
        `when`(rulesRepository.findAllById(userRules.allRules)).thenReturn(lintingRules)

        // Act
        val result = rulesService.updateRules(userId, language, ConfigType.LINTING, lintingRules)

        // Assert
        val expectedJson = """{ "identifier_format": "camel case", "mandatory-variable-or-literal-in-println": "true" }"""
        assertEquals(expectedJson, result)

        verify(userRulesRepository).save(eq(userRules))
        // verify(rulesRepository).saveAll(any())
    }
}


