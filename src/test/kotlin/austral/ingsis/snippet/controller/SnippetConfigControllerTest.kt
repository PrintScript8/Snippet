package austral.ingsis.snippet.controller

import org.mockito.Mockito.times

class SnippetConfigControllerTest {
    /*
    private lateinit var mockMvc: MockMvc

    @Mock
    lateinit var rulesService: RulesService

    @Mock
    lateinit var messageEmitter: RedisMessageEmitter

    private lateinit var controller: SnippetConfigController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        controller = SnippetConfigController(rulesService, messageEmitter)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `should update linting configuration`() {
        // Arrange
        val userId = 1L
        val lintingConfig = "some-linting-config"
        val language = "kotlin"

        doNothing().`when`(rulesService).updateRules(userId, language, ConfigType.LINTING, listOf())
        doNothing().`when`(messageEmitter).publishEvent(userId, language, lintingConfig, "linting")

        // Act & Assert
        mockMvc.perform(
            put("/snippets/config/linting/$userId")
                .param("lintingConfig", lintingConfig)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)

        verify(rulesService, times(1)).updateRules(userId, language, ConfigType.LINTING, listOf())
        verify(messageEmitter, times(1)).publishEvent(userId, language, lintingConfig, "linting")
    }

    @Test
    fun `should update formatting configuration`() {
        // Arrange
        val userId = 1L
        val formattingConfig = "some-formatting-config"
        val language = "kotlin"

        doNothing().`when`(rulesService).updateRules(userId, language, ConfigType.FORMATTING, listOf())
        doNothing().`when`(messageEmitter).publishEvent(userId, language, formattingConfig, "formatting")

        // Act & Assert
        mockMvc.perform(
            put("/snippets/config/formatting/$userId")
                .param("formattingConfig", formattingConfig)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)

        verify(rulesService, times(1)).updateRules(userId, language, ConfigType.FORMATTING, listOf())
        verify(messageEmitter, times(1)).publishEvent(userId, language, formattingConfig, "formatting")
    }

     */
}
