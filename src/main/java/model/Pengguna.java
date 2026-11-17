package main.java.model;

import java.util.regex.Pattern;

public abstract class Pengguna implements Login {
    // Dikompilasi sekali (static final), bukan setiap validate() dipanggil
    private static final Pattern POLA_USERNAME = Pattern.compile("^[A-Za-z0-9_.]{3,30}$");
    private static final Pattern POLA_EMAIL = Pattern.compile(
            "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*"
                    + "@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)+$");
    // 8-20 karakter, minimal 1 angka, 1 huruf kecil, 1 huruf besar, 1 simbol, tanpa
    // spasi
    private static final Pattern POLA_PASSWORD = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*+=()_\\-])(?=\\S+$).{8,20}$");

    private String accountID;
    private String username;
    private String email;
    // Password plaintext: dipakai validate() lalu disimpan lewat PasswordUtil
    private String password;

    public Pengguna(String accountID, String username, String email) {
        setAccountID(accountID);
        setUsername(username);
        setEmail(email);
    }

    public Pengguna() {
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    // Pengecekan bersama yang dipakai validate() milik subclass
    protected boolean usernameValid() {
        return username != null && POLA_USERNAME.matcher(username).matches();
    }

    protected boolean emailValid() {
        return email != null && POLA_EMAIL.matcher(email).matches();
    }

    protected boolean passwordValid() {
        return password != null && POLA_PASSWORD.matcher(password).matches();
    }

    @Override
    public void daftarAkun() {
        System.out.println("Akun " + username + " berhasil mendaftar.");
    }

    @Override
    public abstract void masukAkun();

    // Abstract method: aturan validasi berbeda untuk tiap jenis pengguna
    public abstract boolean validate();
}