package austral.ingsis.snippet.controller

import austral.ingsis.snippet.message.RedisMessageEmitter
import austral.ingsis.snippet.service.RulesService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class SnippetAsyncControllerTest {
    private lateinit var mockMvc: MockMvc

    @Mock
    lateinit var rulesService: RulesService

    @Mock
    lateinit var messageEmitter: RedisMessageEmitter

    private lateinit var controller: SnippetAsyncController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        controller = SnippetAsyncController(rulesService, messageEmitter)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `should update linting configuration`() {
        // Arrange
        val userId = 1L
        val lintingConfig = "some-linting-config"
        val language = "kotlin"

        doNothing().`when`(rulesService).updateLintingRules(userId, lintingConfig, language)
        doNothing().`when`(messageEmitter).publishEvent(userId, language, lintingConfig, "linting")

        // Act & Assert
        mockMvc.perform(
            put("/snippets/config/linting/$userId")
                .param("lintingConfig", lintingConfig)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)

        verify(rulesService, times(1)).updateLintingRules(userId, lintingConfig, language)
        verify(messageEmitter, times(1)).publishEvent(userId, language, lintingConfig, "linting")
    }

    @Test
    fun `should update formatting configuration`() {
        // Arrange
        val userId = 1L
        val formattingConfig = "some-formatting-config"
        val language = "kotlin"

        doNothing().`when`(rulesService).updateFormattingRules(userId, formattingConfig, language)
        doNothing().`when`(messageEmitter).publishEvent(userId, language, formattingConfig, "formatting")

        // Act & Assert
        mockMvc.perform(
            put("/snippets/config/formatting/$userId")
                .param("formattingConfig", formattingConfig)
                .param("language", language)
                .contentType(MediaType.APPLICATION_JSON),
        )
            .andExpect(status().isOk)

        verify(rulesService, times(1)).updateFormattingRules(userId, formattingConfig, language)
        verify(messageEmitter, times(1)).publishEvent(userId, language, formattingConfig, "formatting")
    }
}
