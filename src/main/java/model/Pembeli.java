package main.java.model;

import java.util.regex.Pattern;

public class Pembeli extends Pengguna {
    private static final Pattern POLA_IPV4 = Pattern.compile(
            "^(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$");

    private String alamatIP;
    private String lokasi;
    private int poinStream;

    public Pembeli(String accountID, String username, String email, String alamatIP, String lokasi, int poinStream) {
        super(accountID, username, email);
        setAlamatIP(alamatIP);
        setLokasi(lokasi);
        setPoinStream(poinStream);
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
        this.poinStream = Math.max(0, poinStream);
    }

    public int getPoinStream() {
        return poinStream;
    }

    @Override
    public void masukAkun() {
        System.out.println("Pembeli " + getUsername() + " berhasil masuk.");
    }

    // Implementasi abstract method validate()
    @Override
    public boolean validate() {
        return usernameValid() && emailValid() && passwordValid()
                && alamatIP != null && POLA_IPV4.matcher(alamatIP).matches()
                && lokasi != null && !lokasi.isBlank();
    }
}