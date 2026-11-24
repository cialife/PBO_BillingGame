package main.java.database;

import org.mindrot.jbcrypt.BCrypt;
// Satu-satunya tempat yang memutuskan bagaimana password disimpan dan dicocokkan.
// Password di-hask dengan BCrypt sebelum masuk database; DAO tidak menyimpan plaintext.
public class PasswordUtil {

    private PasswordUtil() {
    }

    // Nilai yang dimasukkan ke kolom password di database
    public static String untukDisimpan(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(10));
    }

    // Membandingkan password yang diketik dengan yang tersimpan di database
    public static boolean cocok(String plain, String tersimpan) {
        if (plain == null || tersimpan == null) {
            return false;
        }
        // Hask BCrypt selalu diawali $2
        if (!tersimpan.startsWith("$2")) {
            return false;
        }
        try {
            return BCrypt.checkpw(plain, tersimpan);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}