package main.java.model;

public class Developer extends Pengguna {
    private String namaDeveloper;

    public Developer(String accountID, String username, String email, String namaDeveloper) {
        super(accountID, username, email);
        setNamaDeveloper(namaDeveloper);
    }

    public Developer() {
        super();
    }

    public void setNamaDeveloper(String namaDeveloper) {
        this.namaDeveloper = namaDeveloper;
    }

    public String getNamaDeveloper() {
        return namaDeveloper;
    }

    @Override
    public void masukAkun() {
        System.out.println("Developer " + getUsername() + " berhasil masuk.");
    }

    @Override
    public boolean validate() {
        return usernameValid() && emailValid() && passwordValid()
                && namaDeveloper != null && !namaDeveloper.isBlank();
    }
}