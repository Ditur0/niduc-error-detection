package pl.edu.pwr.niduc.simulation;

import pl.edu.pwr.niduc.algorithms.AlgorytmDetekcji;
import pl.edu.pwr.niduc.channel.GeneratorBledow;
import pl.edu.pwr.niduc.channel.GeneratorProbek;
import pl.edu.pwr.niduc.core.Blad;
import pl.edu.pwr.niduc.core.Wynik;

import java.util.List;

/**
 * Wykonawca calej symulacji
 */
public class SilnikSymulacji {

    private final ParametrySymulacji parametry;
    private final List<AlgorytmDetekcji> algorytmyDoSprawdzenia;
    private final GeneratorProbek generatorProbek;
    private final GeneratorBledow generatorBledow;

    public SilnikSymulacji(ParametrySymulacji parametry, List<AlgorytmDetekcji> algorytmy) {
        this.parametry = parametry;
        this.algorytmyDoSprawdzenia = algorytmy;
        this.generatorProbek = new GeneratorProbek(parametry.bazaSeed);
        this.generatorBledow = new GeneratorBledow();
    }

    public void uruchom(Raport raport) {
        System.out.println("Generowanie " + parametry.iloscProbek + " probek bazowych...");
        List<boolean[]> listaProbek = generatorProbek.generujProbki(parametry.iloscProbek, parametry.dlugoscProbki);

        System.out.println("Uruchamianie silnika symulacji dla poszczegolnych algorytmów...\n");

        for (AlgorytmDetekcji algorytm : algorytmyDoSprawdzenia) {
            System.out.println("Przetwarzanie transmisji dla algorytmu: " + algorytm.getNazwa());

            for (int i = 0; i < listaProbek.size(); i++) {
                boolean[] oryginalnaProbka = listaProbek.get(i);

                // Kodowanie
                Wynik zakodowane = algorytm.koduj(oryginalnaProbka);

                // Kanal transmisyjny (nakladanie bledow z zachowanie seeda)
                long seedDlaRamki = parametry.bazaSeed + i;
                Blad odebrane = generatorBledow.nakladajBledy(zakodowane, parametry.stopaBledu, seedDlaRamki);

                // Odbior i weryfikacja
                boolean weryfikacja = algorytm.sprawdz(odebrane.getWynikZBledami());
                boolean wykrytoBlad = !weryfikacja;

                // Przekazanie danych do raportu
                raport.zarejestrujWynik(
                        algorytm.getNazwa(),
                        i,
                        odebrane.getIloscBledowNalozonych(),
                        wykrytoBlad,
                        zakodowane.getRamkaTransmisyjna(),
                        odebrane.getWynikZBledami().getRamkaTransmisyjna()
                );
            }
        }
    }
}
