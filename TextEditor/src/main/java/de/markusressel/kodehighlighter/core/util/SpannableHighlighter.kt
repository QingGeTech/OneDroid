package de.markusressel.kodehighlighter.core.util

import android.text.Spannable
import android.text.style.CharacterStyle
import de.markusressel.kodehighlighter.core.LanguageRuleBook
import de.markusressel.kodehighlighter.core.RuleMatches
import de.markusressel.kodehighlighter.core.StyleFactory
import de.markusressel.kodehighlighter.core.colorscheme.ColorScheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Manages the interaction between a [Spannable] and a [LanguageRuleBook]
 */
open class SpannableHighlighter(
        private val languageRuleBook: LanguageRuleBook,
        private val colorScheme: ColorScheme = languageRuleBook.defaultColorScheme)
    : LanguageRuleBook by languageRuleBook {

    /**
     * Highlight the given text
     *
     * @param spannable the [Spannable] to apply highlighting to
     */
    open suspend fun highlight(spannable: Spannable) {
        val highlightEntities = languageRuleBook.createHighlighting(spannable)
        highlight(spannable, highlightEntities)
    }

    /**
     * Highlight the given text
     *
     * @param spannable the [Spannable] to apply highlighting to
     * @param ruleMatches a list of [RuleMatches] objects that hold the styles to apply
     */
    open suspend fun highlight(spannable: Spannable, ruleMatches: List<RuleMatches>) {
        coroutineScope {
            ruleMatches.map {
                async {
                    val styleFactories = colorScheme.getStyles(it.rule)
                    it.matches.map { match ->
                        highlight(spannable, match.startIndex, match.endIndex, styleFactories)
                    }
                }
            }.awaitAll()
        }
    }

    /**
     * Apply a set of styleFactories to a specific part of an spannable
     *
     * @param spannable the spannable to highlighting
     * @param start the starting position
     * @param end the endIndex position (inclusive)
     * @param styleFactories a set of the style factories to apply
     */
    open fun highlight(spannable: Spannable, start: Int, end: Int, styleFactories: Set<StyleFactory>) {
        styleFactories.forEach {
            applyStyle(it(), spannable, start, end)
        }
    }

    /**
     * Applies the given style to a spannable at a specific position
     */
    open fun applyStyle(style: CharacterStyle, spannable: Spannable, start: Int, end: Int) {
        spannable.setSpan(style, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}