package austral.ingsis.snippet

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import kotlin.test.assertNotNull

@SpringBootTest
class SnippetApplicationTests {
	@Autowired
	private lateinit var context: ApplicationContext

	@Test
	fun contextLoads() {
		assertNotNull(context, "The application context should have loaded.")
	}

}
