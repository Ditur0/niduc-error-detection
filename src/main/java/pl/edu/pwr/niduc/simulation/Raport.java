package pl.edu.pwr.niduc.simulation;

import pl.edu.pwr.niduc.UI.InformacjeOProbce;
import pl.edu.pwr.niduc.core.Blad;
import pl.edu.pwr.niduc.core.Wynik;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Odpowiada za zbieranie statystyk, zapisywanie kazdego przypadku do pliku (wraz z ciagami bitow)
 * ladnie wypisuje w konsoli
 */
public class Raport {

    // Klucz laczacy algorytm z konkretnym modelem bledu
    private record KluczStatystyk(String algorytm, String modelBledu) {}

    List<InformacjeOProbce> listaInformacji;

    private final Map<KluczStatystyk, StatystykiAlgorytmu> statystykiMap = new LinkedHashMap<>();
    private PrintWriter csvWriter;

    public Raport(String nazwaPlikuCsv) {
        try {
            csvWriter = new PrintWriter(new FileWriter(nazwaPlikuCsv));
            csvWriter.println("Algorytm;ModelBledu;IdProbki;NarzuconeBledy;CzyWykryto;CzyKolizja;BityZakodowane;BityOdebrane");
        } catch (IOException e) {
            System.err.println("Nie udalo sie otworzyc pliku raportu: " + e.getMessage());
        }

        listaInformacji = new ArrayList<>();
    }

    public void zarejestrujWynik(String nazwaAlgorytmu, String nazwaModeluBledu, int idProbki,
                                 int faktyczneBledy, boolean wykrytoBlad,
                                 boolean[] bityZakodowane, boolean[] bityOdebrane, Wynik wynik, Blad blad) {

        KluczStatystyk klucz = new KluczStatystyk(nazwaAlgorytmu, nazwaModeluBledu);
        StatystykiAlgorytmu statystyki = statystykiMap.computeIfAbsent(klucz, k -> new StatystykiAlgorytmu());

        boolean kolizja = false;

        if (faktyczneBledy > 0) {
            statystyki.ramkiUszkodzone++;
            if (wykrytoBlad) {
                statystyki.bledyWykryte++;
            } else {
                statystyki.kolizje++;
                kolizja = true;
            }
        }

        listaInformacji.add(new InformacjeOProbce(idProbki, wynik, blad, faktyczneBledy, wykrytoBlad, kolizja));


        // Zapis szczegolowy (tylko ramki uszkodzone)
        if (csvWriter != null && faktyczneBledy > -1) {
            String bityWysl = bityNaString(bityZakodowane);
            String bityOdeb = bityNaString(bityOdebrane);

            csvWriter.printf("%s;%s;%d;%d;%b;%b;%s;%s%n",
                    nazwaAlgorytmu, nazwaModeluBledu, idProbki, faktyczneBledy,
                    wykrytoBlad, kolizja, bityWysl, bityOdeb);
        }
    }

    public void zapiszPodsumowanieDoCsv(ParametrySymulacji parametry, String nazwaPliku) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(nazwaPliku))) {
            pw.println("Algorytm;ModelBledu;DlugoscProbki;WyslaneOgolem;Uszkodzone;Wykryte;Kolizje;Skutecznosc");

            for (Map.Entry<KluczStatystyk, StatystykiAlgorytmu> entry : statystykiMap.entrySet()) {
                KluczStatystyk klucz = entry.getKey();
                StatystykiAlgorytmu stat = entry.getValue();
                double skutecznosc = stat.ramkiUszkodzone > 0 ?
                        ((double) stat.bledyWykryte / stat.ramkiUszkodzone) * 100.0 : 100.0;

                pw.printf("%s;%s;%d;%d;%d;%d;%d;%.4f%n",
                        klucz.algorytm(), klucz.modelBledu(), parametry.dlugoscProbki,
                        parametry.iloscProbek, stat.ramkiUszkodzone,
                        stat.bledyWykryte, stat.kolizje, skutecznosc);
            }
            System.out.println("Zapisano podsumowanie do pliku: " + nazwaPliku);
        } catch (IOException e) {
            System.err.println("Blad zapisu podsumowania: " + e.getMessage());
        }
    }

    public void wyswietlPodsumowanieWTerminalu(ParametrySymulacji parametry) {
        System.out.println("\n=========================================");
        System.out.println("       RAPORT Z SYMULACJI (KONSOLA)      ");
        System.out.println("=========================================");
        System.out.println("Wyslane probki ogolem:  " + parametry.iloscProbek);
        System.out.println("Dlugosc probki danych:  " + parametry.dlugoscProbki + " bitow");
        System.out.println("-----------------------------------------");

        for (Map.Entry<KluczStatystyk, StatystykiAlgorytmu> entry : statystykiMap.entrySet()) {
            KluczStatystyk klucz = entry.getKey();
            StatystykiAlgorytmu stat = entry.getValue();

            System.out.printf("Algorytm: %-26s | Model: %s%n", klucz.algorytm(), klucz.modelBledu());
            System.out.println(" - Ramki uszkodzone:        " + stat.ramkiUszkodzone);
            System.out.println(" - Wykryte:                 " + stat.bledyWykryte);
            System.out.println(" - Niewykryte (KOLIZJE):    " + stat.kolizje);

            if (stat.ramkiUszkodzone > 0) {
                double skutecznosc = ((double) stat.bledyWykryte / stat.ramkiUszkodzone) * 100.0;
                System.out.printf(" - Skutecznosc detekcji:    %.4f%%%n", skutecznosc);
            }
            System.out.println("-----------------------------------------");
        }
    }

    public void zamknij() {
        if (csvWriter != null) {
            csvWriter.close();
            System.out.println("Zapisano pelne dane i bity do szczegolowego pliku CSV.");
        }
    }

    public List<InformacjeOProbce> getDane()
    {
        return listaInformacji;
    }

    private static class StatystykiAlgorytmu {
        int ramkiUszkodzone = 0;
        int bledyWykryte = 0;
        int kolizje = 0;
    }

    private String bityNaString(boolean[] bity) {
        StringBuilder sb = new StringBuilder(bity.length);
        for (boolean b : bity) {
            sb.append(b ? "1" : "0");
        }
        return sb.toString();
    }
}
