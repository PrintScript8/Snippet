package austral.ingsis.snippet.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RuleTest {
    @Test
    fun `test Rule toString with null value`() {
        val rule = Rule(1L, "testRule", true, null, ConfigType.FORMATTING)
        val expected = "\"testRule\": true"
        assertEquals(expected, rule.toString())
    }

    @Test
    fun `test Rule toString with non-null value`() {
        val rule = Rule(1L, "testRule", true, "someValue", ConfigType.FORMATTING)
        val expected = "\"testRule\": \"someValue\""
        assertEquals(expected, rule.toString())
    }
}
