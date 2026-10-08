public class ProdukDigital {
    public static final int PERSEN_PPN = 11;

    private String kodeProduk;
    private String judul;
    private double harga;
    private Developer developer;

    public ProdukDigital(String kodeProduk, String judul, Developer developer, double harga) {
        this.kodeProduk = kodeProduk;
        this.judul = judul;
        this.developer = developer;
        this.harga = harga;
    }

    public ProdukDigital() {}

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

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public double getHarga() {
        return harga;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public double hitungPPN(double harga) {
        return harga * PERSEN_PPN / 100;
    }
}