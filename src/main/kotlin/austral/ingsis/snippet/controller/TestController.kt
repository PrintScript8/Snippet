package austral.ingsis.snippet.controller

import austral.ingsis.snippet.model.SnippetTest
import austral.ingsis.snippet.service.AuthService
import austral.ingsis.snippet.service.TestService
import austral.ingsis.snippet.service.ValidationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.AccessDeniedException

@RestController
@RequestMapping("/test")
class TestController(
    @Autowired private val testService: TestService,
    @Autowired private val validationService: ValidationService,
    @Autowired private val authService: AuthService,
) {
    private fun getIdByToken(token: String): String {
        val id: String? = authService.validateToken(token)
        if (id != null) {
            return id
        }
        // error, not authenticated
        throw AccessDeniedException("Could not validate user by it's token")
    }

    @PostMapping
    fun createTest(
        @RequestBody testCaseRequest: ExecuteTest,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<Long> {
        val ownerId = getIdByToken(token)
        if (!validationService.canModify(testCaseRequest.id.toLong(), token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val testId: Long =
            testService.createTest(
                testCaseRequest.id.toLong(),
                ownerId,
                testCaseRequest.name,
                testCaseRequest.input,
                testCaseRequest.output,
            )
        return ResponseEntity.ok(testId)
    }

    @PutMapping("/{id}")
    fun editTest(
        @PathVariable id: String,
        @RequestBody test: SnippetTest,
        @RequestHeader("Authorization") token: String,
    ): String {
        val ownerId = getIdByToken(token)
        println(id)
        testService.editTest(test)
        return "Edited"
    }

    @DeleteMapping("/{id}")
    fun deleteTest(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String,
    ): String {
        val ownerId = getIdByToken(token)
        testService.deleteTest(id)
        return "Deleted"
    }

    @GetMapping("/{id}")
    fun getTestById(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String,
    ): List<SnippetTest> {
        val ownerId = getIdByToken(token)
        return testService.getAllTests(id)
    }

    @GetMapping
    fun getUserTest(
        @RequestHeader("Authorization") token: String,
    ): List<SnippetTest> {
        val userId = getIdByToken(token)
        return testService.getUserTest(userId)
    }

    @PutMapping("/execute")
    fun executeTest(
        @RequestBody testCaseRequest: ExecuteTest,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<String> {
        val ownerId = getIdByToken(token)
        if (!validationService.canModify(testCaseRequest.id.toLong(), token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val testResult: Boolean =
            testService.executeTest(
                testCaseRequest.id.toLong(),
                testCaseRequest.name,
                testCaseRequest.input,
                testCaseRequest.output,
                token,
            )
        val enum = if (testResult) "success" else "fail"
        return ResponseEntity.ok(enum)
    }

    @GetMapping("/retrieve/{id}")
    fun getTestById(
        @PathVariable id: Long,
    ): List<SimpleTest> {
        val tests: List<SnippetTest> = testService.getAllTests(id)
        val simpleTest: List<SimpleTest> =
            tests.map {
                SimpleTest(it.input, it.output)
            }
        return simpleTest
    }
}

data class SimpleTest(val input: List<String>, val output: List<String>)

data class ExecuteTest(
    val id: String,
    val name: String,
    val input: List<String>,
    val output: List<String>,
)
