public class Game extends ProdukDigital {
    private String genre;
    private double ukuranFile;

    public Game(String kodeProduk, String judul, String publisher, double harga, String genre, double ukuranFile) {
        super(kodeProduk, judul, publisher, harga);
        this.genre = genre;
        this.ukuranFile = ukuranFile;
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
        this.ukuranFile = ukuranFile; 
    }

    public double getUkuranFile() { 
        return ukuranFile; 
    }
}