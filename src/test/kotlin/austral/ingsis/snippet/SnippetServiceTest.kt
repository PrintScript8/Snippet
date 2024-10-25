package austral.ingsis.snippet

import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.service.SnippetService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.RequestBodyUriSpec

@RestClientTest(SnippetService::class)
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

    @Autowired
    private lateinit var snippetService: SnippetService

    private val snippet = Snippet(1L, "name", "description", "code", "language", 1L, "config")

    @BeforeEach
    fun setUp() {
        snippetService.client = client
    }

    @Test
    fun `should call the asset service to get snippet by id`() {
        // Mocking method calls for HTTP request flow
        `when`(client.get()).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.uri("/v1/asset/{container}/{key}", "snippet", 1L)).thenReturn(requestHeadersSpec)
        `when`(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.body(Snippet::class.java)).thenReturn(snippet)

        // Call the service method
        snippetService.getSnippetById(1L)

        // Verify interactions
        verify(client, times(1)).get()
        verify(requestHeadersUriSpec, times(1)).uri("/v1/asset/{container}/{key}", "snippet", 1L)
        verify(requestHeadersSpec, times(1)).retrieve()
        verify(responseSpec, times(1)).body(Snippet::class.java)
    }

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

    @Test
    fun `should call the asset service to update snippet`() {
        // Mocking method calls for HTTP request flow
        `when`(client.put()).thenReturn(requestBodyUriSpec)
        `when`(requestBodyUriSpec.uri("/v1/asset/{container}/{key}", "snippet", 1L)).thenReturn(requestBodySpec)
        `when`(requestBodySpec.body(snippet)).thenReturn(requestBodySpec)
        `when`(requestBodySpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.body(Snippet::class.java)).thenReturn(snippet)

        // Call the service method
        snippetService.updateSnippet(1L, "name", "description", "code", "language", 1L, "config")

        // Verify interactions
        verify(client, times(1)).put()
        verify(requestBodyUriSpec, times(1)).uri("/v1/asset/{container}/{key}", "snippet", 1L)
        verify(requestBodySpec, times(1)).body(snippet)
        verify(requestBodySpec, times(1)).retrieve()
        verify(responseSpec, times(1)).body(Snippet::class.java)
    }
}
