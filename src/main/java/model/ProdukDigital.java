package main.java.model;

public class ProdukDigital {
    public static final int PERSEN_PPN = 11;

    private String kodeProduk;
    private String judul;
    private Developer developer;
    private double harga;

    public ProdukDigital(String kodeProduk, String judul, Developer developer, double harga) {
        setKodeProduk(kodeProduk);
        setJudul(judul);
        setDeveloper(developer);
        setHarga(harga);
    }

    public ProdukDigital() {
    }

    public void setKodeProduk(String kodeProduk) {
        this.kodeProduk = kodeProduk;
    }

    public String getKodeProduk() {
        return kodeProduk;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getJudul() {
        return judul;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setHarga(double harga) {
        if (harga < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif.");
        }
        this.harga = harga;
    }

    public double getHarga() {
        return harga;
    }

    public double hitungPPN(double harga) {
        return harga * PERSEN_PPN / 100;
    }
}