package com.example.instagr.screens.common

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.instagr.R
import com.example.instagr.common.formatRelativeTimestamp
import java.util.*

fun Context.showToast(text: String?, duration: Int = Toast.LENGTH_SHORT) {
    text?.let { Toast.makeText(this, it, duration).show() }
}

fun coordinateBtnAndInputs(btn: Button, vararg inputs: EditText) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            btn.isEnabled = inputs.all { it.text.toString().isNotEmpty() }
        }
    }
    inputs.forEach { it.addTextChangedListener(watcher) }
    btn.isEnabled = inputs.all { it.text.toString().isNotEmpty() }
}

fun ImageView.loadImage(image: String?) = ifNotDestroyed {
    GlideApp.with(this).load(image).centerCrop().into(this)
}

fun TextView.setCaptionText(username: String, caption: String, date: Date? = null) {
    val usernameSpannable = SpannableString(username)
    usernameSpannable.setSpan(
        StyleSpan(Typeface.BOLD), 0, usernameSpannable.length
        , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    usernameSpannable.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            widget.context.showToast(context.getString(R.string.username_is_clicked))
        }

        override fun updateDrawState(ds: TextPaint) {}
    }, 0, usernameSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    val dateSpannable = date?.let {
        val dateText = formatRelativeTimestamp(date, Date())
        val spannableString = SpannableString(dateText)
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey)), 0, dateText.length
            , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString
    }

    text = SpannableStringBuilder().apply {
        append(usernameSpannable)
        append(" ")
        append(caption)
        dateSpannable?.let {
            append(" ")
            append(it)
        }
    }
    movementMethod = LinkMovementMethod.getInstance()
}


fun ImageView.loadUserPhoto(photoUrl: String?) = ifNotDestroyed {
    GlideApp.with(this).load(photoUrl).fallback(R.drawable.person).into(this)
}


private fun View.ifNotDestroyed(block: () -> Unit) {
    if (!(context as Activity).isDestroyed) {
        block()
    }
}

fun Editable.toStringOrNull(): String? {
    val str = toString()
    return if (str.isEmpty()) null else str
}

