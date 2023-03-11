package ir.ayantech.whygoogle.helper

import android.util.Base64
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun String.encryptAES(key: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val keyBytes = Arrays.copyOf(digest.digest(key.toByteArray(Charsets.UTF_8)), 16)
    val keySpec = SecretKeySpec(keyBytes, "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val ivSpec = IvParameterSpec(ByteArray(16))
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
    val encryptedBytes = cipher.doFinal(this.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
}

fun String.decryptAES(key: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val keyBytes = Arrays.copyOf(digest.digest(key.toByteArray(Charsets.UTF_8)), 16)
    val keySpec = SecretKeySpec(keyBytes, "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val ivSpec = IvParameterSpec(ByteArray(16))
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
    val decodedBytes = Base64.decode(this, Base64.DEFAULT)
    val decryptedBytes = cipher.doFinal(decodedBytes)
    return String(decryptedBytes, Charsets.UTF_8)
}


