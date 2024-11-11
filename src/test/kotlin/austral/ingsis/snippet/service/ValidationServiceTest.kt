package austral.ingsis.snippet.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(ValidationService::class)
class ValidationServiceTest {
    @Autowired
    private lateinit var validationService: ValidationService

    @Autowired
    private lateinit var mockServer: MockRestServiceServer

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val baseUrl = "http://permission-service:8080"
    private val userId = 1L
    private val snippetId = 2L

    @BeforeEach
    fun setUp() {
        mockServer.reset()
    }

    @Test
    fun `exists should return true when user exists`() {
        mockServer.expect(requestTo("$baseUrl/validate/create/$userId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess(objectMapper.writeValueAsString(true), MediaType.APPLICATION_JSON))

        val result = validationService.exists(userId)
        assertTrue(result)
        mockServer.verify()
    }

    @Test
    fun `exists should return false when user does not exist`() {
        mockServer.expect(requestTo("$baseUrl/validate/create/$userId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess(objectMapper.writeValueAsString(false), MediaType.APPLICATION_JSON))

        val result = validationService.exists(userId)
        assertFalse(result)
        mockServer.verify()
    }

    @Test
    fun `canModify should return true when user has permission`() {
        // Mock exists check
        mockServer.expect(requestTo("$baseUrl/validate/create/$userId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess(objectMapper.writeValueAsString(true), MediaType.APPLICATION_JSON))

        // Mock modify permission check
        mockServer.expect(requestTo("$baseUrl/validate/edit/$userId/$snippetId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess(objectMapper.writeValueAsString(true), MediaType.APPLICATION_JSON))

        val result = validationService.canModify(userId, snippetId)
        assertTrue(result)
        mockServer.verify()
    }

    @Test
    fun `canRead should return true when user has permission`() {
        // Mock exists check
        mockServer.expect(requestTo("$baseUrl/validate/create/$userId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess(objectMapper.writeValueAsString(true), MediaType.APPLICATION_JSON))

        // Mock read permission check
        mockServer.expect(requestTo("$baseUrl/validate/read/$userId/$snippetId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess(objectMapper.writeValueAsString(true), MediaType.APPLICATION_JSON))

        val result = validationService.canRead(userId, snippetId)
        assertTrue(result)
        mockServer.verify()
    }

    @Test
    fun `canDelete should return true when user has permission`() {
        // Mock exists check
        mockServer.expect(requestTo("$baseUrl/validate/create/$userId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess(objectMapper.writeValueAsString(true), MediaType.APPLICATION_JSON))

        // Mock delete permission check
        mockServer.expect(requestTo("$baseUrl/validate/delete/$userId/$snippetId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess(objectMapper.writeValueAsString(true), MediaType.APPLICATION_JSON))

        val result = validationService.canDelete(userId, snippetId)
        assertTrue(result)
        mockServer.verify()
    }

    @Test
    fun `all permission checks should return false when user does not exist`() {
        mockServer.expect(requestTo("$baseUrl/validate/create/$userId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess(objectMapper.writeValueAsString(false), MediaType.APPLICATION_JSON))

        assertFalse(validationService.canModify(userId, snippetId))

        mockServer.reset()
        mockServer.expect(requestTo("$baseUrl/validate/create/$userId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess(objectMapper.writeValueAsString(false), MediaType.APPLICATION_JSON))

        assertFalse(validationService.canRead(userId, snippetId))

        mockServer.reset()
        mockServer.expect(requestTo("$baseUrl/validate/create/$userId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess(objectMapper.writeValueAsString(false), MediaType.APPLICATION_JSON))

        assertFalse(validationService.canDelete(userId, snippetId))

        mockServer.verify()
    }
}
