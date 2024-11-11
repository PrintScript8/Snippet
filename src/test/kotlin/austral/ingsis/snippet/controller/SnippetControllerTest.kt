package austral.ingsis.snippet.controller

import austral.ingsis.snippet.message.RedisMessageEmitter
import austral.ingsis.snippet.model.CommunicationSnippet
import austral.ingsis.snippet.model.ComplianceEnum
import austral.ingsis.snippet.service.AuthService
import austral.ingsis.snippet.service.SnippetService
import austral.ingsis.snippet.service.ValidationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus

@RestClientTest(SnippetController::class)
class SnippetControllerTest {
    @Autowired
    private lateinit var mockServer: MockRestServiceServer

    @Autowired
    private lateinit var snippetController: SnippetController

    @MockBean
    private lateinit var snippetService: SnippetService

    @MockBean
    private lateinit var validationService: ValidationService

    @MockBean
    private lateinit var authService: AuthService

    @MockBean
    @Suppress("UnusedPrivateProperty")
    private lateinit var messageEmitter: RedisMessageEmitter

    @Test
    fun `test createSnippet`() {
        val snippet = MessageSnippet("Test Snippet", "Kotlin", "fun main() {}", "kt")
        val userId = "1L"
        val snippetId = 0L
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "123")
        }

        `when`(authService.validateToken("123")).thenReturn("1L")
        `when`(validationService.exists("123")).thenReturn(true)
        `when`(snippetService.createSnippet(snippet.name, snippet.content, snippet.language, userId, snippet.extension, userId)).thenReturn(snippetId)

        mockServer.expect(requestTo("http://permission-service:8080/users/snippets/$snippetId"))
            .andExpect(method(HttpMethod.PUT))
            .andExpect(header("Authorization", "123"))
            .andRespond(withStatus(HttpStatus.OK))

        val response = snippetController.createSnippet(snippet, request.getHeader("Authorization")!!)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(snippetId, response.body)
    }

    @Test
    fun `test paginatedSnippets`() {
        val userId = "1L"
        val page = 0
        val pageSize = 10
        val snippetName = "Test"

        `when`(validationService.exists("123")).thenReturn(true)
        `when`(validationService.canRead(1L, "123")).thenReturn(true)

        val snippets = listOf(
            CommunicationSnippet(
                1L,
                "Test Snippet",
                "Kotlin",
                userId,
                "kt",
                "kt",
                ComplianceEnum.COMPLIANT,
            )
        )
        `when`(snippetService.paginatedSnippets(page, pageSize, snippetName)).thenReturn(snippets)

        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "123")

        val authorizationHeader = request.getHeader("Authorization")!!
        val response = snippetController.paginatedSnippets(page, pageSize, snippetName, authorizationHeader)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(PaginationSnippet(page, pageSize, snippets.size, snippets), response.body)
    }

    @Test
    fun `test setSnippetStatus`() {
        val userId = "1L"
        val setStatus = SetStatus(1L, "active")

        `when`(validationService.canModify(setStatus.id, "123")).thenReturn(true)

        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "123")
        }


        val response = snippetController.setSnippetStatus(setStatus, request.getHeader("Authorization")!!)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `test getSnippetById`() {
        val userId = "1L"
        val snippetId = 1L
        val snippet = CommunicationSnippet(
            snippetId,
            "Test Snippet",
            "Kotlin",
            userId,
            "kt",
            "kt",
            ComplianceEnum.COMPLIANT,
        )

        `when`(validationService.canRead(snippetId, "123")).thenReturn(true)
        `when`(snippetService.getSnippetById(snippetId)).thenReturn(snippet)

        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "123")
        }

        val response = snippetController.getSnippetById(snippetId, request.getHeader("Authorization")!!)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(snippet, response.body)
    }

    @Test
    fun `test updateSnippet`() {
        val userId = "1L"
        val snippetId = 1L
        val content = "Updated content"
        val language = "Kotlin"
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "123")
        }

        `when`(validationService.canModify(snippetId, "123")).thenReturn(true)

        val response = snippetController.updateSnippet(snippetId, Content(content), language, request.getHeader("Authorization")!!)
        assertEquals(HttpStatus.OK, response.statusCode)
    }
}