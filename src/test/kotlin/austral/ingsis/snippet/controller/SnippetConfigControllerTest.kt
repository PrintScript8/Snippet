package austral.ingsis.snippet.controller

import austral.ingsis.snippet.message.RedisMessageEmitter
import austral.ingsis.snippet.model.ConfigType
import austral.ingsis.snippet.model.Rule
import austral.ingsis.snippet.service.AuthService
import austral.ingsis.snippet.service.RulesService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate

@RestClientTest(SnippetConfigController::class)
class SnippetConfigControllerTest {
    @Autowired
    private lateinit var mockServer: MockRestServiceServer

    @MockBean
    private lateinit var rulesService: RulesService

    @MockBean
    private lateinit var messageEmitter: RedisMessageEmitter

    @MockBean
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var snippetConfigController: SnippetConfigController

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @BeforeEach
    fun setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `test getLintingConfig`() {
        val rules = listOf(Rule(0L, "rule", false, null, ConfigType.LINTING))
        val request =
            MockHttpServletRequest().apply {
                addHeader("Authorization", "Bearer 123")
            }

        `when`(rulesService.getRules("123L", ConfigType.LINTING)).thenReturn(rules)
        `when`(authService.validateToken("123")).thenReturn("123L")

        mockServer.expect(requestTo("/snippets/config/linting"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer 123"))
            .andRespond(withSuccess(objectMapper.writeValueAsString(rules), MediaType.APPLICATION_JSON))

        val response =
            snippetConfigController
                .getLintingConfig(request.getHeader("Authorization")!!.substring(7), "java")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(rules, response.body)
    }

    @Test
    fun `test getFormattingConfig`() {
        val rules = listOf(Rule(0L, "rule", false, null, ConfigType.FORMATTING))
        val request =
            MockHttpServletRequest().apply {
                addHeader("Authorization", "Bearer 123")
            }

        `when`(rulesService.getRules("123L", ConfigType.FORMATTING)).thenReturn(rules)
        `when`(authService.validateToken("123")).thenReturn("123L")

        mockServer.expect(requestTo("/snippets/config/formatting"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer 123"))
            .andRespond(withSuccess(objectMapper.writeValueAsString(rules), MediaType.APPLICATION_JSON))

        val response =
            snippetConfigController
                .getFormattingConfig(request.getHeader("Authorization")!!.substring(7), "java")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(rules, response.body)
    }

    @Test
    fun `test updateFormattingRules`() {
        val rules = listOf(Rule(0L, "rule", false, null, ConfigType.FORMATTING))
        val request =
            MockHttpServletRequest().apply {
                addHeader("Authorization", "Bearer 123")
            }

        `when`(authService.validateToken("Bearer 123")).thenReturn(null)

        val exception =
            assertThrows<java.nio.file.AccessDeniedException> {
                snippetConfigController.updateFormattingRules(
                    request.getHeader("Authorization")!!.substring(7),
                    rules,
                    "java",
                )
            }

        assertEquals("Could not validate user by it's token", exception.message)
    }

    @Test
    fun `test updateLintingRules`() {
        val rules = listOf(Rule(0L, "rule", false, null, ConfigType.LINTING))
        val request =
            MockHttpServletRequest().apply {
                addHeader("Authorization", "Bearer 123")
            }

        `when`(authService.validateToken("123")).thenReturn("123L")
        `when`(rulesService.updateRules("123L", "java", ConfigType.LINTING, rules)).thenReturn("updatedRulesJson")

        snippetConfigController.updateLintingRules(request.getHeader("Authorization")!!.substring(7), rules, "java")

        verify(messageEmitter).publishEvent("123", "java", "updatedRulesJson", "lint")
    }

    @Test
    fun `test initializeRules`() {
        val userId = "123L"
        snippetConfigController.initializeRules(userId, "java")

        verify(rulesService).createRules(userId, "java")
    }
}
