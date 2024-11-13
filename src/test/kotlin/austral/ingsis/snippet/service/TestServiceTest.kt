package austral.ingsis.snippet.service

import austral.ingsis.snippet.model.ComplianceEnum
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.model.SnippetTest
import austral.ingsis.snippet.repository.SnippetRepository
import austral.ingsis.snippet.repository.SnippetTestRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import java.util.Optional

@RestClientTest(TestService::class)
@Import(TestService::class)
class TestServiceTest {
    @Autowired
    private lateinit var testService: TestService

    @Autowired
    private lateinit var server: MockRestServiceServer

    @MockBean
    private lateinit var snippetTestRepository: SnippetTestRepository

    @MockBean
    private lateinit var snippetRepository: SnippetRepository

    @Test
    fun `executeTest should return true when response matches output`() {
        val snippetTest = SnippetTest(1L, 1L, "1L", "Test", listOf("input"), listOf("output"))
        val snippet = Snippet(1L, "Test Snippet", "Kotlin", "1L", "kt", ComplianceEnum.PENDING, "Test")

        `when`(snippetTestRepository.findById(1L)).thenReturn(Optional.of(snippetTest))
        `when`(snippetRepository.getReferenceById(1L)).thenReturn(snippet)

        server.expect(requestTo("http://asset-service:8080/v1/asset/snippet/1"))
            .andRespond(withSuccess("code", MediaType.TEXT_PLAIN))

        server.expect(requestTo("http://parser-service:8080/parser/test/execute"))
            .andRespond(withSuccess("[\"output\"]", MediaType.APPLICATION_JSON))

        val result = testService.executeTest(1L, "Test", listOf("input"), listOf("output"), "test")

        assertTrue(result)
        assertEquals(ComplianceEnum.COMPLIANT, snippet.status)
    }

    @Test
    fun `executeTest should return false when response does not match output`() {
        val snippetTest = SnippetTest(1L, 1L, "1L", "Test", listOf("input"), listOf("output"))
        val snippet = Snippet(1L, "Test Snippet", "Kotlin", "1L", "kt", ComplianceEnum.PENDING, "Test")

        `when`(snippetTestRepository.findById(1L)).thenReturn(Optional.of(snippetTest))
        `when`(snippetRepository.getReferenceById(1L)).thenReturn(snippet)

        server.expect(requestTo("http://asset-service:8080/v1/asset/snippet/1"))
            .andRespond(withSuccess("code", MediaType.TEXT_PLAIN))

        server.expect(requestTo("http://parser-service:8080/parser/test/execute"))
            .andRespond(withSuccess("[\"wrong output\"]", MediaType.APPLICATION_JSON))

        val result = testService.executeTest(1L, "Test", listOf("input"), listOf("output"), "test")

        assertFalse(result)
        assertEquals(ComplianceEnum.FAILED, snippet.status)
    }

    @Test
    fun `createTest should return the created test ID`() {
        val snippetTest = SnippetTest(0L, 1L, "1L", "Test", listOf("input"), listOf("output"))
        `when`(snippetTestRepository.save(any(SnippetTest::class.java))).thenReturn(snippetTest.copy(testId = 1L))

        val result = testService.createTest(1L, "1L", "Test", listOf("input"), listOf("output"))

        assertEquals(1L, result)
    }

    @Test
    fun `editTest should update the test`() {
        val snippetTest = SnippetTest(1L, 1L, "1L", "Test", listOf("input"), listOf("output"))
        testService.editTest(snippetTest)
        verify(snippetTestRepository).save(snippetTest)
    }

    @Test
    fun `deleteTest should remove the test`() {
        testService.deleteTest(1L)
        verify(snippetTestRepository).deleteById(1L)
    }

    @Test
    fun `getAllTests should return all tests for a snippet`() {
        val snippetTests = listOf(SnippetTest(1L, 1L, "1L", "Test", listOf("input"), listOf("output")))
        `when`(snippetTestRepository.findAllBySnippetId(1L)).thenReturn(snippetTests)

        val result = testService.getAllTests(1L)

        assertEquals(snippetTests, result)
    }

    @Test
    fun `getTestById should return the test`() {
        val snippetTest = SnippetTest(1L, 1L, "1L", "Test", listOf("input"), listOf("output"))
        `when`(snippetTestRepository.findById(1L)).thenReturn(Optional.of(snippetTest))

        val result = testService.getTestById(1L)

        assertEquals(snippetTest, result)
    }

    @Test
    fun `getUserTest should return all tests for a user`() {
        val snippetTests = listOf(SnippetTest(1L, 1L, "1L", "Test", listOf("input"), listOf("output")))
        `when`(snippetTestRepository.findAllByOwnerId("1L")).thenReturn(snippetTests)

        val result = testService.getUserTest("1L")

        assertEquals(snippetTests, result)
    }

    @Test
    fun `deleteAllTests should remove all tests for a user`() {
        testService.deleteAllTests(1L)
        verify(snippetTestRepository).deleteAllBySnippetId(1L)
    }
}
