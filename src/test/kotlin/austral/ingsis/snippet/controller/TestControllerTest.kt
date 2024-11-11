package austral.ingsis.snippet.controller

import austral.ingsis.snippet.model.SnippetTest
import austral.ingsis.snippet.service.TestService
import austral.ingsis.snippet.service.ValidationService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.content
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate

@RestClientTest(TestController::class)
class TestControllerTest {
    @Autowired
    private lateinit var mockServer: MockRestServiceServer

    @MockBean
    private lateinit var testService: TestService

    @MockBean
    private lateinit var validationService: ValidationService

    @Autowired
    private lateinit var testController: TestController

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @BeforeEach
    fun setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `test createTest`() {
        val testCaseRequest = ExecuteTest("1", "Test Name", listOf("input1", "input2"), listOf("output1", "output2"))
        val request =
            MockHttpServletRequest().apply {
                addHeader("id", "123")
            }

        `when`(testService.createTest(anyLong(), anyLong(), anyString(), anyList(), anyList())).thenReturn(1L)

        mockServer.expect(requestTo("/test"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(header("id", "123"))
            .andExpect(jsonPath("$.id").value(testCaseRequest.id))
            .andRespond(withSuccess("1", MediaType.APPLICATION_JSON))

        val response = testController.createTest(testCaseRequest, request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1L, response.body)
    }

    @Test
    fun `test editTest`() {
        val snippetTest = SnippetTest(1L, 1L, 1L, "Test Name", listOf("input1", "input2"), listOf("output1", "output2"))
        val request =
            MockHttpServletRequest().apply {
                addHeader("id", "123")
            }

        mockServer.expect(requestTo("/test/1"))
            .andExpect(method(HttpMethod.PUT))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(header("id", "123"))
            .andExpect(jsonPath("$.id").value(snippetTest.testId))
            .andRespond(withSuccess("Edited", MediaType.TEXT_PLAIN))

        val response = testController.editTest("1", snippetTest, request)

        assertEquals("Edited", response)
    }

    @Test
    fun `test deleteTest`() {
        val request =
            MockHttpServletRequest().apply {
                addHeader("id", "123")
            }

        mockServer.expect(requestTo("/test/1"))
            .andExpect(method(HttpMethod.DELETE))
            .andExpect(header("id", "123"))
            .andRespond(withSuccess("Deleted", MediaType.TEXT_PLAIN))

        val response = testController.deleteTest(1L, request)

        assertEquals("Deleted", response)
    }

    @Test
    fun `test getTestById`() {
        val snippetTests =
            listOf(
                SnippetTest(1L, 1L, 1L, "Test Name 1", listOf("input1"), listOf("output1")),
                SnippetTest(2L, 2L, 2L, "Test Name 2", listOf("input2"), listOf("output2")),
            )
        val request =
            MockHttpServletRequest().apply {
                addHeader("id", "123")
            }

        `when`(testService.getAllTests(1L)).thenReturn(snippetTests)

        mockServer.expect(requestTo("/test/1"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("id", "123"))
            .andRespond(withSuccess(objectMapper.writeValueAsString(snippetTests), MediaType.APPLICATION_JSON))

        val response = testController.getTestById(1L, request)

        assertEquals(snippetTests, response)
    }

    @Test
    fun `test getUserTest`() {
        val snippetTests =
            listOf(
                SnippetTest(1L, 1L, 1L, "Test Name 1", listOf("input1"), listOf("output1")),
                SnippetTest(2L, 2L, 2L, "Test Name 2", listOf("input2"), listOf("output2")),
            )
        val request =
            MockHttpServletRequest().apply {
                addHeader("id", "123")
            }

        `when`(testService.getUserTest(123L)).thenReturn(snippetTests)

        mockServer.expect(requestTo("/test"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("id", "123"))
            .andRespond(withSuccess(objectMapper.writeValueAsString(snippetTests), MediaType.APPLICATION_JSON))

        val response = testController.getUserTest(request)

        assertEquals(snippetTests, response)
    }

    @Test
    fun `test executeTest`() {
        val testCaseRequest = ExecuteTest("1", "Test Name", listOf("input1", "input2"), listOf("output1", "output2"))
        val request =
            MockHttpServletRequest().apply {
                addHeader("id", "123")
            }

        `when`(validationService.canModify(anyLong(), anyLong())).thenReturn(true)
        `when`(testService.executeTest(anyLong(), anyString(), anyList(), anyList())).thenReturn(true)

        mockServer.expect(requestTo("/test/execute"))
            .andExpect(method(HttpMethod.PUT))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(header("id", "123"))
            .andExpect(jsonPath("$.id").value(testCaseRequest.id))
            .andRespond(withSuccess("success", MediaType.TEXT_PLAIN))

        val response = testController.executeTest(testCaseRequest, request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("success", response.body)
    }

    @Test
    fun `test getTestById - retrieve`() {
        val snippetTests =
            listOf(
                SnippetTest(1L, 1L, 1L, "Test Name 1", listOf("input1"), listOf("output1")),
                SnippetTest(2L, 2L, 2L, "Test Name 2", listOf("input2"), listOf("output2")),
            )
        val simpleTests = snippetTests.map { SimpleTest(it.input, it.output) }

        `when`(testService.getAllTests(1L)).thenReturn(snippetTests)

        mockServer.expect(requestTo("/test/retrieve/1"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(objectMapper.writeValueAsString(simpleTests), MediaType.APPLICATION_JSON))

        val response = testController.getTestById(1L)

        assertEquals(simpleTests, response)
    }
}
