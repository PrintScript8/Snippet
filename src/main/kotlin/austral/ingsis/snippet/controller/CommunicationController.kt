package austral.ingsis.snippet.controller

import austral.ingsis.snippet.server.CorrelationIdInterceptor
import com.newrelic.api.agent.Trace
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

@RestController
class CommunicationController(restClientBuilder: RestClient.Builder) {
    final val myInterceptor: ClientHttpRequestInterceptor = CorrelationIdInterceptor()
    val restClient =
        restClientBuilder
            .baseUrl("http://permissions-service:8080")
            .requestInterceptor(myInterceptor).build()

    @GetMapping("/testRequestMessage")
    fun callEndpoint(): ResponseEntity<String> {
        val response =
            restClient.get()
                .uri("/testRespondMessage")
                .retrieve()
                .toEntity(String::class.java)
        return ResponseEntity(response.body, response.statusCode)
    }

    @GetMapping("/testRespondMessage")
    fun respondMessage(): ResponseEntity<String> {
        return ResponseEntity.ok("Greetings from Snippet!")
    }

    @GetMapping("/hello-world")
    @Trace(dispatcher = true)
    fun helloWorld(): ResponseEntity<String> {
        val response =
            restClient.get()
                .uri("/hello-world")
                .retrieve()
                .toEntity(String::class.java)

        return ResponseEntity(response.body, response.statusCode)
    }

    @GetMapping("/alert-condition")
    @Trace(dispatcher = true)
    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
    fun alertCondition(): ResponseEntity<String> {
        throw RuntimeException("Error intencional para probar New Relic")
    }
}
