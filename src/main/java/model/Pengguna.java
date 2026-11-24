package main.java.model;

import java.util.regex.Pattern;

public abstract class Pengguna implements Login {
    private static final Pattern POLA_USERNAME = Pattern.compile("^[A-Za-z0-9_.]{3,30}$");
    private static final Pattern POLA_EMAIL = Pattern.compile(
            "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*"
                    + "@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)+$");
    
    private String accountID;
    private String username;
    private String email;
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

    protected boolean usernameValid() {
        return username != null && POLA_USERNAME.matcher(username).matches();
    }

    protected boolean emailValid() {
        return email != null && POLA_EMAIL.matcher(email).matches();
    }

    protected boolean passwordValid() {
        return password != null;
    }

    @Override
    public void daftarAkun() {
        System.out.println("Akun " + username + " berhasil mendaftar.");
    }

    @Override
    public abstract void masukAkun();
    public abstract boolean validate();
}