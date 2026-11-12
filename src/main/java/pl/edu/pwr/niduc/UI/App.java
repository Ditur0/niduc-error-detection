package pl.edu.pwr.niduc.UI;

import pl.edu.pwr.niduc.UI.KonfiguracjaSymulacji.*;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.edu.pwr.niduc.algorithms.*;
import pl.edu.pwr.niduc.channel.ModelBledu;
import pl.edu.pwr.niduc.simulation.ParametrySymulacji;
import pl.edu.pwr.niduc.simulation.Raport;
import pl.edu.pwr.niduc.simulation.SilnikSymulacji;

import java.util.List;

public class App extends Application {

    private OknoWynikow oknoWynikow;

    @Override
    public void start(Stage okno) {
        BooleanProperty symulacjaTrwa = new SimpleBooleanProperty(false);

        ComboBox<RodzajAlgorytmu> algorytm = new ComboBox<>();
        algorytm.getItems().setAll(RodzajAlgorytmu.values());
        algorytm.setPromptText("Wybierz algorytm");

        ComboBox<WariantAlgorytmu> wariant = new ComboBox<>();
        wariant.setPromptText("Wybierz wariant");
        wariant.disableProperty().bind(algorytm.valueProperty().isNull());

        Label etykietaWariantu = new Label("Wariant algorytmu");

        algorytm.valueProperty().addListener((obs, poprzedni, wybrany) -> {
            wariant.getSelectionModel().clearSelection();
            wariant.setValue(null);

            switch (wybrany) {
                case CRC -> {
                    etykietaWariantu.setText("Wielomian CRC");
                    wariant.getItems().setAll(WielomianCrc.values());
                }
                case SUMA_KONTROLNA -> {
                    etykietaWariantu.setText("Rodzaj sumy");
                    wariant.getItems().setAll(RodzajSumy.values());
                }
                case null -> {
                    etykietaWariantu.setText("Wariant algorytmu");
                    wariant.getItems().clear();
                }
            }
        });

        TextField rozmiarProbki = new TextField();
        TextField liczbaProbek = new TextField();
        TextField seed = new TextField();

        ComboBox<RodzajBledu> rodzajBledu = new ComboBox<>();
        rodzajBledu.getItems().setAll(RodzajBledu.values());
        rodzajBledu.setPromptText("Wybierz rodzaj błędu");

        Label etykietaParametru = new Label("Parametr błędu");
        TextField parametrBledu = new TextField();
        parametrBledu.disableProperty().bind(rodzajBledu.valueProperty().isNull());

        rodzajBledu.valueProperty().addListener((obs, poprzedni, wybrany) -> {
            parametrBledu.clear();

            etykietaParametru.setText(switch (wybrany) {
                case WIAZKOWY -> "Długość wiązki";
                case WIELOKROTNY -> "Krotność przekłamania";
                case LOSOWY_BCS -> "Stopa błędu";
                case null -> "Parametr błędu";
            });

            parametrBledu.setPromptText(
                    wybrany == RodzajBledu.LOSOWY_BCS ? "np. 0,05" : ""
            );
        });

        Label naglowekWlasciwosci = new Label("Właściwości");
        naglowekWlasciwosci.setStyle(Ui.STYL_NAGLOWKA);

        VBox wlasciwosci = new VBox(
                Ui.ODSTEP_WIERSZY,
                naglowekWlasciwosci,
                wiersz(new Label("Rozmiar próbki"), rozmiarProbki),
                wiersz(new Label("Liczba próbek"), liczbaProbek),
                wiersz(new Label("Seed"), seed),
                wiersz(new Label("Rodzaj błędu"), rodzajBledu),
                wiersz(etykietaParametru, parametrBledu)
        );
        wlasciwosci.setPadding(new Insets(Ui.MARGINES_RAMKI));
        wlasciwosci.setStyle(Ui.STYL_RAMKI);
        wlasciwosci.disableProperty().bind(wariant.valueProperty().isNull());

        VBox formularz = new VBox(
                Ui.ODSTEP_SEKCJI,
                wiersz(new Label("Rodzaj algorytmu"), algorytm),
                wiersz(etykietaWariantu, wariant),
                wlasciwosci
        );
        formularz.setPadding(new Insets(Ui.MARGINES_OKNA));
        formularz.disableProperty().bind(symulacjaTrwa);

        Label komunikat = new Label();
        komunikat.setWrapText(true);
        komunikat.setAlignment(Pos.CENTER_LEFT);
        komunikat.setMaxWidth(Double.MAX_VALUE);
        komunikat.setStyle(Ui.STYL_KOMUNIKATU);

        komunikat.setPadding(new Insets(0, Ui.MARGINES_OKNA, 0, Ui.MARGINES_OKNA));
        komunikat.setMinHeight(Ui.WYSOKOSC_KOMUNIKATU);
        komunikat.setPrefHeight(Ui.WYSOKOSC_KOMUNIKATU);
        komunikat.setMaxHeight(Ui.WYSOKOSC_KOMUNIKATU);

        komunikat.visibleProperty().bind(komunikat.textProperty().isNotEmpty());
        komunikat.visibleProperty().bind(komunikat.textProperty().isNotEmpty());
        komunikat.managedProperty().bind(komunikat.visibleProperty());

        Button przyciskStart = new Button();
        przyciskStart.textProperty().bind(
                Bindings.when(symulacjaTrwa).then("Stop").otherwise("Start")
        );
        przyciskStart.setMaxWidth(Double.MAX_VALUE);
        przyciskStart.setMinHeight(Ui.WYSOKOSC_PRZYCISKU);
        przyciskStart.setPrefHeight(Ui.WYSOKOSC_PRZYCISKU);
        ustawStylPrzycisku(przyciskStart, symulacjaTrwa);

        przyciskStart.setOnAction(event -> {
            komunikat.setText("");

            if (symulacjaTrwa.get()) {
                stopSymulacja();
                symulacjaTrwa.set(false);
                return;
            }

            KonfiguracjaSymulacji konfiguracja;
            try {
                wymagajWyboru(algorytm, "Wybierz rodzaj algorytmu.");
                WariantAlgorytmu wybranyWariant = wymagajWyboru(
                        wariant, "Wybierz rodzaj sumy lub wielomian CRC."
                );

                int rozmiar = dodatniInt(rozmiarProbki, "Rozmiar próbki");
                int liczba = dodatniInt(liczbaProbek, "Liczba próbek");
                long ziarno = odczytajSeed(seed);

                RodzajBledu wybranyBlad = wymagajWyboru(
                        rodzajBledu, "Wybierz rodzaj błędu."
                );

                ParametryBledu blad = switch (wybranyBlad) {
                    case WIAZKOWY -> new BladWiazkowy(
                            dodatniInt(parametrBledu, "Długość wiązki")
                    );
                    case WIELOKROTNY -> new BladWielokrotny(
                            dodatniInt(parametrBledu, "Krotność przekłamania")
                    );
                    case LOSOWY_BCS -> new BladLosowyBcs(
                            odczytajStopeBledu(parametrBledu)
                    );
                };

                konfiguracja = new KonfiguracjaSymulacji(
                        wybranyWariant, rozmiar, liczba, ziarno, blad
                );
            } catch (BladFormularza e) {
                komunikat.setText(e.getMessage());
                e.pole.requestFocus();
                return;
            }

            symulacjaTrwa.set(true);
            uruchomSymulacje(konfiguracja, okno);
        });

        VBox dol = new VBox(komunikat, przyciskStart);
        dol.setFillWidth(true);

        BorderPane uklad = new BorderPane();
        uklad.setCenter(formularz);
        uklad.setBottom(dol);
        uklad.setStyle(Ui.STYL_OKNA);

        okno.setTitle("Konfiguracja wykrywania błędów");
        okno.setScene(new Scene(uklad, Ui.SZEROKOSC_OKNA, Ui.WYSOKOSC_OKNA));
        okno.setResizable(false);
        okno.show();
    }

