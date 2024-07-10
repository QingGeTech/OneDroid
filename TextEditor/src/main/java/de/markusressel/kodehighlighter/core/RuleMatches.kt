package de.markusressel.kodehighlighter.core

import de.markusressel.kodehighlighter.core.rule.LanguageRule
import de.markusressel.kodehighlighter.core.rule.RuleMatch

/**
 * Data class that describes what styles would need to be applied for a given rule
 */
data class RuleMatches(
        val rule: LanguageRule,
        val matches: List<RuleMatch>
)