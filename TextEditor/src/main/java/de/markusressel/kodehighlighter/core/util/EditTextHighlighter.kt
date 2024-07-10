package de.markusressel.kodehighlighter.core.util

import android.text.Editable
import android.text.TextWatcher
import android.text.style.CharacterStyle
import android.widget.EditText
import de.markusressel.kodehighlighter.core.LanguageRuleBook
import de.markusressel.kodehighlighter.core.colorscheme.ColorScheme
import kotlinx.coroutines.*

/**
 * Convenience class for using a [LanguageRuleBook] in an [EditText]
 *
 * Note: If you need to highlight multiple [EditText] objects at the same time
 * be sure to also create one [EditTextHighlighter] instance for each [EditText].
 * Otherwise applied styles might not be cleared properly when refreshing highlighting
 * of an already highlighted [EditText].
 */
open class EditTextHighlighter(
        /**
         * The target [EditText] to apply syntax highlighting to
         */
        target: EditText,
        /**
         * The [LanguageRuleBook] to use
         */
        languageRuleBook: LanguageRuleBook,
        /**
         * The [ColorScheme] to use
         */
        colorScheme: ColorScheme = languageRuleBook.defaultColorScheme,
        /**
         * Time in milliseconds to debounce user input
         */
        debounceMs: Long = 100) {

    /**
     * The [LanguageRuleBook] to use
     */
    var languageRuleBook: LanguageRuleBook = languageRuleBook
        set(value) {
            field = value
            statefulSyntaxHighlighter = StatefulSpannableHighlighter(field, colorScheme)
        }

    /**
     * The [ColorScheme] to use
     */
    var colorScheme: ColorScheme = colorScheme
        set(value) {
            field = value
            statefulSyntaxHighlighter = StatefulSpannableHighlighter(languageRuleBook, field)
        }

    private var statefulSyntaxHighlighter = StatefulSpannableHighlighter(languageRuleBook, languageRuleBook.defaultColorScheme)
        set(value) {
            clearAppliedStyles()
            field = value
            refreshHighlighting()
        }

    private var highlightingJob: Job? = null

    /**
     * The [Editable] to work with
     */
    val editable: Editable
        get() = target.text

    /**
     * The target [EditText] syntax highlighting is applied to
     */
    var target: EditText = target
        set(value) {
            clearAppliedStyles()
            field = value
            refreshHighlighting()
        }

    /**
     * Time in milliseconds to debounce user input
     */
    var debounceMs: Long = debounceMs
        set(value) {
            field = value
            debouncedTextWatcher.delayMs = value
        }

    private val debouncedTextWatcher = DebouncedTextWatcher(
            delayMs = this.debounceMs,
            action = {
                refreshHighlighting()
            })

    private val realtimeTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            highlightingJob?.cancel("Text has changed")
            highlightingJob = null
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    /**
     * (Re-)Highlight the content of the [target]
     */
    open fun refreshHighlighting() {
        highlightingJob?.cancel("Requested new highlighting")
        highlightingJob = null

        highlightingJob = CoroutineScope(Dispatchers.Main).launch {
            val currentText = editable.toString()
            val highlightEntities = withContext(Dispatchers.Default) {
                statefulSyntaxHighlighter.createHighlighting(currentText)
            }
            val previousStyles = statefulSyntaxHighlighter.appliedStyles.copyOf()
            try {
                statefulSyntaxHighlighter.highlight(editable, highlightEntities)
            } catch (e: IndexOutOfBoundsException) {
                // text has changed while we were trying to apply styles to it
                // the next highlighting run will fix this
                clearAppliedStyles()
            }
            removeStyles(previousStyles)
        }
    }

    /**
     * Create a copy of a Set
     */
    private fun <T> Set<T>.copyOf(): Set<T> {
        val original = this
        return mutableSetOf<T>().apply { addAll(original) }
    }

    /**
     * Clear all currently applied styles
     */
    open fun clearAppliedStyles() = statefulSyntaxHighlighter.clearAppliedStyles(editable)

    /**
     * Remove a list of currently applied styles
     */
    open fun removeStyles(styles: Set<CharacterStyle>) {
        styles.forEach {
            editable.removeSpan(it)
            statefulSyntaxHighlighter.appliedStyles.remove(it)
        }
    }

    /**
     * Start continuous highlighting
     */
    open fun start() {
        target.addTextChangedListener(debouncedTextWatcher)
        target.addTextChangedListener(realtimeTextWatcher)
    }

    /**
     * Stop continuous highlighting
     */
    open fun cancel() {
        target.removeTextChangedListener(realtimeTextWatcher)
        target.removeTextChangedListener(debouncedTextWatcher)
    }
}