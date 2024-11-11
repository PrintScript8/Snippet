package austral.ingsis.snippet.message

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StreamOperations

class RedisMessageEmitterTest {
    private val redisTemplate: RedisTemplate<String, String> =
        mock(RedisTemplate::class.java) as RedisTemplate<String, String>
    private val redisMessageEmitter = RedisMessageEmitter("testStreamKey", redisTemplate)
    private val streamOperations: StreamOperations<String, String, String> =
        mock(StreamOperations::class.java) as StreamOperations<String, String, String>

    @Test
    fun `test publishEvent`() {
        val ownerId = 1L
        val language = "Kotlin"
        val rules = "rules"
        val action = "create"
        val snippedId = 123L
        `when`(redisTemplate.opsForStream<String, String>()).thenReturn(streamOperations)

        redisMessageEmitter.publishEvent(ownerId, language, rules, action, snippedId)

        verify(redisTemplate, times(1)).opsForStream<String, String>()
    }
}
