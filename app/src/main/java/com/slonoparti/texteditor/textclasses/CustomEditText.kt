package com.slonoparti.texteditor.textclasses

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import com.slonoparti.texteditor.activities.SettingsActivity


class CustomEditText(context: Context,attributeSet: AttributeSet): androidx.appcompat.widget.AppCompatEditText(context,attributeSet) {

    private var gutterWidth = 0
    private var gutterDigitCount = 0
    private var gutterMargin = 4.dpToPx()
    private val gutterTextPaint = Paint()
    private fun getTopVisibleLine(): Int {
        if (lineHeight == 0 || SettingsActivity.textSize==0) {
            return 0
        }
        val line = (scrollY / (lineHeight*(SettingsActivity.textSize/textSize))).toInt()
        if (line < 0) {
            return 0
        }
        return if (line >= lineCount) {
            lineCount - 1
        } else line
    }

    private fun getBottomVisibleLine(): Int {
        if (lineHeight == 0 || SettingsActivity.textSize==0) {
            return 0
        }
        val line = getTopVisibleLine() + height / lineHeight + 1
        if (line < 0) {
            return 0
        }
        return if (line >= lineCount) {
            lineCount - 1
        } else line
    }
    private fun updateGutter() {
        var count = 3
        var widestNumber = 0
        var widestWidth = 0f

        gutterDigitCount = lineCount.toString().length
        for (i in 0..9) {
            val width = paint.measureText(i.toString())
            if (width > widestWidth) {
                widestNumber = i
                widestWidth = width
            }
        }
        if (gutterDigitCount >= count) {
            count = gutterDigitCount
        }
        val builder = StringBuilder()
        for (i in 0 until count) {
            builder.append(widestNumber.toString())
        }
        gutterWidth = paint.measureText(builder.toString()).toInt()
        gutterWidth += gutterMargin
        if (paddingLeft != gutterWidth + gutterMargin) {
            setPadding(gutterWidth + gutterMargin, gutterMargin, paddingRight, 0)
        }
    }
    override fun onDraw(canvas: Canvas?) {
        if(SettingsActivity.isNumerationEnabled){
            updateGutter()
            super.onDraw(canvas)
            var topVisibleLine = getTopVisibleLine()
            val bottomVisibleLine = getBottomVisibleLine()
            val textRight = (gutterWidth - gutterMargin / 2) + scrollX
            gutterTextPaint.textAlign = Paint.Align.RIGHT
            gutterTextPaint.textSize =gutterMargin.toFloat()*4
            while (topVisibleLine <= bottomVisibleLine) {
                canvas?.drawText(
                    (topVisibleLine + 1).toString(),
                    textRight.toFloat(),
                    (layout.getLineBaseline(topVisibleLine)+paddingTop).toFloat(),
                    gutterTextPaint
                )
                topVisibleLine++
            }
        }
        else{
            setPadding(0,0,0,0)
            super.onDraw(canvas)
        }
    }
}

private fun Int.dpToPx(): Int {
    val r: Resources = Resources.getSystem()
    val px = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        r.displayMetrics
    )
    return px.toInt()
}
