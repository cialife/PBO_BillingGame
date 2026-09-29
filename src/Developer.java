public class Developer extends Pengguna {
    private String namaDeveloper;

    public Developer(String accountID, String username, String email, String namaDeveloper) {
        super(accountID, username, email);
        this.namaDeveloper = namaDeveloper;
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
}