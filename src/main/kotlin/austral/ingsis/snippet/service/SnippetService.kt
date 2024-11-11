package austral.ingsis.snippet.service

import austral.ingsis.snippet.exception.InvalidSnippetException
import austral.ingsis.snippet.model.CommunicationSnippet
import austral.ingsis.snippet.model.ComplianceEnum
import austral.ingsis.snippet.model.Snippet
import austral.ingsis.snippet.repository.SnippetRepository
import jakarta.transaction.Transactional
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class SnippetService(
    @Autowired final val restClientBuilder: RestClient.Builder,
    @Autowired final val snippetRepository: SnippetRepository,
    @Autowired final val testService: TestService,
) {
    var bucketClient: RestClient = restClientBuilder.baseUrl("http://asset-service:8080").build()
    var parserClient: RestClient = restClientBuilder.baseUrl("http://parser-service:8080").build()
    val logger: Logger = LogManager.getLogger(SnippetService::class.java)

    fun getSnippetById(id: Long): CommunicationSnippet? {
        val code =
            bucketClient.get()
                .uri("/v1/asset/{container}/{key}", "snippet", id)
                .retrieve()
                .body(String::class.java)
        val snippet: Snippet = snippetRepository.getReferenceById(id)
        if (code == null) {
            logger.info("Snippet with id $id not found")
            return null
        }
        val communicationSnippet =
            CommunicationSnippet(
                snippet.id,
                snippet.name,
                snippet.language,
                snippet.ownerId,
                code,
                snippet.extension,
                snippet.status,
            )
        logger.info("Snippet with id $id has been retrieved")
        return communicationSnippet
    }

    fun createSnippet(
        name: String,
        code: String,
        language: String,
        ownerId: String,
        extension: String,
        token: String,
    ): Long {
        val snippet: Snippet =
            snippetRepository.save(
                Snippet(0, name, language, ownerId, extension, ComplianceEnum.PENDING),
            )
        val result =
            parserClient.put()
                .uri("/parser/validate")
                .body(ExecuteSnippet(code, language))
                .headers { headers -> headers.set("Authorization", token) }
                .retrieve()
                .toBodilessEntity()
        logger.info("Snippet has status code ${result.statusCode}")
        if (result.statusCode.is2xxSuccessful) {
            bucketClient.put()
                .uri("/v1/asset/{container}/{key}", "snippet", snippet.id)
                .body(code)
                .retrieve()
                .body(String::class.java)
            logger.info("Snippet has been created with id ${snippet.id}")
            snippet.status = ComplianceEnum.COMPLIANT
            snippetRepository.save(snippet)
            return snippet.id
        } else {
            throw InvalidSnippetException("Invalid snippet")
        }
    }

    fun updateSnippet(
        id: Long,
        code: String,
        language: String,
        token: String,
    ) {
        val snippet: Snippet = snippetRepository.getReferenceById(id)
        snippet.status = ComplianceEnum.PENDING
        snippetRepository.save(snippet)
        val result =
            parserClient.put()
                .uri("/parser/validate")
                .body(ExecuteSnippet(code, language))
                .headers { headers -> headers.set("Authorization", token) }
                .retrieve()
                .toBodilessEntity()
        logger.info("Snippet with id $id has status code ${result.statusCode}")
        if (result.statusCode.is2xxSuccessful) {
            bucketClient.put()
                .uri("/v1/asset/{container}/{key}", "snippet", id)
                .body(code)
                .retrieve()
                .body(String::class.java)
            logger.info("Snippet with id $id has been updated")
            snippetRepository.save(snippet)
        } else {
            throw InvalidSnippetException("Invalid snippet")
        }
    }

    fun deleteSnippet(id: Long) {
        bucketClient.delete()
            .uri("/v1/asset/{container}/{key}", "snippet", id)
            .retrieve()
            .body(Void::class.java)
        snippetRepository.deleteById(id)
        testService.deleteAllTests(id)
        logger.info("Snippet with id $id has been deleted")
    }

    @Suppress("ReturnCount")
    fun paginatedSnippets(
        page: Int,
        pageSize: Int,
        snippetName: String,
    ): List<CommunicationSnippet> {
        val snippets: List<Snippet> = snippetRepository.findAll()
        val output = mutableListOf<CommunicationSnippet>()
        for (snippet in snippets) {
            val code =
                bucketClient.get()
                    .uri("/v1/asset/{container}/{key}", "snippet", snippet.id)
                    .retrieve()
                    .body(String::class.java)
            output.add(
                CommunicationSnippet(
                    snippet.id,
                    snippet.name,
                    snippet.language,
                    snippet.ownerId,
                    code ?: "",
                    snippet.extension,
                    snippet.status,
                ),
            )
        }
        if (snippetName.isNotEmpty()) {
            return output.filter { it.name.contains(snippetName) }
        }
        val fromIndex = page * pageSize
        val toIndex = minOf(fromIndex + pageSize, output.size)
        if (fromIndex > output.size) {
            return emptyList()
        }
        return output.subList(fromIndex, toIndex)
    }

    @Transactional
    fun setSnippetStatus(
        id: Long,
        status: String,
    ) {
        val translatedStatus =
            when (status) {
                "compliant" -> ComplianceEnum.COMPLIANT
                "pending" -> ComplianceEnum.PENDING
                "non-compliant" -> ComplianceEnum.NON_COMPLIANT
                "failed" -> ComplianceEnum.FAILED
                else -> throw IllegalArgumentException("Invalid status")
            }

        val snippet: Snippet = snippetRepository.getReferenceById(id)
        snippet.status = translatedStatus

        // Immediately flush changes to the database to ensure consistency
        snippetRepository.saveAndFlush(snippet)

        logger.info("Snippet with id $id has been updated to $translatedStatus")
    }
}

data class ExecuteSnippet(val code: String, val language: String)
