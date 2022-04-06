package ir.ayantech.whygoogle.helper

fun <T> List<T>.safeGet(position: Int) =
    when {
        position < 0 -> null
        position < size -> this[position]
        else -> null
    }
