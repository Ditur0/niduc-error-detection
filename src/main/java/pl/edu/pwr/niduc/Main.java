package pl.edu.pwr.niduc;

import pl.edu.pwr.niduc.algorithms.AlgorytmBitParzystosci;
import pl.edu.pwr.niduc.algorithms.AlgorytmDetekcji;
import pl.edu.pwr.niduc.algorithms.AlgorytmInternetCheckSum;
import pl.edu.pwr.niduc.algorithms.FabrykaCRC;
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
     *      - Implementacja kodow cyklicznych CRC
     *
     *      - dedykowane klasy bledow (do przeanalizowania konrektych rodzajow bledow)
     *      takich jak bledy podwojne i wielokrotne (k-krotne), wiazki bledow
     *      czyli dopisac dla klast GeneratorBledow
     *
     *      -zroznicowac dlugosci ramek bo w zadaniu jet napisane ze stopien wielomianu CRC powinien rosnac wraz z dlugoscia chronionego bloku
     *      czyli kod testujacy musi przeprowadzic symulacje dla roznych wartosci parametru 'dlugoscProbki'
     *      np. krotkich wiadomosciach 64 bity oraz duzych blokow danych do 1500 bajtow ktore sa wykorzystywane w ethernecie
     */

    static void main() {

        // Definiujemy parametry
        ParametrySymulacji parametry = new ParametrySymulacji(
                10000,
                64,
                0.05,
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
                FabrykaCRC.stworzCrc32()
        );

        // Inicjalizacja narzedzi
        Raport raport = new Raport("wyniki_symulacji.csv");
        SilnikSymulacji silnik = new SilnikSymulacji(parametry, algorytmy);

        silnik.uruchom(raport);

        // Odbieranie raportu
        raport.wyswietlPodsumowanieWTerminalu(parametry);
        raport.zapiszPodsumowanieDoCsv(parametry, "dane_do_wykresow.csv");
        raport.zamknij();
    }
}
