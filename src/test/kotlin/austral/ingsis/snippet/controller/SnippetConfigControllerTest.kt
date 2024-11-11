package austral.ingsis.snippet.controller

import austral.ingsis.snippet.message.RedisMessageEmitter
import austral.ingsis.snippet.model.ConfigType
import austral.ingsis.snippet.model.Rule
import austral.ingsis.snippet.service.RulesService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
                addHeader("id", "123")
            }

        `when`(rulesService.getRules(123L, ConfigType.LINTING)).thenReturn(rules)

        mockServer.expect(requestTo("/snippets/config/linting"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("id", "123"))
            .andRespond(withSuccess(objectMapper.writeValueAsString(rules), MediaType.APPLICATION_JSON))

        val response = snippetConfigController.getLintingConfig(request, "java")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(rules, response.body)
    }

    @Test
    fun `test getFormattingConfig`() {
        val rules = listOf(Rule(0L, "rule", false, null, ConfigType.FORMATTING))
        val request =
            MockHttpServletRequest().apply {
                addHeader("id", "123")
            }

        `when`(rulesService.getRules(123L, ConfigType.FORMATTING)).thenReturn(rules)

        mockServer.expect(requestTo("/snippets/config/formatting"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("id", "123"))
            .andRespond(withSuccess(objectMapper.writeValueAsString(rules), MediaType.APPLICATION_JSON))

        val response = snippetConfigController.getFormattingConfig(request, "java")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(rules, response.body)
    }

    @Test
    fun `test updateFormattingRules`() {
        val rules = listOf(Rule(0L, "rule", false, null, ConfigType.FORMATTING))
        val request =
            MockHttpServletRequest().apply {
                addHeader("id", "123")
            }

        `when`(rulesService.updateRules(123L, "java", ConfigType.FORMATTING, rules)).thenReturn("updatedRulesJson")

        snippetConfigController.updateFormattingRules(request, rules, "java")

        verify(messageEmitter).publishEvent(123L, "java", "updatedRulesJson", "format")
    }

    @Test
    fun `test updateLintingRules`() {
        val rules = listOf(Rule(0L, "rule", false, null, ConfigType.LINTING))
        val request =
            MockHttpServletRequest().apply {
                addHeader("id", "123")
            }

        `when`(rulesService.updateRules(123L, "java", ConfigType.LINTING, rules)).thenReturn("updatedRulesJson")

        snippetConfigController.updateLintingRules(request, rules, "java")

        verify(messageEmitter).publishEvent(123L, "java", "updatedRulesJson", "lint")
    }

    @Test
    fun `test initializeRules`() {
        val userId = 123L
        snippetConfigController.initializeRules(userId, "java")

        verify(rulesService).createRules(userId, "java")
    }
}
