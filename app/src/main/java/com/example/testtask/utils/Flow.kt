package com.example.testtask.utils

import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

inline fun EditText.bind(
    lifecycle: LifecycleOwner,
    inputName: MutableStateFlow<String>,
    crossinline onTextChanged: (String) -> Unit = {}
) {
    inputName.launchAndRepeatInLifecycle(lifecycle) { newValue ->
        if (newValue != text.toString()) setText(newValue)
    }
    doAfterTextChanged {
        val text = it.toString()
        inputName.value = text
        onTextChanged(text)
    }
}

fun Button.bindEnable(
    lifecycle: LifecycleOwner,
    enabledStatus: StateFlow<Boolean>
) = enabledStatus.launchAndRepeatInLifecycle(lifecycle) { isEnabled = it }

fun SwitchCompat.bindChecked(
    lifecycle: LifecycleOwner,
    enabledStatus: MutableStateFlow<Boolean>
) = enabledStatus.launchAndRepeatInLifecycle(lifecycle) {
    isChecked = it
    setOnCheckedChangeListener { _, isChecked ->
        enabledStatus.value = isChecked
    }
}

inline fun <T : Any> Flow<T>.launchAndRepeatInLifecycle(
    lifecycle: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline result: suspend (T) -> Unit
) {
    lifecycle.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(state) {
            filterNotNull().collectLatest {
                result(it)
            }
        }
    }
}
