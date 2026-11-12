package pl.edu.pwr.niduc;

import pl.edu.pwr.niduc.algorithms.*;
import pl.edu.pwr.niduc.channel.BladLosowyBSC;
import pl.edu.pwr.niduc.channel.BladWiazkowy;
import pl.edu.pwr.niduc.channel.BladWielokrotnyK;
import pl.edu.pwr.niduc.channel.ModelBledu;
import pl.edu.pwr.niduc.simulation.ParametrySymulacji;
import pl.edu.pwr.niduc.simulation.Raport;
import pl.edu.pwr.niduc.simulation.SilnikSymulacji;

import java.util.List;

public class Main {

    /**
     *  TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     *      - klasa GeneratorProbek naraize generuje probki i liste probek ale pamietac o tym ze te probki sa jeszcze dzielona na pakiety
     *      - moze jakied ladne README z przykladami zeby juz bylo do protfolio
     *      - sumaAdlera
     *      - sumaFletchera
     *      -zroznicowac dlugosci ramek bo w zadaniu jet napisane ze stopien wielomianu CRC powinien rosnac wraz z dlugoscia chronionego bloku
     *          czyli kod testujacy musi przeprowadzic symulacje dla roznych wartosci parametru 'dlugoscProbki'
     *          np. krotkich wiadomosciach 64 bity oraz duzych blokow danych do 1500 bajtow ktore sa wykorzystywane w ethernecie
     *      - Zrobic lepszy raport zeby byly dokladne dane zapisywane takie jakie chcemy zeby zrobic z tego wykresy
     */

    static void main() {

        // Definiujemy parametry
        ParametrySymulacji parametry = new ParametrySymulacji(
                10000,
                128,
                12345L
        );

        // Rejestracja algorytmow ktore chcemy porownac
        List<AlgorytmDetekcji> algorytmy = List.of(
                new AlgorytmBitParzystosci(),
                new AlgorytmInternetCheckSum(),
                FabrykaCRC.stworzCrc8Atm(),
                FabrykaCRC.stworzCrc16(),
                FabrykaCRC.stworzCrc12(),
                FabrykaCRC.stworzCrc16Ccitt(),
                FabrykaCRC.stworzCrc32(),
                new AlgorytmAdlerCheckSum(8),
                new AlgorytmAdlerCheckSum(16),
                new AlgorytmFletcherCheckSum(8),
                new AlgorytmFletcherCheckSum(16)
        );

        // Inicjalizacja roznych modeli bledow dla algorytmow
        List<ModelBledu> modeleBledow = List.of(
                new BladWielokrotnyK(1),
                new BladWielokrotnyK(2),
                new BladWielokrotnyK(3),
                new BladWielokrotnyK(4),
                new BladWiazkowy(8),
                new BladWiazkowy(12),
                new BladWiazkowy(16),
                new BladWiazkowy(24),
                new BladLosowyBSC(0.01)
        );


        // Inicjalizacja narzedzi
        Raport raport = new Raport("wyniki_symulacji.csv");
        SilnikSymulacji silnik = new SilnikSymulacji(parametry, algorytmy, modeleBledow);

        silnik.uruchom(raport);

        // Odbieranie raportu
        raport.wyswietlPodsumowanieWTerminalu(parametry);
        raport.zapiszPodsumowanieDoCsv(parametry, "dane_do_wykresow.csv");
        raport.zamknij();


    }
}
