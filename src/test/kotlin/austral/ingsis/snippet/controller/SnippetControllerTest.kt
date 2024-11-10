package austral.ingsis.snippet.controller

import org.mockito.Mockito.times
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class SnippetControllerTest {
    /*
    private lateinit var mockMvc: MockMvc

    @Mock
    lateinit var snippetService: SnippetService

    @Mock
    lateinit var permissionClient: RestClient

    @Mock
    lateinit var clientBuilder: RestClient.Builder

    @Mock
    private lateinit var responseSpec: RestClient.ResponseSpec

    @Mock
    private lateinit var requestHeadersUriSpec: RestClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestBodyUriSpec: RequestBodyUriSpec

    @Mock
    private lateinit var requestBodySpec: RestClient.RequestBodySpec

    @Mock
    private lateinit var requestHeadersSpec: RestClient.RequestHeadersSpec<*>

    @Mock
    private lateinit var validationService: ValidationService

    private lateinit var controller: SnippetController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(clientBuilder.baseUrl(anyString())).thenReturn(clientBuilder)
        `when`(clientBuilder.build()).thenReturn(permissionClient)
        controller = SnippetController(snippetService, clientBuilder, validationService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `should return snippet by id`() {
        // Arrange
        val snippet =
            CommunicationSnippet(
                0L,
                "First Snippet",
                "PrintScript",
                1L,
                "let n:Number = 3",
                "ps",
                ComplianceEnum.COMPLIANT
            )
        `when`(snippetService.getSnippetById(1)).thenReturn(snippet)

        // Act & Assert
        mockMvc.perform(get("/snippets/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("First Snippet"))

        verify(snippetService, times(1)).getSnippetById(1)
    }

    @Test
    fun `should delete snippet by id`() {
        // Arrange
        `when`(snippetService.getSnippetById(1)).thenReturn(
        CommunicationSnippet(1, "", "", 1, "", "", ComplianceEnum.COMPLIANT))
        `when`(permissionClient.delete()).thenReturn(requestHeadersUriSpec)
        `when`(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec)
        `when`(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        doNothing().`when`(snippetService).deleteSnippet(1L)

        // Act & Assert
        mockMvc.perform(delete("/snippets/1"))
            .andExpect(status().isOk)

        verify(snippetService, times(1)).deleteSnippet(1)
    }

    @Test
    fun `should create snippet successfully`() {
        // Arrange
        val snippet =
            CommunicationSnippet(
                0L,
                "First Snippet",
                "PrintScript",
                1L,
                "let n:Number = 3",
                "ps",
                ComplianceEnum.COMPLIANT
            )
        val id = (snippet.name + snippet.content).hashCode().toLong().absoluteValue

        // Mock service and client behavior
        doNothing().`when`(snippetService).updateSnippet(
            id,
            snippet.content,
            snippet.language
        )

        // Mock chain of calls for RestClient
        `when`(permissionClient.put()).thenReturn(requestBodyUriSpec)
        `when`(requestBodyUriSpec.uri("/users/snippets/{id}/{snippetId}", snippet.ownerId, id))
            .thenReturn(requestBodySpec)
        `when`(requestBodySpec.retrieve()).thenReturn(responseSpec)

        // Act & Assert
        mockMvc.perform(
            post("/snippets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "First Snippet",
                        "description": "First Description",
                        "code": "First Code",
                        "language": "First Language",
                        "ownerId": 1
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(content().string(id.toString()))

        // Verify all interactions
        verify(snippetService, times(1)).updateSnippet(
            id,
            snippet.content,
            snippet.language
        )
        verify(permissionClient, times(1)).put()
        verify(requestBodyUriSpec, times(1)).uri("/users/snippets/{id}/{snippetId}", snippet.ownerId, id)
        verify(requestBodySpec, times(1)).retrieve()
    }

    @Test
    fun `should update snippet`() {
        // Arrange
        val snippet =
            CommunicationSnippet(
                1L,
                "First Snippet",
                "PrintScript",
                1L,
                "let n:Number = 3",
                "ps",
                ComplianceEnum.COMPLIANT
            )
        doNothing().`when`(snippetService).updateSnippet(
            1L,
            snippet.content,
            snippet.language
        )

        // Act & Assert
        mockMvc.perform(
            put("/snippets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "First Snippet",
                        "description": "First Description",
                        "code": "First Code",
                        "language": "First Language",
                        "ownerId": 1
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        verify(snippetService, times(1)).updateSnippet(
            1L,
            snippet.content,
            snippet.language
        )
    }
     */
}
