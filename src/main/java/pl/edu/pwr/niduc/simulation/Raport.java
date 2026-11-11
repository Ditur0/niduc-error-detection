package pl.edu.pwr.niduc.simulation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Odpowiada za zbieranie statystyk, zapisywanie kazdego przypadku do pliku (wraz z ciagami bitow)
 * ladnie wypisuje w konsoli
 */
public class Raport implements AutoCloseable {

    private final Map<String, StatystykiAlgorytmu> statystykiMap = new LinkedHashMap<>();
    private PrintWriter csvWriter;

    public Raport(String nazwaPlikuCsv) {
        try {
            csvWriter = new PrintWriter(new FileWriter(nazwaPlikuCsv));
            csvWriter.println("Algorytm;IdProbki;NarzuconeBledy;CzyWykryto;CzyKolizja;BityZakodowane;BityOdebrane");
        } catch (IOException e) {
            System.err.println("Nie udalo sie otworzyc pliku raportu: " + e.getMessage());
        }
    }

    // Metoda wywolywana przez Silnik po każdej probce
    public void zarejestrujWynik(String nazwaAlgorytmu, int idProbki, int faktyczneBledy, boolean wykrytoBlad, boolean[] bityZakodowane, boolean[] bityOdebrane) {

        // Pobierz albo utworz statystyki dla danego algorytmu
        statystykiMap.putIfAbsent(nazwaAlgorytmu, new StatystykiAlgorytmu());
        StatystykiAlgorytmu statystyki = statystykiMap.computeIfAbsent(nazwaAlgorytmu, k -> new StatystykiAlgorytmu());

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

        // Zapis do pliku CSV (teraz tylko ramki uszkodzone zeby nie zapychac CSV idealnymi transimsjami -> MOZNA TO USUNAC)
        if (csvWriter != null && faktyczneBledy > 0) {
            String bityWysl = bityNaString(bityZakodowane);
            String bityOdeb = bityNaString(bityOdebrane);

            csvWriter.printf("%s;%d;%d;%b;%b;%s;%s%n",
                    nazwaAlgorytmu, idProbki, faktyczneBledy, wykrytoBlad, kolizja, bityWysl, bityOdeb);
        }
    }

    public void wyswietlPodsumowanieWTerminalu(ParametrySymulacji parametry) {
        System.out.println("\n=========================================");
        System.out.println("       RAPORT Z SYMULACJI (KONSOLA)      ");
        System.out.println("=========================================");
        System.out.println("Wyslane probki ogolem:  " + parametry.iloscProbek);
        System.out.println("Długosc probki danych:  " + parametry.dlugoscProbki + " bitow");
        System.out.println("Prawdopodobienstwo (p): " + parametry.stopaBledu);
        System.out.println("-----------------------------------------");

        for (Map.Entry<String, StatystykiAlgorytmu> entry : statystykiMap.entrySet()) {
            String nazwa = entry.getKey();
            StatystykiAlgorytmu stat = entry.getValue();

            System.out.println("Algorytm: " + nazwa);
            System.out.println(" - Ramki uszkodzone:        " + stat.ramkiUszkodzone);
            System.out.println(" - Wykryte:                 " + stat.bledyWykryte);
            System.out.println(" - Niewykryte (KOLIZJE):    " + stat.kolizje);

            if (stat.ramkiUszkodzone > 0) {
                double skutecznosc = ((double) stat.bledyWykryte / stat.ramkiUszkodzone) * 100.0;
                System.out.printf(" - Skutecznosc detekcji:    %.2f%%%n", skutecznosc);
            }
            System.out.println("-----------------------------------------");
        }
    }

    @Override
    public void close() throws Exception {
        if (csvWriter != null) {
            csvWriter.close();
            System.out.println("Zapisano pelne dane i bity do pliku CSV");
        }
    }

    private static class StatystykiAlgorytmu {
        int ramkiUszkodzone = 0;
        int bledyWykryte = 0;
        int kolizje = 0;
    }

    // Pomocnicza do zamiany z bool[true,false,false...] na "10110"
    private String bityNaString(boolean[] bity) {
        StringBuilder sb = new StringBuilder(bity.length);
        for (boolean b : bity) {
            sb.append(b ? "1" : "0");
        }
        return sb.toString();
    }
}
