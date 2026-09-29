public class Pembeli extends Pengguna {
    private String alamatIP;
    private String lokasi;
    private int poinSteam;

    public Pembeli(String accountID, String username, String email, String alamatIP, String lokasi, int poinSteam) {
        super(accountID, username, email);
        this.alamatIP = alamatIP;
        this.lokasi = lokasi;
        this.poinSteam = poinSteam;
    }

    public Pembeli() {
        super();
    }

    public void setAlamatIP(String alamatIP) { 
        this.alamatIP = alamatIP; 
    }

    public String getAlamatIP() { 
        return alamatIP; 
    }

    public void setLokasi(String lokasi) { 
        this.lokasi = lokasi; 
    }

    public String getLokasi() { 
        return lokasi; 
    }
    
    public void setPoinSteam(int poinSteam) { 
        this.poinSteam = poinSteam; 
    }

    public int getPoinSteam() { 
        return poinSteam; 
    }

    @Override
    public void masukAkun() {
        System.out.println("Pembeli " + getUsername() + " berhasil masuk.");
    }
}