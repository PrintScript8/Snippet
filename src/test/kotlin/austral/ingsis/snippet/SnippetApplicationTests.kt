package austral.ingsis.snippet

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.assertNotNull

@SpringBootTest
class SnippetApplicationTests {
    @Autowired
    private lateinit var context: ApplicationContext

    companion object {
        private val postgresContainer =
            PostgreSQLContainer<Nothing>("postgres:14").apply {
                withDatabaseName("test_db")
                withUsername("test_user")
                withPassword("test_password")
            }

        @BeforeAll
        @JvmStatic
        fun startContainer() {
            postgresContainer.start()
            // Override the Spring datasource properties to point to the test container
            System.setProperty("spring.datasource.url", postgresContainer.jdbcUrl)
            System.setProperty("spring.datasource.username", postgresContainer.username)
            System.setProperty("spring.datasource.password", postgresContainer.password)
        }

        @AfterAll
        @JvmStatic
        fun stopContainer() {
            postgresContainer.stop()
        }
    }

    @Test
    fun contextLoads() {
        assertNotNull(context, "The application context should have loaded.")
    }
}
