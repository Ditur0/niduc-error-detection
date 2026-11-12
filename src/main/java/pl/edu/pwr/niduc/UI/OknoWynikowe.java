package pl.edu.pwr.niduc.UI;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

final class OknoWynikow {

    private final Stage okno;

    OknoWynikow(
            Stage oknoGlowne,
            KonfiguracjaSymulacji konfiguracja,
            List<InformacjeOProbce> wynikiProbek
    ) {
        Label naglowek = new Label("Wyniki symulacji");
        naglowek.setStyle(App.Ui.STYL_NAGLOWKA);

        Label podsumowanie = new Label(
                konfiguracja.wariant()
                        + "  ·  Liczba wyników: " + wynikiProbek.size()
                        + "  ·  Rozmiar próbki: " + konfiguracja.rozmiarProbki()
        );
        podsumowanie.setStyle(App.Ui.STYL_ETYKIETY);

        CheckBox odfiltrujNiezmienione = new CheckBox("Odfiltruj niezmienione");
        CheckBox odfiltrujWykryte = new CheckBox("Odfiltruj wykryte");
        odfiltrujNiezmienione.setStyle(App.Ui.STYL_ETYKIETY);
        odfiltrujWykryte.setStyle(App.Ui.STYL_ETYKIETY);

        HBox filtry = new HBox(
                App.Ui.ODSTEP_SEKCJI, odfiltrujNiezmienione, odfiltrujWykryte
        );

        TableView<InformacjeOProbce> tabela = new TableView<>();
        tabela.setFixedCellSize(30);
        tabela.setPlaceholder(new Label(
                wynikiProbek.isEmpty()
                        ? "Brak wyników do wyświetlenia"
                        : "Brak próbek spełniających wybrane filtry"
        ));

        tabela.getColumns().setAll(List.of(
                kolumna("ID próbki", 75, probka -> probka.id),
                kolumnaBitowa("Blok danych", 210,
                        probka -> probka.probka_bez_bledu.getProbka()),
                kolumnaBitowa("Wartość kontrolna", 135,
                        probka -> probka.probka_bez_bledu.getKod()),
                kolumnaBitowa("Blok danych z błędami", 210,
                        probka -> probka.probka_z_bledem.getProbka()),
                kolumnaBitowa("Wartość kontrolna z błędami", 190,
                        probka -> probka.probka_z_bledem.getKod()),
                kolumna("Liczba błędów", 95, probka -> probka.ilosc_bledow),
                kolumna("Błąd wykryty?", 105, probka -> probka.wykryto_blad ? "Tak" : "Nie"),
                kolumna("Kolizja?", 75, probka -> probka.kolizja ? "Tak" : "Nie")
        ));

        FilteredList<InformacjeOProbce> przefiltrowane = new FilteredList<>(
                FXCollections.observableArrayList(wynikiProbek)
        );

        Runnable aktualizujFiltry = () -> {
            boolean ukryjNiezmienione = odfiltrujNiezmienione.isSelected();
            boolean ukryjWykryte = odfiltrujWykryte.isSelected();

            przefiltrowane.setPredicate(probka ->
                    (!ukryjNiezmienione || probka.ilosc_bledow > 0)
                            && (!ukryjWykryte || !probka.wykryto_blad)
            );
        };

        odfiltrujNiezmienione.selectedProperty().addListener(
                (obs, poprzednia, aktualna) -> aktualizujFiltry.run()
        );
        odfiltrujWykryte.selectedProperty().addListener(
                (obs, poprzednia, aktualna) -> aktualizujFiltry.run()
        );

        SortedList<InformacjeOProbce> posortowane = new SortedList<>(przefiltrowane);
        posortowane.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setItems(posortowane);

        Label statystyki = new Label(obliczPodsumowanie(wynikiProbek));
        statystyki.setStyle(App.Ui.STYL_ETYKIETY);
        statystyki.setWrapText(true);

        VBox zawartosc = new VBox(
                App.Ui.ODSTEP_SEKCJI, naglowek, podsumowanie, filtry, tabela, statystyki
        );
        zawartosc.setPadding(new Insets(App.Ui.MARGINES_OKNA));
        zawartosc.setStyle(App.Ui.STYL_OKNA);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        okno = new Stage();
        okno.initOwner(oknoGlowne);
        okno.setTitle("Wyniki symulacji");
        okno.setScene(new Scene(zawartosc, 1200, 720));
    }

    void pokaz() {
        okno.show();
    }

    void zamknij() {
        okno.close();
    }

    private static String obliczPodsumowanie(List<InformacjeOProbce> wyniki) {
        if (wyniki.isEmpty()) {
            return "Brak próbek do obliczenia procentów.";
        }

        int wykryte = 0;
        int niewykryte = 0;
        int bezBledow = 0;

        for (InformacjeOProbce probka : wyniki) {
            if (probka.ilosc_bledow == 0) {
                bezBledow++;
            } else if (probka.wykryto_blad) {
                wykryte++;
            } else {
                niewykryte++;
            }
        }

        double przelicznik = 100.0 / wyniki.size();

        return String.format(
                Locale.forLanguageTag("pl-PL"),
                "Próbki z wykrytym błędem: %.2f%%%n"
                        + "Próbki z niewykrytym błędem: %.2f%%%n"
                        + "Próbki bez błędów: %.2f%%",
                wykryte * przelicznik,
                niewykryte * przelicznik,
                bezBledow * przelicznik
        );
    }

    private static <T> TableColumn<InformacjeOProbce, T> kolumna(
            String tytul,
            double szerokosc,
            Function<InformacjeOProbce, T> odczytajWartosc
    ) {
        TableColumn<InformacjeOProbce, T> kolumna = new TableColumn<>(tytul);
        kolumna.setPrefWidth(szerokosc);
        kolumna.setCellValueFactory(komorka ->
                new ReadOnlyObjectWrapper<>(odczytajWartosc.apply(komorka.getValue()))
        );
        return kolumna;
    }

    private static TableColumn<InformacjeOProbce, String> kolumnaBitowa(
            String tytul,
            double szerokosc,
            Function<InformacjeOProbce, boolean[]> odczytajBity
    ) {
        TableColumn<InformacjeOProbce, String> kolumna = kolumna(
                tytul, szerokosc, probka -> naCiagBitow(odczytajBity.apply(probka))
        );

        kolumna.setCellFactory(ignorowana -> new TableCell<>() {
            private final Tooltip podglad = new Tooltip();

            @Override
            protected void updateItem(String tekst, boolean pusta) {
                super.updateItem(tekst, pusta);

                if (pusta || tekst == null) {
                    setText(null);
                    setTooltip(null);
                    return;
                }

                setText(tekst);
                podglad.setText(tekst);
                setTooltip(podglad);
            }
        });

        return kolumna;
    }

    private static String naCiagBitow(boolean[] bity) {
        StringBuilder tekst = new StringBuilder(bity.length);
        for (boolean bit : bity) {
            tekst.append(bit ? '1' : '0');
        }
        return tekst.toString();
    }
}