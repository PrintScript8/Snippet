package austral.ingsis.snippet.service

import austral.ingsis.snippet.model.CommunicationSnippet
import austral.ingsis.snippet.model.ComplianceEnum
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.repository.SnippetRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.RequestBodyUriSpec

class SnippetServiceTest {
    @Mock
    private lateinit var requestHeadersUriSpec: RestClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestBodyUriSpec: RequestBodyUriSpec

    @Mock
    private lateinit var requestBodySpec: RestClient.RequestBodySpec

    @Mock
    private lateinit var requestHeadersSpec: RestClient.RequestHeadersSpec<*>

    @Mock
    private lateinit var responseSpec: RestClient.ResponseSpec

    @Mock
    private lateinit var client: RestClient

    @Mock
    private lateinit var builder: RestClient.Builder

    @Mock
    private lateinit var snippetRepository: SnippetRepository

    @Mock
    private lateinit var testService: TestService

    private lateinit var snippetService: SnippetService

    private val snippet = CommunicationSnippet(1L, "", "", 1L, "", "", ComplianceEnum.COMPLIANT)

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(builder.baseUrl(anyString())).thenReturn(builder)
        `when`(builder.build()).thenReturn(client)
        snippetService = SnippetService(
            builder, snippetRepository, testService
        )
    }
/*
    @Test
    fun `should call the asset service to get snippet by id`() {
        // Mocking method calls for HTTP request flow
        `when`(client.get()).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.uri("/v1/asset/{container}/{key}", "snippet", 1L)).thenReturn(requestHeadersSpec)
        `when`(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.body(CommunicationSnippet::class.java)).thenReturn(snippet)

        // Call the service method
        snippetService.getSnippetById(1L)

        // Verify interactions
        verify(client, times(1)).get()
        verify(requestHeadersUriSpec, times(1)).uri("/v1/asset/{container}/{key}", "snippet", 1L)
        verify(requestHeadersSpec, times(1)).retrieve()
        verify(responseSpec, times(1)).body(Snippet::class.java)
    }
*/
    @Test
    fun `should call the asset service to delete snippet`() {
        // Mocking method calls for HTTP request flow
        `when`(client.delete()).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.uri("/v1/asset/{container}/{key}", "snippet", 1L)).thenReturn(requestHeadersSpec)
        `when`(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.body(Void::class.java)).thenReturn(null)

        // Call the service method
        snippetService.deleteSnippet(1L)

        // Verify interactions
        verify(client, times(1)).delete()
        verify(requestHeadersUriSpec, times(1)).uri("/v1/asset/{container}/{key}", "snippet", 1L)
        verify(requestHeadersSpec, times(1)).retrieve()
        verify(responseSpec, times(1)).body(Void::class.java)
    }
/*
    @Test
    fun `should call the asset service to update snippet`() {
        // Mocking method calls for HTTP request flow
        `when`(client.put()).thenReturn(requestBodyUriSpec)
        `when`(requestBodyUriSpec.uri("/v1/asset/{container}/{key}", "snippet", 1L)).thenReturn(requestBodySpec)
        `when`(requestBodyUriSpec.uri("/parser/validate")).thenReturn(requestBodySpec)
        `when`(requestBodySpec.body(snippet)).thenReturn(requestBodySpec)
        `when`(requestBodySpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build())

        // Call the service method
        snippetService.updateSnippet(1L,  "let n:Number = 5", "PrintScript")

        // Verify interactions
        verify(client, times(2)).put()
        verify(requestBodyUriSpec, times(1)).uri("/v1/asset/{container}/{key}", "snippet", 1L)
        verify(requestBodySpec, times(2)).body(snippet)
        verify(requestBodySpec, times(2)).retrieve()
        verify(responseSpec, times(1)).body(Snippet::class.java)
        verify(responseSpec, times(1)).toBodilessEntity()
        verify(requestBodyUriSpec, times(1)).uri("/parser/validate")
    }
 */
}
