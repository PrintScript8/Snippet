package austral.ingsis.snippet.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConfigTypeTest {
    @Test
    fun `test ConfigType values`() {
        val values = ConfigType.values()
        assertEquals(2, values.size)
        assertEquals(ConfigType.LINTING, values[0])
        assertEquals(ConfigType.FORMATTING, values[1])
    }
}
