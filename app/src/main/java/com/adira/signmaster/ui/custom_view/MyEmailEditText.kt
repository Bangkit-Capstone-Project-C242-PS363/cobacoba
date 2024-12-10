package com.adira.signmaster.ui.custom_view

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.adira.signmaster.R

class MyEmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        setBackgroundResource(R.drawable.custom_edit_text_default)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!isValidEmail(s.toString())) {
                    showError("Invalid email format")
                } else {
                    clearError()
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setBackgroundResource(R.drawable.custom_edit_text_focused)
            } else {
                setBackgroundResource(R.drawable.custom_edit_text_default)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Email"
        setHintTextColor(ContextCompat.getColor(context, R.color.gray))
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showError(message: String) {
        error = message
        setTextColor(ContextCompat.getColor(context, R.color.red))
    }

    private fun clearError() {
        error = null
        setTextColor(ContextCompat.getColor(context, R.color.black))
    }
}

