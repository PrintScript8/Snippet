package austral.ingsis.snippet.repository

import austral.ingsis.snippet.model.Rule
import org.springframework.data.jpa.repository.JpaRepository

interface RulesRepository : JpaRepository<Rule, Long>
