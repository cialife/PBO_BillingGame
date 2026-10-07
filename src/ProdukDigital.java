public class ProdukDigital {
    private String kodeProduk;
    private String judul;
    private String publisher;
    private double harga;

    public ProdukDigital(String kodeProduk, String judul, String publisher, double harga) {
        this.kodeProduk = kodeProduk;
        this.judul = judul;
        this.publisher = publisher;
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

    public void setPublisher(String publisher) { 
        this.publisher = publisher; 
    }

    public String getPublisher() { 
        return publisher; 
    }

    public void setHarga(double harga) { 
        this.harga = harga; 
    }

    public double getHarga() { 
        return harga; 
    }

    public double hitungPPN(double harga) {
        return harga * 0.11;
    }
}