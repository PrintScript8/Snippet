package austral.ingsis.snippet.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CompilanceEnumTest {
    @Test
    fun `test compliance enum values`() {
        val expectedValues = listOf("COMPLIANT", "NON_COMPLIANT", "PENDING", "FAILED")
        val actualValues = ComplianceEnum.values().map { it.name }
        assertEquals(expectedValues, actualValues)
    }

    @Test
    fun `test compliance enum toString`() {
        assertEquals("compliant", ComplianceEnum.COMPLIANT.toString())
        assertEquals("non_compliant", ComplianceEnum.NON_COMPLIANT.toString())
        assertEquals("pending", ComplianceEnum.PENDING.toString())
        assertEquals("failed", ComplianceEnum.FAILED.toString())
    }
}
