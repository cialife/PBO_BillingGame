package main.java.model;

public class Game extends ProdukDigital {
    private String genre;
    private double ukuranFile;

    public Game(String kodeProduk, String judul, Developer developer, double harga, String genre, double ukuranFile) {
        super(kodeProduk, judul, developer, harga);
        setGenre(genre);
        setUkuranFile(ukuranFile);
    }

    public Game() {
        super();
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    public void setUkuranFile(double ukuranFile) {
        if (ukuranFile < 0) {
            throw new IllegalArgumentException("Ukuran file tidak boleh negatif.");
        }
        this.ukuranFile = ukuranFile;
    }

    public double getUkuranFile() {
        return ukuranFile;
    }

    @Override
    public String toString() {
        return getJudul();
    }
}