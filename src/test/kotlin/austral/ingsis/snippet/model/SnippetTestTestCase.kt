package austral.ingsis.snippet.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SnippetTestTestCase {
    @Test
    fun `test SnippetTest default constructor`() {
        val snippetTest = SnippetTest()
        assertEquals(0L, snippetTest.testId)
        assertEquals(0L, snippetTest.snippetId)
        assertEquals(0L, snippetTest.ownerId)
        assertEquals("", snippetTest.name)
        assertTrue(snippetTest.input.isEmpty())
        assertTrue(snippetTest.output.isEmpty())
    }

    @Test
    fun `test SnippetTest parameterized constructor`() {
        val inputList = listOf("input1", "input2")
        val outputList = listOf("output1", "output2")
        val snippetTest = SnippetTest(1L, 2L, 3L, "TestName", inputList, outputList)
        assertEquals(1L, snippetTest.testId)
        assertEquals(2L, snippetTest.snippetId)
        assertEquals(3L, snippetTest.ownerId)
        assertEquals("TestName", snippetTest.name)
        assertEquals(inputList, snippetTest.input)
        assertEquals(outputList, snippetTest.output)
    }
}
