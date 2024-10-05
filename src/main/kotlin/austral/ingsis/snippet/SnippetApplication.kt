package austral.ingsis.snippet

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SnippetApplication

fun main(args: Array<String>) {
    val dotenv = Dotenv.load()

    val dbHost = dotenv["DB_HOST"]
    val dbPort = dotenv["DB_PORT"]
    val dbName = dotenv["DB_NAME"]
    val dbUser = dotenv["DB_USER"]
    val dbPassword = dotenv["DB_PASSWORD"]

    println("DB_HOST: $dbHost")
    println("DB_PORT: $dbPort")
    println("DB_NAME: $dbName")
    println("DB_USER: $dbUser")
    println("DB_PASSWORD: $dbPassword")

    System.setProperty("spring.datasource.url", "jdbc:postgresql://$dbHost:$dbPort/$dbName")
    System.setProperty("spring.datasource.username", dbUser)
    System.setProperty("spring.datasource.password", dbPassword)

    runApplication<SnippetApplication>(args.toString())
}
