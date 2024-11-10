package austral.ingsis.snippet.service

import austral.ingsis.snippet.model.SnippetTest
import austral.ingsis.snippet.repository.SnippetTestRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class TestService(
    @Autowired private val snippetTestRepository: SnippetTestRepository,
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
        val code = bucketClient.get()
            .uri("/v1/asset/{container}/{key}", "snippet", snippetId)
            .retrieve()
            .body(kotlin.String::class.java)
        if (code == null) { throw Exception("Snippet not found") }
        val response: List<String> = parserClient.put()
            .uri("/parser/test/execute")
            .body(TestRequest(code, "printscript", input)) // se podria ver de buscar el language en el snippet
            .retrieve()
            .toEntity(List::class.java) as List<String>

        return response == output
    }

    fun deleteAllTests(id: Long) {
        snippetTestRepository.deleteAllByOwnerId(id)
    }
}
data class TestRequest(val code: String, val language: String, val input: List<String>)