package de.markusressel.kodehighlighter.core.rule

/**
 * Helper to implement [LanguageRule] more easily
 */
object RuleHelper {

    /**
     * Finds all matches for the given regex in the given text
     *
     * @param text the text to use
     * @param regex the regex to use
     * @return list of matches
     */
    fun findRegexMatches(text: CharSequence, regex: Regex) = regex.findAll(text).map {
        RuleMatch(it.range.first, it.range.last + 1)
    }.toList()

}