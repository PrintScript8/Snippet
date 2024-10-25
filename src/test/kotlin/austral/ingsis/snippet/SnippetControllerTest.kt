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
        val snippet =
            Snippet(
                1L,
                "First Snippet",
                "First Description",
                "First Code",
                "First Language",
                1L,
                "First Config",
            )
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
            .andExpect(status().isOk)

        // Assert - Verify the service method was called
        verify(exactly = 1) { snippetService.deleteSnippet(1) }
    }

    @Test
    fun `should create snippet`() {
        // Arrange - Mock the service to handle snippet creation
        val snippet =
            Snippet(
                0L,
                "First Snippet",
                "First Description",
                "First Code",
                "First Language",
                1L,
                "First Config",
            )
        val id = (snippet.name + snippet.code + snippet.description).hashCode().toLong()
        every {
            snippetService.updateSnippet(
                id,
                snippet.name,
                snippet.description,
                snippet.code,
                snippet.language,
                snippet.ownerId,
                snippet.config,
            )
        } returns Unit

        // Act - Make a POST request to create the snippet
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
                        "ownerId": 1,
                        "config": "First Config"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(content().string(id.toString()))

        // Assert - Verify the service method was called
        verify(exactly = 1) {
            snippetService.updateSnippet(
                id,
                snippet.name,
                snippet.description,
                snippet.code,
                snippet.language,
                snippet.ownerId,
                snippet.config,
            )
        }
    }

    @Test
    fun `should update snippet`() {
        // Arrange - Mock the service to handle snippet update
        val snippet =
            Snippet(
                1L,
                "First Snippet",
                "First Description",
                "First Code",
                "First Language",
                1L,
                "First Config",
            )
        every {
            snippetService.updateSnippet(
                1,
                snippet.name,
                snippet.description,
                snippet.code,
                snippet.language,
                snippet.ownerId,
                snippet.config,
            )
        } returns Unit

        // Act - Make a PUT request to update the snippet
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
                        "ownerId": 1,
                        "config": "First Config"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)

        // Assert - Verify the service method was called
        verify(exactly = 1) {
            snippetService.updateSnippet(
                1,
                snippet.name,
                snippet.description,
                snippet.code,
                snippet.language,
                snippet.ownerId,
                snippet.config,
            )
        }
    }
}
