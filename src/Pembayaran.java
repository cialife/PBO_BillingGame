public class Pembayaran {
    private String metodePembayaran;

    public Pembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public Pembayaran() {}

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }
}