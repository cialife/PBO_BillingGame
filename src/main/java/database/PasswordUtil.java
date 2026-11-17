package main.java.database;

// Satu-satunya tempat yang memutuskan bagaimana password disimpan dan dicocokkan.
// Saat ini password disimpan apa adanya (plaintext). Kalau nanti ingin di-hash,
// cukup ubah dua method di bawah ini; DAO tidak perlu disentuh lagi.
public class PasswordUtil {

    private PasswordUtil() {
    }

    // Nilai yang dimasukkan ke kolom password di database
    public static String untukDisimpan(String plain) {
        return plain;
    }

    // Membandingkan password yang diketik dengan yang tersimpan di database
    public static boolean cocok(String plain, String tersimpan) {
        return plain != null && tersimpan != null && plain.equals(tersimpan);
    }
}