    private static HBox wiersz(Label etykieta, Control pole) {
        etykieta.setMinWidth(Ui.SZEROKOSC_ETYKIETY);
        etykieta.setPrefWidth(Ui.SZEROKOSC_ETYKIETY);
        etykieta.setLabelFor(pole);
        etykieta.setStyle(Ui.STYL_ETYKIETY);

        pole.setMinWidth(0);
        pole.setMaxWidth(Double.MAX_VALUE);
        pole.setMinHeight(Ui.WYSOKOSC_POLA);
        pole.setPrefHeight(Ui.WYSOKOSC_POLA);
        HBox.setHgrow(pole, Priority.ALWAYS);
        ustawStylPola(pole);

        HBox wiersz = new HBox(Ui.ODSTEP_WIERSZY, etykieta, pole);
        wiersz.setAlignment(Pos.CENTER_LEFT);
        return wiersz;
    }

    private static void ustawStylPola(Control pole) {
        pole.styleProperty().bind(Bindings.createStringBinding(() -> {
            String tlo = Ui.TLO_POLA;
            String ramka = Ui.RAMKA;
            String tekst = Ui.TEKST;

            if (pole.isDisabled()) {
                tlo = Ui.TLO_POLA_WYLACZONEGO;
                ramka = Ui.RAMKA_WYLACZONA;
                tekst = Ui.TEKST_WYLACZONY;
            } else if (pole.isFocused()) {
                ramka = Ui.FOKUS;
            } else if (pole.isHover()) {
                tlo = Ui.TLO_POLA_NAJECHANEGO;
                ramka = Ui.RAMKA_NAJECHANA;
            }

            return Ui.STYL_POLA
                    + "-fx-background-color: " + tlo + ";"
                    + "-fx-border-color: " + ramka + ";"
                    + "-fx-text-fill: " + tekst + ";"
                    + "-fx-text-base-color: " + tekst + ";"
                    + "-fx-text-inner-color: " + tekst + ";";
        }, pole.disabledProperty(), pole.focusedProperty(), pole.hoverProperty()));
    }

