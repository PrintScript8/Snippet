package austral.ingsis.snippet

import austral.ingsis.snippet.controller.CommunicationController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CommunicationController::class)
class CommunicationControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should respond with greetings message`() {
        mockMvc.perform(get("/testRespondMessage"))
            .andExpect(status().isOk)
            .andExpect(content().string("Greetings from Snippet!"))
    }

    @Test
    fun `should call endpoint and return response`() {
        mockMvc.perform(get("/testRequestMessage"))
            .andExpect(status().isOk)
            .andExpect(content().string("Greetings from Snippet!"))
    }
}
