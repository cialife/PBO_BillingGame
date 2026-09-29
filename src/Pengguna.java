public abstract class Pengguna implements Login {
    private String accountID;
    private String username;
    private String email;

    public Pengguna(String accountID, String username, String email) {
        this.accountID = accountID;
        this.username = username;
        this.email = email;
    }

    public Pengguna() {}

    public void setAccountID(String accountID) { 
        this.accountID = accountID; 
    }

    public String getAccountID() { 
        return accountID; 
    }

    public String getUsername() { 
        return username; 
    }

    public void setUsername(String username) { 
        this.username = username; 
    }

    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getEmail() { 
        return email; 
    }

    @Override
    public void daftarAkun() {
        System.out.println("Akun " + username + " berhasil mendaftar.");
    }

    @Override
    public abstract void masukAkun();
}