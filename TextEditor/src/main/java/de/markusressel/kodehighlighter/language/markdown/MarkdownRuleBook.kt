package de.markusressel.kodehighlighter.language.markdown

import de.markusressel.kodehighlighter.core.LanguageRuleBook
import de.markusressel.kodehighlighter.core.colorscheme.ColorScheme
import de.markusressel.kodehighlighter.core.rule.LanguageRule
import de.markusressel.kodehighlighter.language.markdown.colorscheme.DarkBackgroundColorScheme
import de.markusressel.kodehighlighter.language.markdown.rule.*

/**
 * Markdown language rule book
 */
class MarkdownRuleBook : LanguageRuleBook {

    override val defaultColorScheme: ColorScheme = DarkBackgroundColorScheme()

    override fun getRules(): Set<LanguageRule> {
        return setOf(
                HeadingRule(),
                ItalicRule(),
                BoldRule(),
                CodeInlineRule(),
                CodeLineRule(),
                TextLinkRule(),
                ImageLinkRule(),
                StrikeRule(),
                ListRule())
    }

}