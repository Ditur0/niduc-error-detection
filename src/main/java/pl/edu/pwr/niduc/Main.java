package pl.edu.pwr.niduc;

import pl.edu.pwr.niduc.algorithms.AlgorytmBitParzystosci;
import pl.edu.pwr.niduc.algorithms.AlgorytmDetekcji;
import pl.edu.pwr.niduc.simulation.ParametrySymulacji;
import pl.edu.pwr.niduc.simulation.Raport;
import pl.edu.pwr.niduc.simulation.SilnikSymulacji;

import java.util.List;

public class Main {

    /**
     *  TODO:
     *      - klasa GeneratorProbek naraize generuje probki i liste probek ale pamietac o tym ze te probki sa jeszcze dzielona na pakiety
     *      - moze jakied ladne README z przykladami zeby juz bylo do protfolio
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
                new AlgorytmBitParzystosci()
                // new AlgorytmCRC16(),
                // new AlgorytmInternetCheckSum()
        );

        // Inicjalizacja narzedzi
        Raport raport = new Raport("wyniki_symulacji.csv");
        SilnikSymulacji silnik = new SilnikSymulacji(parametry, algorytmy);

        silnik.uruchom(raport);

        // Odbieranie raportu
        raport.wyswietlPodsumowanieWTerminalu(parametry);
        raport.zamknij(); // Zapisuje i zamyka plik CSV
    }
}
