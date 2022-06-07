package ir.ayantech.whygoogle.helper

import android.os.Build
import android.text.Html
import android.widget.TextView

fun TextView.setHtmlText(html: String?) {
    text = when {
        html == null -> null
        !html.isHtml() -> html
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(
            html,
            Html.FROM_HTML_MODE_COMPACT
        )
        else -> Html.fromHtml(html)
    }
}

fun String.isHtml() =
    when {
        this.startsWith("<html") -> true
        this.replace(" ", "").contains("<br/>") -> true
        this.replace(" ", "").contains("<br>") -> true
        this.replace(" ", "").contains("<p/>") -> true
        this.replace(" ", "").contains("<p>") -> true
        else -> false
    }