public class DLC extends Game {
    private String jenis;
    private Game baseGame;

    public DLC(String kodeProduk, String judul, String publisher, double harga, String genre, double ukuranFile, String jenis, Game baseGame) {
        super(kodeProduk, judul, publisher, harga, genre, ukuranFile);
        this.jenis = jenis;
        this.baseGame = baseGame;
    }

    public DLC() {
        super();
    }

    public void setBaseGame(Game baseGame) { 
        this.baseGame = baseGame; 
    }

    public Game getBaseGame() { 
        return baseGame; 
    }

    public void setJenis(String jenis) { 
        this.jenis = jenis; 
    }

    public String getJenis() { 
        return jenis; 
    }
}