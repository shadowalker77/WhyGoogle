package ir.ayantech.whygoogle.helper

import android.app.Activity
import android.content.*
import android.content.Intent.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.widget.Toast
import androidx.core.app.ShareCompat
import java.io.ByteArrayInputStream
import java.net.URLDecoder

fun String.oldOpenUrl(context: Context?, failed: SimpleCallBack? = null) {
    if (context == null) return
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this))
    val activityInfo =
        browserIntent.resolveActivityInfo(context.packageManager, browserIntent.flags)
    if (activityInfo?.exported == true) {
        context.startActivity(browserIntent)
    } else {
        if (failed != null)
            failed()
        else {
            Toast.makeText(
                context,
                "امکان باز کردن لینک در دستگاه شما وجود ندارد.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

fun String.openUrl(context: Context?, failed: SimpleCallBack? = null) {
    if (context == null) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        try {
            val flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_REQUIRE_NON_BROWSER
            val browserIntent =
                Intent(ACTION_VIEW, Uri.parse(this)).addCategory(CATEGORY_BROWSABLE).addFlags(flags)
            context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this))
            val activityInfo =
                browserIntent.resolveActivityInfo(context.packageManager, browserIntent.flags)
            if (activityInfo?.exported == true) {
                context.startActivity(browserIntent)
            } else {
                if (failed != null)
                    failed()
                else {
                    Toast.makeText(
                        context,
                        "امکان باز کردن لینک در دستگاه شما وجود ندارد.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    } else {
        oldOpenUrl(context, failed)
    }
}

fun String.openPhoneWithNumber(context: Context?) {
    if (context == null) return
    var finalNumber = this
    if (finalNumber.contains("#")) {
        finalNumber = finalNumber.replace("#", "")
        finalNumber += Uri.encode("#")
    }
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$finalNumber")
    context.startActivity(intent)
}

fun String.copyToClipBoard(context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("ReferralCode", this)
    clipboard.setPrimaryClip(clip)
}

fun String.share(activity: Activity) {
    ShareCompat.IntentBuilder.from(activity)
        .setText(this)
        .setType("text/plain")
        .setChooserTitle("به اشتراک گذاری از طریق:")
        .startChooser()
}

fun String.openEmailAppSelectorWithEmailAddress(context: Context?) {
    if (context == null) return
    val intent = Intent(
        Intent.ACTION_SENDTO,
        Uri.fromParts("mailto", this, null)
    )
    context.startActivity(Intent.createChooser(intent, "انتخاب کنید"))
}

fun String.unify() =
    this.replace('ي', 'ی')
        .replace('ئ', 'ی')
        .replace('ك', 'ک')
        .replace('۰', '0')
        .replace('۱', '1')
        .replace('۲', '2')
        .replace('۳', '3')
        .replace('۴', '4')
        .replace('۵', '5')
        .replace('۶', '6')
        .replace('۷', '7')
        .replace('۸', '8')
        .replace('۹', '9')
        .replace('٠', '0')
        .replace('١', '1')
        .replace('٢', '2')
        .replace('٣', '3')
        .replace('٤', '4')
        .replace('٥', '5')
        .replace('٦', '6')
        .replace('٧', '7')
        .replace('٨', '8')
        .replace('٩', '9')

fun String.toPersianNumber() =
    this.replace('0', '۰')
        .replace('1', '۱')
        .replace('2', '۲')
        .replace('3', '۳')
        .replace('4', '۴')
        .replace('5', '۵')
        .replace('6', '۶')
        .replace('7', '۷')
        .replace('8', '۸')
        .replace('9', '۹')
        .replace('٠', '۰')
        .replace('١', '۱')
        .replace('٢', '۲')
        .replace('٣', '۳')
        .replace('٤', '۴')
        .replace('٥', '۵')
        .replace('٦', '۶')
        .replace('٧', '۷')
        .replace('٨', '۸')
        .replace('٩', '۹')

fun String.urlDecode(): String {
    return URLDecoder.decode(this, "UTF-8")
}

fun String.base64ToBitmap(): Bitmap =
    BitmapFactory.decodeStream(
        ByteArrayInputStream(
            Base64.decode(
                this, Base64.DEFAULT
            )
        )
    )

fun String.formatAmount(unit: String = "ریال", isNegative: Boolean = false): String {
    if (this.contains("-")) {
        return this.replace("-", "").formatAmount(isNegative = true)
    }
    var amountString = this
    var mod = amountString.length % 3
    if (mod == 0)
        mod = 3
    while (mod < amountString.length) {
        amountString =
            amountString.substring(0, mod) + "," + amountString.substring(mod, amountString.length)
        mod += 4
    }
    val negativeCare = if (isNegative) " -" else ""
    amountString = "$amountString$negativeCare $unit "
    if (this == "0 ریال") amountString.replace("0", "صفر")
    return amountString.trim()
}