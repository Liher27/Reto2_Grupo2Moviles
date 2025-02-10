import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.util.Base64

private const val KEY_ALGORITHM = "AES"
private const val CHARSET_NAME = "utf-8"

fun AESEncode(encodeKey: String, content: String): String? {
    return try {
        // Hash the encodeKey to get a fixed-size key (256 bits for AES-256)
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(encodeKey.toByteArray(charset(CHARSET_NAME)))

        // Use the first 16 bytes of the hash for AES-128 (or the full 32 bytes for AES-256)
        val key = SecretKeySpec(hash, KEY_ALGORITHM)

        val cipher = Cipher.getInstance(KEY_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val byteEncode: ByteArray = content.toByteArray(charset(CHARSET_NAME))

        val byteAES: ByteArray = cipher.doFinal(byteEncode)
        val AES_encode: String = Base64.getEncoder().encodeToString(byteAES)

        return AES_encode
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
