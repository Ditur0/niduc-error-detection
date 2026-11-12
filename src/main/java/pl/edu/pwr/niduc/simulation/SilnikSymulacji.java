package pl.edu.pwr.niduc.simulation;

import pl.edu.pwr.niduc.algorithms.AlgorytmDetekcji;
import pl.edu.pwr.niduc.channel.GeneratorProbek;
import pl.edu.pwr.niduc.channel.ModelBledu;
import pl.edu.pwr.niduc.core.Blad;
import pl.edu.pwr.niduc.core.Wynik;

import java.util.List;

/**
 * Wykonawca calej symulacji dla wielu algorytmow i modeli bledow
 */
public class SilnikSymulacji {

    private final ParametrySymulacji parametry;
    private final List<AlgorytmDetekcji> algorytmyDoSprawdzenia;
    private final GeneratorProbek generatorProbek;
    private final List<ModelBledu> modeleBledow;

    public SilnikSymulacji(ParametrySymulacji parametry, List<AlgorytmDetekcji> algorytmy, List<ModelBledu> modeleBledow) {
        this.parametry = parametry;
        this.algorytmyDoSprawdzenia = algorytmy;
        this.generatorProbek = new GeneratorProbek(parametry.bazaSeed);
        this.modeleBledow = modeleBledow;
    }


    public void uruchom(Raport raport) {
        System.out.println("Generowanie " + parametry.iloscProbek + " probek bazowych...");
        List<boolean[]> listaProbek = generatorProbek.generujProbki(parametry.iloscProbek, parametry.dlugoscProbki);

        System.out.println("Uruchamianie silnika symulacji dla poszczegolnych algorytmów...\n  " + listaProbek.size());

        for (ModelBledu model : modeleBledow) {
            System.out.println("=== Model bledu: " + model.getNazwa() + " ===");

            for (AlgorytmDetekcji algorytm : algorytmyDoSprawdzenia) {
                System.out.println("  -> Testowanie: " + algorytm.getNazwa());

                for (int i = 0; i < listaProbek.size(); i++) {
                    boolean[] oryginalnaProbka = listaProbek.get(i);

                    // Kodowanie
                    Wynik zakodowane = algorytm.koduj(oryginalnaProbka);

                    // Kanal transmisyjny (nakladanie bledow z zachowanie seeda)
                    long seedDlaRamki = parametry.bazaSeed + i;
                    Blad odebrane = model.zaszum(zakodowane, seedDlaRamki);

                    // Odbior i weryfikacja
                    boolean weryfikacja = algorytm.sprawdz(odebrane.getWynikZBledami());
                    boolean wykrytoBlad = !weryfikacja;

                    // Przekazanie danych do raportu
                    raport.zarejestrujWynik(
                            algorytm.getNazwa(),
                            model.getNazwa(),
                            i,
                            odebrane.getIloscBledowNalozonych(),
                            wykrytoBlad,
                            zakodowane.getRamkaTransmisyjna(),
                            odebrane.getWynikZBledami().getRamkaTransmisyjna(),
                            zakodowane,
                            odebrane
                    );
                }
            }
        }
    }
}
