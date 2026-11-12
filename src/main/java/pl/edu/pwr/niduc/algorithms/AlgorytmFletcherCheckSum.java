package pl.edu.pwr.niduc.algorithms;

import pl.edu.pwr.niduc.core.Wynik;

import java.util.Arrays;

public class AlgorytmFletcherCheckSum implements AlgorytmDetekcji {

    private final int modul;
    private final int bitowWBloku;
    private final int bitowKodu;

    public AlgorytmFletcherCheckSum(int bitowWBloku) {
        this.bitowWBloku = bitowWBloku;
        this.modul = (1 << bitowWBloku) - 1;   // 255 lub 65535
        this.bitowKodu = 2 * bitowWBloku;      // 16 lub 32
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
        return "Fletcher Checksum (" + bitowKodu + "-bit)";
    }

    private boolean[] obliczKod(boolean[] probka) {
        int sumA = 0;
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
}
