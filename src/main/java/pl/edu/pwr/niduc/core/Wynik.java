package pl.edu.pwr.niduc.core;

/**
 * Klasa przechowujaca oryginalne dane oraz wyliczony dla nich kod kontrolny
 */
public class Wynik {

    private final boolean[] probka; // oryginalne dane
    private final boolean[] kod; // wyliczona suma/crc


    public Wynik(boolean[] probka, boolean[] kod) {
        this.probka = probka;
        this.kod = kod;
    }

    /**
     * Metoda pomocnicza
     * Zwraca jedna tablice probka + wyliczony kod
     */
    public boolean[] getRamkaTransmisyjna() {
        boolean[] ramka = new boolean[probka.length + kod.length];
        System.arraycopy(probka, 0, ramka, 0, probka.length);
        System.arraycopy(kod,0, ramka, probka.length, kod.length);
        return ramka;
    }

    public boolean[] getProbka() { return probka; }
    public boolean[] getKod() { return kod; }
}
