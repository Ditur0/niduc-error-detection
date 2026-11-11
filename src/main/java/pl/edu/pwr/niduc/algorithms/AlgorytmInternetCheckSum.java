package pl.edu.pwr.niduc.algorithms;

import pl.edu.pwr.niduc.core.Wynik;

public class AlgorytmInternetCheckSum implements AlgorytmDetekcji {

    @Override
    public Wynik koduj(boolean[] probka) {

        // Obliczamy sume 16-bitowych slow z przeniesieniem cyklicznym
        long suma = obliczSume(probka);

        // Negacja wszystkich bitow wyniku
        int checkSum = (int) (~suma & 0xFFFF);

        // Zamiana 16-bitowej liczby z powrotem na tablice bitow
        boolean[] kod = konwertujNaBity(checkSum);

        return new Wynik(probka, kod);
    }

    @Override
    public boolean sprawdz(Wynik wynikDoSprawdzenia) {
        // Weryfikacja: sumujemy chronione dane razem z odebrana suma kontrolna
        // Laczymy bity danych i kodu w jedna ramke
        boolean[] ramka = wynikDoSprawdzenia.getRamkaTransmisyjna();

        long suma = obliczSume(ramka);

        // Po dodaniu wszystkiego (z suma kontrolna) to poprawny wynik powiniec byc z samych jedynek
        // Zatem negacja bitowa tego wyniku musi dac wartosc zero (same zera)
        int wynikWeryfikacji = (int) (~suma & 0xFFFF);

        return wynikWeryfikacji == 0;
    }

    @Override
    public String getNazwa() {
        return "Internet Checksum (16-bit)";
    }

    /**
     * Zlicza sume 16-bitowych slow z przeniesiem cyklicznym
     * Metoda obsluguje tez sytuacje gdy brakuje bitow do pelych 16 (dopelnia zerami)
     */
    private long obliczSume(boolean[] bity) {
        long sum = 0;

        // Dzielimy strumien bitow na bloki po 16 bitow
        for (int i = 0; i < bity.length; i+=16) {
            int slowo16bit = 0;

            // Konstruowanie liczbu z bitow (od nastarszeo do najmlodszego)
            for (int j = 0; j < 16; j++) {
                if (i + j < bity.length && bity[i + j]) {
                    slowo16bit |= (1 << (15 - j)); // Ustawiamy j-ty bit od lewej
                }
            }

            sum += slowo16bit;

            // Przeniesienie cykliczne (end-around carry
            // Dopoki suma przekracza 16 bitow (czyli 0xFFFF),bierzemy to co powyzej i dodajmey na koncu
            while ((sum >> 16) > 0) {
                sum = (sum & 0xFFFF) + (sum >> 16);
            }
        }
        return sum;
    }

    // Pomocnicza zamieniajaca liczbe calkowita (wynik) na tablice 16 bitow
    private boolean[] konwertujNaBity(int wartosc16bit) {
        boolean[] bity = new boolean[16];
        for (int i = 0; i < 16; i++) {
            // Sprawdza stan kolejnych bitow maska bitowa przesunieta w odpowiednie miejsce
            bity[i] = (wartosc16bit & (1 << (15 - i))) != 0;
        }
        return bity;
    }
}
