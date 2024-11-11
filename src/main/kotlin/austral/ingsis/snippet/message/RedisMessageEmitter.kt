package austral.ingsis.snippet.message

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.LogManager
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

interface MessageEmitter {
    fun publishEvent(
        ownerId: Long,
        language: String,
        rules: String,
        action: String,
        snippedId: Long? = null,
    )
}

@Component
class RedisMessageEmitter
    @Autowired
    constructor(
        @Value("\${stream.key}") streamKey: String,
        redis: RedisTemplate<String, String>,
    ) : MessageEmitter, RedisStreamProducer(streamKey, redis) {
        private val logger = LogManager.getLogger(RedisMessageEmitter::class.java)

        override fun publishEvent(
            ownerId: Long,
            language: String,
            rules: String,
            action: String,
            snippedId: Long?,
        ) {
            val mapper = ObjectMapper()
            val publishingMessage = ExecuteRequest(ownerId, language, rules, action, snippedId)
            val stringPublishingImage = mapper.writeValueAsString(publishingMessage)
            logger.info("Publishing message: $stringPublishingImage")
            emit(stringPublishingImage)
        }
    }
