package austral.ingsis.snippet.service

import austral.ingsis.snippet.factory.UserRulesFactory
import austral.ingsis.snippet.model.ConfigType
import austral.ingsis.snippet.model.UserRules
import austral.ingsis.snippet.repository.RulesRepository
import austral.ingsis.snippet.model.Rule
import austral.ingsis.snippet.repository.UserRulesRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.web.client.RestClient

class RulesServiceTest {





    /*
    @Mock
    private lateinit var userRulesFactory: UserRulesFactory

    @Mock
    private lateinit var clientBuilder: RestClient.Builder

    @Mock
    private lateinit var bucketClient: RestClient

    @Mock
    private lateinit var userRulesRepository: UserRulesRepository

    @Mock
    private lateinit var rulesRepository: RulesRepository

    @Mock
    private lateinit var requestHeadersUriSpec: RestClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestBodyUriSpec: RestClient.RequestBodyUriSpec

    @Mock
    private lateinit var requestBodySpec: RestClient.RequestBodySpec

    @Mock
    private lateinit var responseSpec: RestClient.ResponseSpec

    private lateinit var rulesService: RulesService

    private val userId = 1L
    private val language = "kotlin"
    private val lintingConfig = "lintingConfig"
    private val formattingConfig = "formattingConfig"

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(clientBuilder.baseUrl(anyString())).thenReturn(clientBuilder)
        `when`(clientBuilder.build()).thenReturn(bucketClient)
        rulesService = RulesService(
            userRulesFactory, clientBuilder,
            userRulesRepository = userRulesRepository,
            rulesRepository = rulesRepository
        )
    }

    @Test
    fun `should create new linting rules if not present`() {
        // Arrange
        val userRules = UserRules(0L, userId, language, emptyList<Long>())
        `when`(bucketClient.get()).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.uri(anyString(), any(), any())).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec)
        `when`(userRulesFactory.buildUserRules(language, userId, emptyList<Long>())).thenReturn(userRules)
        `when`(bucketClient.put()).thenReturn(requestBodyUriSpec)
        `when`(requestBodyUriSpec.uri(anyString(), any(), any())).thenReturn(requestBodySpec)
        `when`(requestBodySpec.body(userRules)).thenReturn(requestBodySpec)
        `when`(requestBodySpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.body(UserRules::class.java)).thenReturn(userRules)

        // Act
        rulesService.updateRules(userId, language, ConfigType.FORMATTING, emptyList<Rule>())

        // Assert
        verify(bucketClient, times(1)).put()
        verify(requestBodyUriSpec, times(1)).uri(anyString(), any(), any())
        verify(requestBodySpec, times(1)).body(userRules)
        verify(requestBodySpec, times(1)).retrieve()
    }


    @Test
    fun `should update existing linting rules`() {
        // Arrange
        val userRules = UserRules(userId, language, "", "")
        `when`(bucketClient.get()).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.uri(anyString(), any(), any())).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.body(UserRules::class.java)).thenReturn(userRules)
        `when`(bucketClient.put()).thenReturn(requestBodyUriSpec)
        `when`(requestBodyUriSpec.uri(anyString(), any(), any())).thenReturn(requestBodySpec)
        `when`(requestBodySpec.body(userRules)).thenReturn(requestBodySpec)
        `when`(requestBodySpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.body(UserRules::class.java)).thenReturn(userRules)

        // Act
        rulesService.updateLintingRules(userId, lintingConfig, language)

        // Assert
        verify(bucketClient, times(1)).put()
        verify(requestBodyUriSpec, times(1)).uri(anyString(), any(), any())
        verify(requestBodySpec, times(1)).body(userRules)
        verify(requestBodySpec, times(1)).retrieve()
    }

    @Test
    fun `should create new formatting rules if not present`() {
        // Arrange
        val userRules = UserRules(userId, language, "", formattingConfig)
        `when`(bucketClient.get()).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.uri(anyString(), any(), any())).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec)
        `when`(userRulesFactory.buildUserRules(userId, "", formattingConfig, language)).thenReturn(userRules)
        `when`(bucketClient.put()).thenReturn(requestBodyUriSpec)
        `when`(requestBodyUriSpec.uri(anyString(), any(), any())).thenReturn(requestBodySpec)
        `when`(requestBodySpec.body(userRules)).thenReturn(requestBodySpec)
        `when`(requestBodySpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.body(UserRules::class.java)).thenReturn(userRules)

        // Act
        rulesService.updateFormattingRules(userId, formattingConfig, language)

        // Assert
        verify(bucketClient, times(1)).put()
        verify(requestBodyUriSpec, times(1)).uri(anyString(), any(), any())
        verify(requestBodySpec, times(1)).body(userRules)
        verify(requestBodySpec, times(1)).retrieve()
    }

    @Test
    fun `should update existing formatting rules`() {
        // Arrange
        val userRules = UserRules(userId, language, "", "")
        `when`(bucketClient.get()).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.uri(anyString(), any(), any())).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.body(UserRules::class.java)).thenReturn(userRules)
        `when`(bucketClient.put()).thenReturn(requestBodyUriSpec)
        `when`(requestBodyUriSpec.uri(anyString(), any(), any())).thenReturn(requestBodySpec)
        `when`(requestBodySpec.body(userRules)).thenReturn(requestBodySpec)
        `when`(requestBodySpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.body(UserRules::class.java)).thenReturn(userRules)

        // Act
        rulesService.updateFormattingRules(userId, formattingConfig, language)

        // Assert
        verify(bucketClient, times(1)).put()
        verify(requestBodyUriSpec, times(1)).uri(anyString(), any(), any())
        verify(requestBodySpec, times(1)).body(userRules)
        verify(requestBodySpec, times(1)).retrieve()
    }

    @Test
    fun `should update rules and give json string`() {
        val rules: List<Rule> = listOf(
            Rule(name = "spaceBeforeColon", isActive = true, value = null, type = ConfigType.FORMATTING),
            Rule(name = "newlineBeforePrintln", isActive = true, value = "2", type = ConfigType.FORMATTING)
        )
        rulesService.updateRules(1, "ps", ConfigType.FORMATTING, rules)
    }
    */
}
