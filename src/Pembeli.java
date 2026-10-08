public class Pembeli extends Pengguna {
    private String alamatIP;
    private String lokasi;
    private int poinStream;

    public Pembeli(String accountID, String username, String email, String alamatIP, String lokasi, int poinStream) {
        super(accountID, username, email);
        this.alamatIP = alamatIP;
        this.lokasi = lokasi;
        this.poinStream = poinStream;
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
    
    public void setPoinStream(int poinStream) { 
        this.poinStream = poinStream; 
    }

    public int getPoinStream() { 
        return poinStream; 
    }

    @Override
    public void masukAkun() {
        System.out.println("Pembeli " + getUsername() + " berhasil masuk.");
    }
}