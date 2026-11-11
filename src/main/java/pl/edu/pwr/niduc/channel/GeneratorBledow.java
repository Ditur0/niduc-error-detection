package pl.edu.pwr.niduc.channel;

import pl.edu.pwr.niduc.core.Blad;
import pl.edu.pwr.niduc.core.Wynik;

import java.util.Random;

/**
 *  Klasa nakladajaca bledy w oparciu o ustalona stope bledu
 *  (np. 0.1 czyli 10% szans na odwrocenie bitu) oraz ziarno (seed)
 */
public class GeneratorBledow {

    public Blad nakladajBledy(Wynik oryginalnyWynik, double stopaBledu, long seed) {
        Random random = new Random(seed);
        int iloscBledow = 0;

        // Klonujemy tablice (referencja) zeby nie nadpisac oruginalych danych w pamieci
        boolean[] zepsuteDane = oryginalnyWynik.getProbka().clone();
        boolean[] zepsutyKod = oryginalnyWynik.getKod().clone();

        // Psucie bitow danych
        for (int i = 0; i < zepsuteDane.length; i++) {
            if (random.nextDouble() < stopaBledu) {
                zepsuteDane[i] = !zepsuteDane[i]; // Odwracamy bit
                iloscBledow++;
            }
        }

        // Psucie bitow kodu kontrolnego
        for (int i = 0; i < zepsutyKod.length; i++) {
            if (random.nextDouble() < stopaBledu) {
                zepsutyKod[i] = !zepsutyKod[i]; // Odwracamy bit
                iloscBledow++;
            }
        }

        Wynik zepsutyWynik = new Wynik(zepsuteDane, zepsutyKod);
        return new Blad(zepsutyWynik, iloscBledow);
    }
}
