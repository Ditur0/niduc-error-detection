package pl.edu.pwr.niduc.simulation;

public class ParametrySymulacji {
    public final int iloscProbek;
    public final int dlugoscProbki;
    public final double stopaBledu;
    public final long bazaSeed;

    public ParametrySymulacji(int iloscProbek, int dlugoscProbki, double stopaBledu, long bazaSeed) {
        this.iloscProbek = iloscProbek;
        this.dlugoscProbki = dlugoscProbki;
        this.stopaBledu = stopaBledu;
        this.bazaSeed = bazaSeed;
    }
}