    private static void ustawStylPrzycisku(Button przycisk, BooleanProperty symulacjaTrwa) {
        przycisk.styleProperty().bind(Bindings.createStringBinding(() -> {
                    boolean stop = symulacjaTrwa.get();
                    String tlo = stop ? Ui.STOP : Ui.START;

                    if (przycisk.isArmed()) {
                        tlo = stop ? Ui.STOP_WCISNIETY : Ui.START_WCISNIETY;
                    } else if (przycisk.isHover()) {
                        tlo = stop ? Ui.STOP_NAJECHANY : Ui.START_NAJECHANY;
                    }

                    String ramka = przycisk.isFocused() ? Ui.FOKUS_PRZYCISKU : tlo;
                    return Ui.STYL_PRZYCISKU
                            + "-fx-background-color: " + tlo + ";"
                            + "-fx-border-color: " + ramka + ";";
                }, symulacjaTrwa, przycisk.armedProperty(),
                przycisk.hoverProperty(), przycisk.focusedProperty()));
    }

    private static <T> T wymagajWyboru(ComboBox<T> lista, String komunikat) {
        T wartosc = lista.getValue();
        if (wartosc == null) {
            throw new BladFormularza(lista, komunikat);
        }
        return wartosc;
    }

    private static int dodatniInt(TextField pole, String nazwa) {
        String tekst = wymaganyTekst(pole, nazwa);
        int wartosc;

        try {
            wartosc = Integer.parseInt(tekst);
        } catch (NumberFormatException e) {
            throw new BladFormularza(
                    pole, "W polu „" + nazwa + "” wpisz liczbę całkowitą w zakresie int."
            );
        }

        if (wartosc <= 0) {
            throw new BladFormularza(
                    pole, "Wartość w polu „" + nazwa + "” musi być większa od zera."
            );
        }

        return wartosc;
    }

