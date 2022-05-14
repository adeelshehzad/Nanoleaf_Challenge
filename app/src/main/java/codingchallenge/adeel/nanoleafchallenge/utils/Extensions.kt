package codingchallenge.adeel.nanoleafchallenge.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputLayout

/**
 * Extension function to validate strings
 */
fun String?.isValid(): Boolean {
    return this != null && this.isNotBlank()
}

/**
 * Extension function to get text from edittext inside TextInputLayout
 */
fun TextInputLayout.getText(): String {
    return editText?.text.toString()
}

fun View.hideKeyboard(context: Context) {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
}