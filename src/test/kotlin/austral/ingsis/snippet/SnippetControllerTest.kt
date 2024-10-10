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
    fun `should return all snippets`() {
        // Arrange - Mock the service to return a list of snippets
        val snippets =
            listOf(
                Snippet(
                    1,
                    "First Snippet",
                    "Description",
                    "Code",
                    1L,
                    1L,
                ),
            )
        every { snippetService.getAllSnippets() } returns snippets

        // Act - Make a GET request to /snippets
        mockMvc.perform(get("/snippets"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("First Snippet"))

        // Assert - Verify the service method was called once
        verify(exactly = 1) { snippetService.getAllSnippets() }
    }

    @Test
    fun `should return snippet by id`() {
        // Arrange - Mock the service to return a specific snippet
        val snippet =
            Snippet(
                1,
                "First Snippet",
                "Description",
                "Code",
                1L,
                1L,
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
    fun `should create new snippet`() {
        // Arrange - Mock the service to create a snippet
        val newSnippet =
            Snippet(
                2,
                "New Snippet",
                "Description",
                "Code",
                1L,
                1L,
            )
        every { snippetService.createSnippet(any(), any(), any(), any(), any()) } returns newSnippet

        // Act - Make a POST request to /snippets
        val jsonSnippet =
            """{ "name": "New Snippet", "description": "Description", "code": "Code",
            | "languageId": 1, "ownerId": 1, "usersWithReadPermission": [],
            |  "usersWithWritePermission": [] }
            """.trimMargin()
        mockMvc.perform(
            post("/snippets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSnippet),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("New Snippet"))

        // Assert - Verify the service method was called
        verify(exactly = 1) { snippetService.createSnippet(any(), any(), any(), any(), any()) }
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
    fun `should update snippet`() {
        // Arrange - Mock the service to return an existing snippet and then an updated snippet
        val existingSnippet =
            Snippet(
                1,
                "Old Snippet",
                "Old Description",
                "Old Code",
                1L,
                1L,
            )
        val updatedSnippet =
            Snippet(
                1,
                "Updated Snippet",
                "Updated Description",
                "Updated Code",
                1L,
                1L,
            )
        every { snippetService.getSnippetById(1) } returns existingSnippet
        every {
            snippetService.updateSnippet(
                1,
                "Updated Snippet",
                "Updated Description",
                "Updated Code",
                1L,
                1L,
            )
        } returns updatedSnippet

        // Act - Make a PUT request to /snippets/1
        val jsonSnippet =
            """{"id": "1", "name": "Updated Snippet", "description": "Updated Description",
            | "code": "Updated Code", "languageId": 1, "ownerId": 1, 
            | "usersWithReadPermission": [], "usersWithWritePermission": [] }
            """.trimMargin()
        mockMvc.perform(
            put("/snippets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSnippet),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated Snippet"))
            .andExpect(jsonPath("$.description").value("Updated Description"))
            .andExpect(jsonPath("$.code").value("Updated Code"))

        // Assert - Verify the service method was called
        verify(exactly = 1) {
            snippetService.updateSnippet(
                1,
                "Updated Snippet",
                "Updated Description",
                "Updated Code",
                1L,
                1L,
            )
        }
    }
}
