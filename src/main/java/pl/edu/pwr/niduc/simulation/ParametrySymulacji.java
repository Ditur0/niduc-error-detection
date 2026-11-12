package pl.edu.pwr.niduc.simulation;

public class ParametrySymulacji {
    public final int iloscProbek;
    public final int dlugoscProbki;
    public final long bazaSeed;

    public ParametrySymulacji(int iloscProbek, int dlugoscProbki, long bazaSeed) {
        this.iloscProbek = iloscProbek;
        this.dlugoscProbki = dlugoscProbki;
        this.bazaSeed = bazaSeed;
    }
}
