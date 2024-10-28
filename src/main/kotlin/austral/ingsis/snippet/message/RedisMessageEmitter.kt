package austral.ingsis.snippet.message

import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisMessageEmitter
    @Autowired
    constructor(
        @Value("\${stream.key}") streamKey: String,
        redis: RedisTemplate<String, String>,
    ) : RedisStreamProducer(streamKey, redis) {
        fun publishEvent(
            ownerId: Long,
            language: String,
            rules: String,
            action: String,
        ) {
            val publishingMessage = ExecuteRequest(ownerId, language, rules, action)
            emit(publishingMessage)
        }
    }
