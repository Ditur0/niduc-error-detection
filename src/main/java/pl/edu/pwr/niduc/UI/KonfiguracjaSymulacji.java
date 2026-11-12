package pl.edu.pwr.niduc.UI;

public record KonfiguracjaSymulacji(WariantAlgorytmu wariant, int rozmiarProbki, int liczbaProbek, long seed, ParametryBledu blad)
{
    public RodzajAlgorytmu rodzajAlgorytmu()
    {
        return wariant.rodzajAlgorytmu();
    }

    public enum RodzajAlgorytmu
    {
        CRC,
        SUMA_KONTROLNA;

        @Override
        public String toString()
        {
            return this == CRC ? "CRC" : "Suma kontrolna";
        }
    }

    public sealed interface WariantAlgorytmu permits RodzajSumy, WielomianCrc
    {
        RodzajAlgorytmu rodzajAlgorytmu();
    }

    public enum RodzajSumy implements WariantAlgorytmu
    {
        BIT_PARZYSTOSCI("Bit parzystości"),
        INTERNETOWA("Internetowa suma kontrolna"),
        ADLERA8("Suma Adlera 16"),
        FLETCHERA8("Suma Fletchera 16"),
        ADLERA16("Suma Adlera 32"),
        FLETCHERA16("Suma Fletchera 32");

        private final String etykieta;

        RodzajSumy(String etykieta) {
            this.etykieta = etykieta;
        }

        @Override
        public RodzajAlgorytmu rodzajAlgorytmu() {
            return RodzajAlgorytmu.SUMA_KONTROLNA;
        }

        @Override
        public String toString() {
            return etykieta;
        }
    }

    public enum WielomianCrc implements WariantAlgorytmu
    {
        X1("CRC 8 ATM"),
        X2("CRC 12"),
        X3("CRC 16"),
        X4("CRC 32"),
        X5("CRC 16 CCITT");

        private final String etykieta;

        WielomianCrc(String etykieta)
        {
            this.etykieta = etykieta;
        }

        @Override
        public RodzajAlgorytmu rodzajAlgorytmu() {
            return RodzajAlgorytmu.CRC;
        }

        @Override
        public String toString() {
            return etykieta;
        }
    }

    public enum RodzajBledu
    {
        WIAZKOWY("Wiązkowy"),
        WIELOKROTNY("Wielokrotny"),
        LOSOWY_BCS("LosowyBCS");

        private final String etykieta;

        RodzajBledu(String etykieta) {
            this.etykieta = etykieta;
        }

        @Override
        public String toString() {
            return etykieta;
        }
    }

    public sealed interface ParametryBledu
            permits BladWiazkowy, BladWielokrotny, BladLosowyBcs {}

    public record BladWiazkowy(int dlugoscWiazki)
            implements ParametryBledu {}

    public record BladWielokrotny(int krotnoscPrzeklamania)
            implements ParametryBledu {}

    public record BladLosowyBcs(double stopaBledu)
            implements ParametryBledu {}
}