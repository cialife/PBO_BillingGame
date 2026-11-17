package main.java.model;

public interface Cetak {
    // Field pada interface otomatis public static final
    String PENERBIT = "Valve Corporation";
    String ALAMAT = "PO Box 1688\nBellevue, WA 98009\nUnited States";
    String IDPPN = "021629266053000";

    void tampilkanInvoice();
}