    private static long odczytajSeed(TextField pole) {
        String tekst = wymaganyTekst(pole, "Seed");

        try {
            return Long.parseLong(tekst);
        } catch (NumberFormatException e) {
            throw new BladFormularza(
                    pole, "Seed musi być liczbą całkowitą w zakresie long."
            );
        }
    }

    private static double odczytajStopeBledu(TextField pole) {
        String tekst = wymaganyTekst(pole, "Stopa błędu").replace(',', '.');
        double wartosc;

        try {
            wartosc = Double.parseDouble(tekst);
        } catch (NumberFormatException e) {
            throw new BladFormularza(pole, "Wpisz stopę błędu, np. 0,05.");
        }

        if (!Double.isFinite(wartosc) || wartosc < 0 || wartosc > 1) {
            throw new BladFormularza(
                    pole, "Stopa błędu musi należeć do zakresu od 0 do 1."
            );
        }

        return wartosc;
    }

    private static String wymaganyTekst(TextField pole, String nazwa) {
        String tekst = pole.getText().strip();
        if (tekst.isEmpty()) {
            throw new BladFormularza(pole, "Uzupełnij pole „" + nazwa + "”.");
        }
        return tekst;
    }

    private void uruchomSymulacje(KonfiguracjaSymulacji konfiguracja, Stage okno) {
        ParametrySymulacji parametry = new ParametrySymulacji(
                konfiguracja.liczbaProbek(),
                konfiguracja.rozmiarProbki(),
                konfiguracja.seed()
        );

        AlgorytmDetekcji algorytm = switch (konfiguracja.wariant()) {
            case KonfiguracjaSymulacji.WielomianCrc crc -> switch (crc) {
                case X1 -> FabrykaCRC.stworzCrc8Atm();
                case X2 -> FabrykaCRC.stworzCrc12();
                case X3 -> FabrykaCRC.stworzCrc16();
                case X4 -> FabrykaCRC.stworzCrc32();
                case X5 -> FabrykaCRC.stworzCrc16Ccitt();
            };

            case KonfiguracjaSymulacji.RodzajSumy suma -> switch (suma) {
                case BIT_PARZYSTOSCI -> new AlgorytmBitParzystosci();
                case INTERNETOWA -> new AlgorytmInternetCheckSum();
                case ADLERA8 -> new AlgorytmAdlerCheckSum(8);
                case ADLERA16 -> new AlgorytmAdlerCheckSum(16);
                case FLETCHERA8 -> new AlgorytmFletcherCheckSum(8);
                case FLETCHERA16 -> new AlgorytmFletcherCheckSum(16);
            };
        };

        ModelBledu modelBledu = switch (konfiguracja.blad()) {
            case KonfiguracjaSymulacji.BladWiazkowy blad ->
                    new pl.edu.pwr.niduc.channel.BladWiazkowy(blad.dlugoscWiazki());

            case KonfiguracjaSymulacji.BladWielokrotny blad ->
                    new pl.edu.pwr.niduc.channel.BladWielokrotnyK(blad.krotnoscPrzeklamania());

            case KonfiguracjaSymulacji.BladLosowyBcs blad ->
                    new pl.edu.pwr.niduc.channel.BladLosowyBSC(blad.stopaBledu());
        };

        List<AlgorytmDetekcji> algorytmy = List.of(algorytm);
        List<ModelBledu> modeleBledow = List.of(modelBledu);

        // Tutaj wstaw istniejące wywołanie silnika,
        // przekazując parametry, algorytmy i modeleBledow.

        Raport raport = new Raport("wyniki_symulacji.csv");
        SilnikSymulacji silnik = new SilnikSymulacji(parametry, algorytmy, modeleBledow);

        silnik.uruchom(raport);

        oknoWynikow = new OknoWynikow(okno, konfiguracja, raport.getDane());
        oknoWynikow.pokaz();

        raport.zapiszPodsumowanieDoCsv(parametry, "dane_do_wykresow.csv");
        raport.zamknij();
    }

