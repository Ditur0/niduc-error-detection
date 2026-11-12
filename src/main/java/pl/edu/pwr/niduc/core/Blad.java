package pl.edu.pwr.niduc.core;

/**
 * Reprezentuje wynik po przejsciu przez zaszumiony kanal (z nalozonymi bledami)
 * czyli nasza RAMKA ODEBRANA (moze zawierac bledy po przejsciu tunelu transmisyjnego)
 */
public class Blad {

    private final Wynik wynikZBledami;
    private final int iloscBledowNalozonych;

    public Blad(Wynik wynikZBledami, int iloscBledowNalozonych) {
        this.wynikZBledami = wynikZBledami;
        this.iloscBledowNalozonych = iloscBledowNalozonych;
    }

    public Wynik getWynikZBledami() { return wynikZBledami; }
    public int getIloscBledowNalozonych() { return iloscBledowNalozonych; }

    public boolean[] getProbka() { return wynikZBledami.getProbka(); }
    public boolean[] getKod() { return wynikZBledami.getKod(); }
}
