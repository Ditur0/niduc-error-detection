package pl.edu.pwr.niduc.channel;

import pl.edu.pwr.niduc.core.Blad;
import pl.edu.pwr.niduc.core.Wynik;

import java.util.Random;

/**
 * Generuje ciagle zaklocenie o L bitow
 * Pokazuje ze kod CRC stopnia r wykrywa kazda wiazke o dlugoscni nie wiekszej niz r
 */
public class BladWiazkowy implements ModelBledu {

    private final int dlugoscWiazki; // Dlugosc L w bitach

    public BladWiazkowy(int dlugoscWiazki) {
        this.dlugoscWiazki = dlugoscWiazki;
    }

    @Override
    public Blad zaszum(Wynik oryginalnyWynik, long seed) {
        Random random = new Random(seed);
        boolean[] zepsuteDane = oryginalnyWynik.getProbka().clone();
        boolean[] zepsutyKod = oryginalnyWynik.getKod().clone();

        int calkowitaDlugosc = zepsuteDane.length + zepsutyKod.length;
        int l = Math.min(dlugoscWiazki, calkowitaDlugosc);
        if (l <= 0) {
            return new Blad(new Wynik(zepsuteDane, zepsutyKod), 0);
        }

        int start = random.nextInt(calkowitaDlugosc - l + 1);
        int licznikBledow = 0;

        // Pierwszy bit wiązki jest zawsze bledny
        odwrocBit(zepsuteDane, zepsutyKod, start);
        licznikBledow++;

        // Bity w srodku wiazki psuja sie z prawdopodobienstwem 50%
        for (int i = 1; i < l - 1; i++) {
            if (random.nextBoolean()) {
                odwrocBit(zepsuteDane, zepsutyKod, start + i);
                licznikBledow++;
            }
        }

        // Ostatni bit wiazki (jezli L > 1) tez jest zawsze bledny
        if (l > 1) {
            odwrocBit(zepsuteDane, zepsutyKod, start + l - 1);
            licznikBledow++;
        }

        return new Blad(new Wynik(zepsuteDane, zepsutyKod), licznikBledow);
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
        return "Wiazka (L=" + dlugoscWiazki + ")";
    }
}
