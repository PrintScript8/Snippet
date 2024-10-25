package austral.ingsis.snippet.controller

import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippets/update")
class SnippetAsyncController {

    @PutMapping("/linting")
    fun updatedLinting(@RequestParam snippets: List<Long>) {

    }
}