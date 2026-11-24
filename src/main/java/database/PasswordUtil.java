package main.java.database;

import org.mindrot.jbcrypt.BCrypt;
public class PasswordUtil {

    private PasswordUtil() {
    }

    public static String untukDisimpan(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(10));
    }

    public static boolean cocok(String plain, String tersimpan) {
        if (plain == null || tersimpan == null) {
            return false;
        }
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