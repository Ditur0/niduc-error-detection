package pl.edu.pwr.niduc.channel;

import pl.edu.pwr.niduc.core.Blad;
import pl.edu.pwr.niduc.core.Wynik;

import java.util.Random;

/**
 * Bledy k-krotne
 * Odwraca dokladnie k losowych, roznych bitow w ramce
 * Pozwala zweryfikowac wykrywanie bledow podwojnych, potrojnych i nieparzystych
 */
public class BladWielokrotnyK implements ModelBledu {

    private final int k; // Dokladna liczba bitow do przeklamania

    public BladWielokrotnyK(int k) {
        this.k = k;
    }

    @Override
    public Blad zaszum(Wynik oryginalnyWynik, long seed) {
        Random random = new Random(seed);
        boolean[] zepsuteDane = oryginalnyWynik.getProbka().clone();
        boolean[] zepsutyKod = oryginalnyWynik.getKod().clone();

        int calkowitaDlugosc = zepsuteDane.length + zepsutyKod.length;
        int liczbaBledow = Math.min(k, calkowitaDlugosc);

        // Losowanie k unikalnych pozycji bez powtorzen (algorytm Fishera)
        int[] pozycje = new int[calkowitaDlugosc];
        for (int i = 0; i < calkowitaDlugosc; i++) {
            pozycje[i] = i;
        }

        for (int i = 0; i < liczbaBledow; i++) {
            int losowyIndeks = i + random.nextInt(calkowitaDlugosc - i);
            int wybranaPozycja = pozycje[losowyIndeks];
            pozycje[losowyIndeks] = pozycje[i];

            odwrocBit(zepsuteDane, zepsutyKod, wybranaPozycja);
        }

        return new Blad(new Wynik(zepsuteDane, zepsutyKod), liczbaBledow);
    }

    private void odwrocBit(boolean[] dane, boolean[] kod, int indeks) {
        if (indeks < dane.length) {
            dane[indeks] = !dane[indeks];
        } else {
            kod[indeks - dane.length] = !kod[indeks - dane.length];
        }
    }

    @Override
    public String getNazwa() {
        return "Wielokrotny (k=" + k + ")";
    }
}
