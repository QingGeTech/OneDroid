package de.markusressel.kodehighlighter.language.markdown.colorscheme

import android.graphics.Color
import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import de.markusressel.kodehighlighter.core.StyleFactory
import de.markusressel.kodehighlighter.core.colorscheme.ColorScheme
import de.markusressel.kodehighlighter.core.rule.LanguageRule
import de.markusressel.kodehighlighter.language.markdown.rule.*

/**
 * A dark color scheme for markdown
 */
class DarkBackgroundColorScheme : ColorScheme {

    override fun getStyles(type: LanguageRule): Set<StyleFactory> {
        return when (type) {
            is BoldRule -> {
                setOf({ ForegroundColorSpan(Color.parseColor("#0091EA")) },
                        { StyleSpan(Typeface.BOLD) })
            }
            is ItalicRule -> {
                setOf({ ForegroundColorSpan(Color.parseColor("#0091EA")) }, { StyleSpan(Typeface.ITALIC) })
            }
            is CodeInlineRule, is CodeLineRule -> {
                setOf { ForegroundColorSpan(Color.parseColor("#00C853")) }
            }
            is HeadingRule -> {
                setOf { ForegroundColorSpan(Color.parseColor("#FF6D00")) }
            }
            is ImageLinkRule, is TextLinkRule -> {
                setOf { ForegroundColorSpan(Color.parseColor("#7C4DFF")) }
            }
            is StrikeRule -> {
                setOf { ForegroundColorSpan(Color.parseColor("#5D4037")) }
            }
            is ListRule -> {
                setOf { ForegroundColorSpan(Color.parseColor("#5f84d4")) }
            }
            else -> emptySet()
        }
    }

}