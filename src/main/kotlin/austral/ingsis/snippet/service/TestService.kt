package austral.ingsis.snippet.service

import austral.ingsis.snippet.model.ComplianceEnum
import austral.ingsis.snippet.model.SnippetTest
import austral.ingsis.snippet.repository.SnippetRepository
import austral.ingsis.snippet.repository.SnippetTestRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class TestService(
    @Autowired private val snippetTestRepository: SnippetTestRepository,
    @Autowired private val snippetRepository: SnippetRepository,
    @Autowired final val restClientBuilder: RestClient.Builder,
) {
    var bucketClient: RestClient = restClientBuilder.baseUrl("http://asset-service:8080").build()
    var parserClient: RestClient = restClientBuilder.baseUrl("http://parser-service:8080").build()

    fun createTest(snippetId: Long, ownerId: Long, name: String, inputs: List<String>, outputs: List<String>): Long {
        val test = snippetTestRepository.save(SnippetTest(0L, snippetId, ownerId, name, inputs, outputs))
        return test.testId
    }

    fun editTest(snippetTest: SnippetTest) {
        snippetTestRepository.save(snippetTest)
    }

    fun deleteTest(id: Long) {
        snippetTestRepository.deleteById(id)
    }

    fun getAllTests(snippetId: Long): List<SnippetTest> {
        return snippetTestRepository.findAllBySnippetId(snippetId)
    }

    fun getTestById(id: Long): SnippetTest {
        return snippetTestRepository.findById(id).get()
    }

    fun getUserTest(id: Long): List<SnippetTest> {
        return snippetTestRepository.findAllByOwnerId(id)
    }

    fun executeTest(testId: Long, name: String, input: List<String>, output: List<String>): Boolean {
        val snippetId = getTestById(testId).snippetId
        val snippet = snippetRepository.getReferenceById(snippetId)
        snippet.status = ComplianceEnum.PENDING
        snippetRepository.save(snippet)
        val code = bucketClient.get()
            .uri("/v1/asset/{container}/{key}", "snippet", snippetId)
            .retrieve()
            .body(kotlin.String::class.java)
        if (code == null) { throw Exception("Snippet not found") }
        val response: List<String> = parserClient.put()
            .uri("/parser/test/execute")
            .body(TestRequest(code, "printscript", input))
            .retrieve()
            .toEntity(object : ParameterizedTypeReference<List<String>>() {})
            .body ?: throw Exception("Failed to parse response")

        if (response == output) {
            snippet.status = ComplianceEnum.COMPLIANT
        } else {
            snippet.status = ComplianceEnum.FAILED
        }
        snippetRepository.save(snippet)
        return response == output
    }

    fun deleteAllTests(id: Long) {
        snippetTestRepository.deleteAllByOwnerId(id)
    }
}
data class TestRequest(val code: String, val language: String, val input: List<String>)