package pl.edu.pwr.niduc.algorithms;

import pl.edu.pwr.niduc.core.Wynik;

public class AlgorytmCRC implements AlgorytmDetekcji {

    private final String nazwa;
    private final boolean[] wielomianG; // Wielomian generujacy G(x)
    private final int r; // Stopien wuelomianu (liczba bitow kontrolnych r = n - k)

    public AlgorytmCRC(String nazwa, boolean[] wielomianG) {
        this.nazwa = nazwa;
        this.wielomianG = wielomianG;
        this.r = wielomianG.length - 1;
    }

    @Override
    public Wynik koduj(boolean[] probka) {
        // Zrobienie miejsca na bity kontrolne -> dopisanie do danych r zer
        boolean[] daneRozszerzone = new boolean[probka.length + r];
        System.arraycopy(probka, 0, daneRozszerzone, 0, probka.length);

        // Dzielenie i zapamietanie reszty R(X)
        boolean[] reszta = obliczReszte(daneRozszerzone);

        return new Wynik(probka, reszta);
    }

    @Override
    public boolean sprawdz(Wynik wynikDoSprawdzenia) {
        // Odbiorca dzieli odebrana ramkę (dane + kod) przez G(X)
        boolean[] odebranaRamka = wynikDoSprawdzenia.getRamkaTransmisyjna();
        boolean[] reszta = obliczReszte(odebranaRamka);

        // Reszta rowna zero znaczy z nie wykryto bledu
        for (boolean bit : reszta) {
            if (bit) {
                return false; // Znaleziono '1' czyli wykryto blad
            }
        }
        return true;
    }

    @Override
    public String getNazwa() {
        return nazwa;
    }

    /**
     * Dzielenie wielomianow z wykrzystaniem XOR
     * Jezeli najstarszy bit jest jedynka to robimy XOR z dzielnikiem i przesuwamy sie o jedna pozycje dalej
     */
    private boolean[] obliczReszte(boolean[] dane) {
        boolean[] temp = dane.clone();

        for (int i = 0; i <= temp.length - wielomianG.length; i++) {
            // Gdy z przodu jest jedynka robimy XOR z G(X)
            if (temp[i]) {
                for (int j = 0; j < wielomianG.length; j++) {
                    temp[i + j] ^= wielomianG[j];
                }
            }
        }

        // Reszta znajduje sie na ostatnich r pozycjach tablicy
        boolean[] reszta = new boolean[r];
        System.arraycopy(temp, temp.length - r, reszta, 0, r);
        return reszta;
    }
}
