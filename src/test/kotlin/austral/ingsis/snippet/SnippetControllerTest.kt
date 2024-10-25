package austral.ingsis.snippet

import austral.ingsis.snippet.controller.SnippetController
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.service.SnippetService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(SnippetController::class) // Tests only the controller
class SnippetControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var snippetService: SnippetService // Mock the service layer

    @Test
    fun `should return snippet by id`() {
        // Arrange - Mock the service to return a specific snippet
        val snippet = Snippet(1, "First Snippet", "Content of the first snippet")
        every { snippetService.getSnippetById(1) } returns snippet

        // Act - Make a GET request to /snippets/1
        mockMvc.perform(get("/snippets/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("First Snippet"))

        // Assert - Verify the service method was called once
        verify(exactly = 1) { snippetService.getSnippetById(1) }
    }

    @Test
    fun `should delete snippet by id`() {
        // Arrange - Mock the service to do nothing for delete
        every { snippetService.deleteSnippet(1) } returns Unit

        // Act - Make a DELETE request to /snippets/1
        mockMvc.perform(delete("/snippets/1"))
            .andExpect(status().isNoContent)

        // Assert - Verify the service method was called
        verify(exactly = 1) { snippetService.deleteSnippet(1) }
    }

@Test
fun `should create snippet`() {
    // Arrange - Mock the service to handle snippet creation and retrieval
    every { snippetService.updateSnippet(1, "Name", "Date") } returns Unit
    every { snippetService.getSnippetById(1) } returns Snippet(1, "Name", "Date")

    // Act - Make a PUT request to create the snippet
    mockMvc.perform(put("/snippets/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"id\": 1, \"name\": \"Name\", \"creationDate\": \"Date\"}"))
        .andExpect(status().isOk)

    // Act - Make a GET request to retrieve the created snippet
    mockMvc.perform(get("/snippets/1"))
        .andExpect(status().isOk)
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value("Name"))
        .andExpect(jsonPath("$.creationDate").value("Date"))

    // Assert - Verify the service methods were called
    verify(exactly = 1) { snippetService.updateSnippet(1, "Name", "Date") }
    verify(exactly = 1) { snippetService.getSnippetById(1) }
}

}
