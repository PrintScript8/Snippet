package austral.ingsis.snippet

import io.github.cdimascio.dotenv.Dotenv
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
        private val dotenv = Dotenv.load()

        private val dbPassword = dotenv["DB_PASSWORD"]
        private val dbName = dotenv["DB_NAME"]
        private val dbUser = dotenv["DB_USER"]

        private val postgresContainer =
            PostgreSQLContainer<Nothing>("postgres:14").apply {
                withDatabaseName(dbName)
                withUsername(dbUser)
                withPassword(dbPassword)
            }

        @BeforeAll
        @JvmStatic
        fun startContainer() {
            println("DB_NAME: $dbName, DB_USER: $dbUser")
            postgresContainer.start()
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

    @Test
    fun `run main method`() {
        main(arrayOf())
        assertNotNull(context, "The application context should have loaded after running main.")
    }
}
