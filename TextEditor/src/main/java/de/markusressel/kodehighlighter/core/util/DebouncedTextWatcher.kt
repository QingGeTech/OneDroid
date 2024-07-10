package de.markusressel.kodehighlighter.core.util

import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * TextWatcher with built in support for debouncing fast input
 *
 * @param delayMs debounce delay in milliseconds
 * @param action action to execute after the text has stabilized
 */
open class DebouncedTextWatcher(
        var delayMs: Long,
        val action: ((CharSequence?) -> Unit))
    : TextWatcher {

    private var timer = Timer()

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = s?.toString() ?: ""

        timer.cancel()
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                CoroutineScope(Dispatchers.Main).launch {
                    action(text)
                }
            }
        }, delayMs)
    }

    override fun afterTextChanged(s: Editable?) = Unit
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
}
