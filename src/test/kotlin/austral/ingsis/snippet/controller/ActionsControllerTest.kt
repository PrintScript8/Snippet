package austral.ingsis.snippet.controller

import austral.ingsis.snippet.service.AuthService
import austral.ingsis.snippet.service.RulesService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import kotlin.test.assertEquals

@RestClientTest(ActionsController::class)
@Import(ActionsController::class)
class ActionsControllerTest {
    @Autowired
    private lateinit var mockServer: MockRestServiceServer

    @Autowired
    private lateinit var actionsController: ActionsController

    @MockBean
    private lateinit var rulesService: RulesService

    @MockBean
    private lateinit var authService: AuthService

    @Test
    fun `test getType`() {
        val request =
            MockHttpServletRequest().apply {
                addHeader("Authorization", "1")
            }

        `when`(authService.validateToken("1")).thenReturn("validUserId")
        val response = actionsController.getType(request.getHeader("Authorization")!!)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf(FileType("printscript", "prs")), response.body)
    }

    @Test
    fun `test formatSnippet`() {
        val request =
            MockHttpServletRequest().apply {
                addHeader("Authorization", "1")
            }
        val action = FormatAction("code", "language", "1")
        val json = "{}"

        `when`(authService.validateToken("1")).thenReturn("1L")

        `when`(rulesService.getFormatJson("1L")).thenReturn(json)

        mockServer.expect(requestTo("http://parser-service:8080/parser/format"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.OK))

        val response = actionsController.formatSnippet(action, request.getHeader("Authorization")!!)

        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
