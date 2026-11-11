package pl.edu.pwr.niduc.algorithms;

import pl.edu.pwr.niduc.core.Wynik;

/**
 * Przykladowy algorytm do sprawdzenia czy program dziala prawidlowo
 */
public class AlgorytmBitParzystosci implements AlgorytmDetekcji {

    @Override
    public Wynik koduj(boolean[] probka) {
        int liczbaJedynek = 0;

        // Zliczamy  jedynki w naszej probce
        for (boolean bit : probka) {
            if (bit) { liczbaJedynek++; }
        }

        // Jezeli ilosc jedynek jest nieparzysta to bit parzystosci musi byc 1 (true)
        // zeby ustalic ich laczna parzysta liczbe
        boolean bitParzystosci = (liczbaJedynek % 2 != 0);

        // Wygenerowany kod to tablica jednoelementowa
        boolean[] kod = new boolean[] { bitParzystosci };

        return new Wynik(probka, kod);
    }

    @Override
    public boolean sprawdz(Wynik wynikDoSprawdzenia) {
        int liczbaJedynek = 0;

        // Zliczamy jedynki w odebrancyh danych (ktore mogly ulec zepsuciu)
        for (boolean bit : wynikDoSprawdzenia.getProbka()) {
            if (bit) {
                liczbaJedynek++;
            }
        }

        // Dodajemy do puli jedynek odebrany bit kontrolny
        if (wynikDoSprawdzenia.getKod()[0]) {
            liczbaJedynek++;
        }

        // Jesli laczna liczba jedynek jest nadal parzysta
        // uznajemy ze nie wykryto bledu (zwraca true)
        return liczbaJedynek % 2 == 0;
    }

    @Override
    public String getNazwa() {
        return "Bit parzystosci";
    }
}
