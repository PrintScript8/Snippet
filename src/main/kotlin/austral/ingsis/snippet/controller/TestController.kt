package austral.ingsis.snippet.controller

import austral.ingsis.snippet.model.SnippetTest
import austral.ingsis.snippet.service.TestService
import austral.ingsis.snippet.service.ValidationService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController(
    @Autowired private val testService: TestService,
    @Autowired private val validationService: ValidationService,
) {
    @PostMapping
    fun createTest(
        @RequestBody testCaseRequest: ExecuteTest,
        request: HttpServletRequest,
    ): ResponseEntity<Long> {
        val ownerId = request.getHeader("id").toLong()
//        if (!validationService.canModify(ownerId, testCaseRequest.snippetId.toLong())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
//        }
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
        request: HttpServletRequest,
    ): String {
        val ownerId = request.getHeader("id").toLong()
        println(id)
        testService.editTest(test)
        return "Edited"
    }

    @DeleteMapping("/{id}")
    fun deleteTest(
        @PathVariable id: Long,
        request: HttpServletRequest,
    ): String {
        val ownerId = request.getHeader("id").toLong()
        testService.deleteTest(id)
        return "Deleted"
    }

    @GetMapping("/{id}")
    fun getTestById(
        @PathVariable id: Long,
        request: HttpServletRequest,
    ): List<SnippetTest> {
        val ownerId = request.getHeader("id").toLong()
        return testService.getAllTests(id)
    }

    @GetMapping
    fun getUserTest(request: HttpServletRequest): List<SnippetTest> {
        val userId = request.getHeader("id").toLong()
        return testService.getUserTest(userId)
    }

    @PutMapping("/execute")
    fun executeTest(
        @RequestBody testCaseRequest: ExecuteTest,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        val ownerId = request.getHeader("id").toLong()
        if (!validationService.canModify(ownerId, testCaseRequest.id.toLong())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val testResult: Boolean =
            testService.executeTest(
                testCaseRequest.id.toLong(),
                testCaseRequest.name,
                testCaseRequest.input,
                testCaseRequest.output,
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
