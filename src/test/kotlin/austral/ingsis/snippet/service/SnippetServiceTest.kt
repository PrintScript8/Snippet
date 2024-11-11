package austral.ingsis.snippet.service

import austral.ingsis.snippet.model.ComplianceEnum
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.repository.SnippetRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(SnippetService::class)
class SnippetServiceTest {
    @Autowired
    private lateinit var snippetService: SnippetService

    @MockBean
    private lateinit var snippetRepository: SnippetRepository

    @MockBean
    private lateinit var testService: TestService

    @Autowired
    private lateinit var mockServer: MockRestServiceServer

    @Test
    fun `test getSnippetById`() {
        val snippetId = 1L
        val snippet = Snippet(snippetId, "Test Snippet", "Kotlin", 1L, "kt", ComplianceEnum.PENDING)
        `when`(snippetRepository.getReferenceById(snippetId)).thenReturn(snippet)

        mockServer.expect(ExpectedCount.once(), requestTo("http://asset-service:8080/v1/asset/snippet/$snippetId"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("fun main() {}", MediaType.TEXT_PLAIN))

        val result = snippetService.getSnippetById(snippetId)

        assertNotNull(result)
        assertEquals(snippetId, result?.id)
        assertEquals("fun main() {}", result?.content)
    }

    @Test
    fun `test createSnippet`() {
        val snippetId = 1L
        val snippet = Snippet(snippetId, "Test Snippet", "Kotlin", 1L, "kt", ComplianceEnum.PENDING)
        `when`(snippetRepository.save(any(Snippet::class.java))).thenReturn(snippet)

        mockServer.expect(ExpectedCount.once(), requestTo("http://parser-service:8080/parser/validate"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.OK))

        mockServer.expect(ExpectedCount.once(), requestTo("http://asset-service:8080/v1/asset/snippet/$snippetId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.OK))

        val result = snippetService.createSnippet("Test Snippet", "fun main() {}", "Kotlin", 1L, "kt")

        assertEquals(snippetId, result)
    }

    @Test
    fun `test updateSnippet`() {
        val snippetId = 1L
        val snippet = Snippet(snippetId, "Test Snippet", "Kotlin", 1L, "kt", ComplianceEnum.PENDING)
        `when`(snippetRepository.getReferenceById(snippetId)).thenReturn(snippet)

        mockServer.expect(ExpectedCount.once(), requestTo("http://parser-service:8080/parser/validate"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.OK))

        mockServer.expect(ExpectedCount.once(), requestTo("http://asset-service:8080/v1/asset/snippet/$snippetId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.OK))

        snippetService.updateSnippet(snippetId, "fun main() {}", "Kotlin")

        verify(snippetRepository, times(2)).save(snippet)
    }

    @Test
    fun `test deleteSnippet`() {
        val snippetId = 1L

        mockServer.expect(ExpectedCount.once(), requestTo("http://asset-service:8080/v1/asset/snippet/$snippetId"))
            .andExpect(method(HttpMethod.DELETE))
            .andRespond(withStatus(HttpStatus.OK))

        doNothing().`when`(snippetRepository).deleteById(snippetId)
        doNothing().`when`(testService).deleteAllTests(snippetId)

        snippetService.deleteSnippet(snippetId)

        verify(snippetRepository, times(1)).deleteById(snippetId)
        verify(testService, times(1)).deleteAllTests(snippetId)
    }

    @Test
    fun `test setSnippetStatus to non-compliant`() {
        val snippetId = 2L
        val snippet = Snippet(snippetId, "Test Snippet 2", "Kotlin", 2L, "kt", ComplianceEnum.PENDING)
        `when`(snippetRepository.getReferenceById(snippetId)).thenReturn(snippet)

        snippetService.setSnippetStatus(snippetId, "non-compliant")

        assertEquals(ComplianceEnum.NON_COMPLIANT, snippet.status)
        verify(snippetRepository, times(1)).saveAndFlush(snippet)
    }

    @Test
    fun `test setSnippetStatus to failed`() {
        val snippetId = 3L
        val snippet = Snippet(snippetId, "Test Snippet 3", "Kotlin", 3L, "kt", ComplianceEnum.PENDING)
        `when`(snippetRepository.getReferenceById(snippetId)).thenReturn(snippet)

        snippetService.setSnippetStatus(snippetId, "failed")

        assertEquals(ComplianceEnum.FAILED, snippet.status)
        verify(snippetRepository, times(1)).saveAndFlush(snippet)
    }

    @Test
    fun `test paginatedSnippets`() {
        val snippet1 = Snippet(1L, "Snippet 1", "Kotlin", 1L, "kt", ComplianceEnum.COMPLIANT)
        val snippet2 = Snippet(2L, "Snippet 2", "Kotlin", 1L, "kt", ComplianceEnum.COMPLIANT)
        val snippets = listOf(snippet1, snippet2)
        `when`(snippetRepository.findAll()).thenReturn(snippets)

        mockServer.expect(ExpectedCount.once(), requestTo("http://asset-service:8080/v1/asset/snippet/1"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("fun main() {}", MediaType.TEXT_PLAIN))

        mockServer.expect(ExpectedCount.once(), requestTo("http://asset-service:8080/v1/asset/snippet/2"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("fun main() {}", MediaType.TEXT_PLAIN))

        val result = snippetService.paginatedSnippets(0, 2, "")

        assertEquals(2, result.size)
        assertEquals("Snippet 1", result[0].name)
        assertEquals("Snippet 2", result[1].name)
    }
}