    private void stopSymulacja()
    {
        if (oknoWynikow != null) {
            oknoWynikow.zamknij();
            oknoWynikow = null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static final class BladFormularza extends RuntimeException {
        private final Node pole;

        private BladFormularza(Node pole, String komunikat) {
            super(komunikat);
            this.pole = pole;
        }
    }

    public static final class Ui {

        // Wymiary i odstępy
        public static final double SZEROKOSC_OKNA = 560;
        public static final double WYSOKOSC_OKNA = 540;

        public static final double MARGINES_OKNA = 24;
        public static final double MARGINES_RAMKI = 16;
        public static final double ODSTEP_SEKCJI = 12;
        public static final double ODSTEP_WIERSZY = 8;

        public static final double SZEROKOSC_ETYKIETY = 176;
        public static final double WYSOKOSC_POLA = 36;
        public static final double WYSOKOSC_KOMUNIKATU = 52;
        public static final double WYSOKOSC_PRZYCISKU = 52;

        // Tła
        public static final String KOLOR_GLOWNY = "#9A352D";
        public static final String TLO_OKNA = "#F6F1EC";
        public static final String TLO_RAMKI = "#FFFDF9";
        public static final String TLO_POLA = "#FFFFFF";
        public static final String TLO_POLA_NAJECHANEGO = "#FDF4EF";
        public static final String TLO_POLA_WYLACZONEGO = "#EFE8E2";

        // Tekst
        public static final String TEKST = "#302421";
        public static final String TEKST_WYLACZONY = "#756762";
        public static final String TEKST_PRZYCISKU = "#FFFFFF";
        public static final String BLAD = "#8B2922";

        // Obramowania i fokus
        public static final String RAMKA = "#D9CBC3";
        public static final String RAMKA_NAJECHANA = "#B7877F";
        public static final String RAMKA_WYLACZONA = "#E0D4CD";
        public static final String FOKUS = KOLOR_GLOWNY;
        public static final String FOKUS_PRZYCISKU = "#F5C7B8";

        // Stany przycisku Start
        public static final String START = KOLOR_GLOWNY;
        public static final String START_NAJECHANY = "#862D26";
        public static final String START_WCISNIETY = "#71241E";

        // Stany przycisku Stop
        public static final String STOP = "#542D32";
        public static final String STOP_NAJECHANY = "#45242A";
        public static final String STOP_WCISNIETY = "#351A1F";



        // Style JavaFX
        public static final String STYL_OKNA =
                "-fx-background-color: " + TLO_OKNA + ";"
                        + "-fx-font-size: 13px;";

        public static final String STYL_RAMKI =
                "-fx-background-color: " + TLO_RAMKI + ";"
                        + "-fx-border-color: " + RAMKA + ";"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-radius: 10;"
                        + "-fx-opacity: 1;";

        public static final String STYL_ETYKIETY =
                "-fx-text-fill: " + TEKST + ";";

        public static final String STYL_NAGLOWKA =
                "-fx-text-fill: " + KOLOR_GLOWNY + ";"
                        + "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;";

        public static final String STYL_KOMUNIKATU =
                "-fx-text-fill: " + BLAD + ";";

        public static final String STYL_POLA =
                "-fx-background-insets: 0;"
                        + "-fx-background-radius: 6;"
                        + "-fx-border-radius: 6;"
                        + "-fx-border-width: 1;"
                        + "-fx-opacity: 1;"
                        + "-fx-prompt-text-fill: " + TEKST_WYLACZONY + ";";

        public static final String STYL_PRZYCISKU =
                "-fx-background-insets: 0;"
                        + "-fx-background-radius: 0;"
                        + "-fx-border-width: 2;"
                        + "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-cursor: hand;"
                        + "-fx-text-fill: " + TEKST_PRZYCISKU + ";";
    }
}