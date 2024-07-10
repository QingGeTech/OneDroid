package de.markusressel.kodehighlighter.core

import android.text.Spannable
import android.text.style.CharacterStyle
import de.markusressel.kodehighlighter.core.colorscheme.ColorScheme
import de.markusressel.kodehighlighter.core.rule.LanguageRule
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * A function that creates a [CharacterStyle] that can be applied to a [Spannable]
 */
typealias StyleFactory = () -> CharacterStyle

/**
 * Interface for a language rule book, providing definitions of what to highlight
 */
interface LanguageRuleBook {

    /**
     * The default color scheme
     */
    val defaultColorScheme: ColorScheme

    /**
     * Get a set of rules for this highlighter
     */
    fun getRules(): Set<LanguageRule>

    /**
     * Analyzes the given text and creates a list of styles that would need to be applied
     * to it.
     *
     * @param charSequence the text to analyze
     * @return list of highlighting entities
     */
    suspend fun createHighlighting(charSequence: CharSequence): List<RuleMatches> {
        return coroutineScope {
            getRules().map { rule ->
                async {
                    val matches = rule.findMatches(charSequence)
                    if (matches.isNotEmpty()) {
                        RuleMatches(
                                rule = rule,
                                matches = matches)
                    } else {
                        null
                    }
                }
            }.awaitAll().filterNotNull()
        }
    }

}