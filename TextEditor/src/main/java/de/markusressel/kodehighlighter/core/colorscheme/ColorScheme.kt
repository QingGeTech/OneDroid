package de.markusressel.kodehighlighter.core.colorscheme

import de.markusressel.kodehighlighter.core.StyleFactory
import de.markusressel.kodehighlighter.core.rule.LanguageRule

/**
 * A color scheme for a language rule book.
 * This essentially maps rules to the styles that will be applied to matched text passages.
 */
interface ColorScheme {

    /**
     * Get a set of styles to apply for a specific language rule
     */
    fun getStyles(type: LanguageRule): Set<StyleFactory>

}
