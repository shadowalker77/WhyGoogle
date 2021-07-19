package ir.ayantech.whygoogle.helper

fun Long.formatAmount(unit: String = "ریال"): String {
    return this.toString().formatAmount(unit, this < 0)
}

fun Long.toTimeCareString(): String {
    return if (this < 10) "0$this" else "$this"
}