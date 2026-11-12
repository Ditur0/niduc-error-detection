package pl.edu.pwr.niduc.algorithms;

import pl.edu.pwr.niduc.core.Wynik;

import java.util.Arrays;

public class AlgorytmAdlerCheckSum implements AlgorytmDetekcji {

    private final int modul;
    private final int bitowWBloku;
    private final int bitowKodu;

    public AlgorytmAdlerCheckSum(int bitowWBloku) {
        this.bitowWBloku = bitowWBloku;
        this.modul = najwiekszaLiczbaPierwszaPonizej(1 << bitowWBloku); // 251 lub 65521
        this.bitowKodu = 2 * bitowWBloku;                                      // 16 lub 32
    }

    @Override
    public Wynik koduj(boolean[] probka) {
        return new Wynik(probka, obliczKod(probka));
    }

    @Override
    public boolean sprawdz(Wynik wynikDoSprawdzenia) {
        boolean[] oczekiwany = obliczKod(wynikDoSprawdzenia.getProbka());
        return Arrays.equals(oczekiwany, wynikDoSprawdzenia.getKod());
    }

    @Override
    public String getNazwa() {
        return "Adler Checksum (" + bitowKodu + "-bit)";
    }

    private boolean[] obliczKod(boolean[] probka) {
        int sumA = 1;
        int sumB = 0;

        for (int i = 0; i < probka.length; i += bitowWBloku) {
            int blok = 0;
            for (int j = 0; j < bitowWBloku; j++) {
                blok <<= 1;
                if (i + j < probka.length && probka[i + j]) {
                    blok |= 1;
                }
            }
            sumA = (sumA + blok) % modul;
            sumB = (sumB + sumA) % modul;
        }

        long wartosc = ((long) sumB << bitowWBloku) | sumA;

        boolean[] kod = new boolean[bitowKodu];
        for (int i = 0; i < bitowKodu; i++) {
            kod[i] = ((wartosc >> (bitowKodu - 1 - i)) & 1) == 1;
        }
        return kod;
    }

    private static int najwiekszaLiczbaPierwszaPonizej(int granica) {
        for (int n = granica - 1; n >= 2; n--) {
            if (czyPierwsza(n)) {
                return n;
            }
        }
        throw new IllegalArgumentException("Brak liczby pierwszej poniżej " + granica);
    }

    private static boolean czyPierwsza(int n) {
        for (int d = 2; (long) d * d <= n; d++) {
            if (n % d == 0) {
                return false;
            }
        }
        return true;
    }
}
