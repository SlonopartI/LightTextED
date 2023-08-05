package com.slonoparti.texteditor.textclasses

import android.text.Editable
import android.text.TextWatcher
import android.text.style.StyleSpan
import androidx.core.text.getSpans
import com.slonoparti.texteditor.MainActivity
import com.slonoparti.texteditor.activities.SettingsActivity

class CustomTextWatcher(var activity: MainActivity) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s != null) {
            if(s.getSpans<StyleSpan>(0, s.length).isNotEmpty()){
                val span:StyleSpan=s.getSpans<StyleSpan>(0,s.length)[0]
                s.removeSpan(span)
            }
            SettingsActivity.applySettingsToText(s,activity)
        }
    }
}