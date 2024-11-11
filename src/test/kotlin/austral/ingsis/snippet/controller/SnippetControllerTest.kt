package austral.ingsis.snippet.controller

import austral.ingsis.snippet.message.RedisMessageEmitter
import austral.ingsis.snippet.model.CommunicationSnippet
import austral.ingsis.snippet.model.ComplianceEnum
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
    @Suppress("UnusedPrivateProperty")
    private lateinit var messageEmitter: RedisMessageEmitter

    @Test
    fun `test createSnippet`() {
        val snippet = MessageSnippet("Test Snippet", "Kotlin", "fun main() {}", "kt")
        val userId = 1L
        val snippetId = 1L

        // Mock validation service
        `when`(validationService.exists(userId)).thenReturn(true)

        // Mock snippet service
        `when`(snippetService.createSnippet(snippet.name, snippet.content, snippet.language, userId, snippet.extension))
            .thenReturn(snippetId)

        // Mock permission client
        mockServer.expect(requestTo("http://permission-service:8080/users/snippets/$snippetId"))
            .andExpect(method(HttpMethod.PUT))
            .andExpect(header("id", userId.toString()))
            .andRespond(withStatus(HttpStatus.OK))

        val request = MockHttpServletRequest()
        request.addHeader("id", userId.toString())

        val response = snippetController.createSnippet(snippet, request)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(snippetId, response.body)
    }

    @Test
    fun `test paginatedSnippets`() {
        val userId = 1L
        val page = 0
        val pageSize = 10
        val snippetName = "Test"

        // Mock validation service
        `when`(validationService.exists(userId)).thenReturn(true)
        `when`(validationService.canRead(userId, 1L)).thenReturn(true)

        // Mock snippet service
        val snippets =
            listOf(
                CommunicationSnippet(
                    1L,
                    "Test Snippet",
                    "Kotlin",
                    1,
                    "kt",
                    "kt",
                    ComplianceEnum.COMPLIANT,
                ),
            )
        `when`(snippetService.paginatedSnippets(page, pageSize, snippetName)).thenReturn(snippets)

        val request = MockHttpServletRequest()
        request.addHeader("id", userId.toString())

        val response = snippetController.paginatedSnippets(page, pageSize, snippetName, request)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(PaginationSnippet(page, pageSize, snippets.size, snippets), response.body)
    }

    @Test
    fun `test setSnippetStatus`() {
        val userId = 1L
        val setStatus = SetStatus(1L, "active")

        // Mock validation service
        `when`(validationService.canModify(userId, setStatus.id)).thenReturn(true)

        val request = MockHttpServletRequest()
        request.addHeader("id", userId.toString())

        val response = snippetController.setSnippetStatus(setStatus, request)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `test getSnippetById`() {
        val userId = 1L
        val snippetId = 1L
        val snippet =
            CommunicationSnippet(
                snippetId,
                "Test Snippet",
                "Kotlin",
                userId,
                "kt",
                "kt",
                ComplianceEnum.COMPLIANT,
            )

        // Mock validation service
        `when`(validationService.canRead(userId, snippetId)).thenReturn(true)

        // Mock snippet service
        `when`(snippetService.getSnippetById(snippetId)).thenReturn(snippet)

        val request = MockHttpServletRequest()
        request.addHeader("id", userId.toString())

        val response = snippetController.getSnippetById(snippetId, request)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(snippet, response.body)
    }

    @Test
    fun `test updateSnippet`() {
        val userId = 1L
        val snippetId = 1L
        val content = Content("Updated content")
        val language = "Kotlin"

        // Mock validation service
        `when`(validationService.canModify(userId, snippetId)).thenReturn(true)

        val request = MockHttpServletRequest()
        request.addHeader("id", userId.toString())

        val response = snippetController.updateSnippet(snippetId, content, language, request)
        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
