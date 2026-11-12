package pl.edu.pwr.niduc.algorithms;

/**
 * Tlumaczy zapis wielomianowy na tablice bitow
 * np dla X^8 + X^2 + X + 1
 */
public final class FabrykaCRC {

    private FabrykaCRC() { }

    private static boolean[] wielomianZPoteg(int stopien, int... potegi) {
        boolean[] w = new boolean[stopien + 1];
        for (int potega : potegi) {
            // Potega X^k trafia na indeks (stopien - k)
            w[stopien - potega] = true;
        }
        return w;
    }

    public static AlgorytmCRC stworzCrc8Atm() {
        // X^8 + X^2 + X + 1
        return new AlgorytmCRC("CRC-8-ATM", wielomianZPoteg(8, 8, 2, 1, 0));
    }

    public static AlgorytmCRC stworzCrc10() {
        // X^10 + X^9 + X^5 + X^4 + X + 1
        return new AlgorytmCRC("CRC-10", wielomianZPoteg(10, 10, 9, 5, 4, 1, 0));
    }

    public static AlgorytmCRC stworzCrc12() {
        // X^12 + X^11 + X^3 + X^2 + X + 1
        return new AlgorytmCRC("CRC-12", wielomianZPoteg(12, 12, 11, 3, 2, 1, 0));
    }

    public static AlgorytmCRC stworzCrc16() {
        // X^16 + X^15 + X^2 + 1
        return new AlgorytmCRC("CRC-16", wielomianZPoteg(16, 16, 15, 2, 0));
    }

    public static AlgorytmCRC stworzCrc16Ccitt() {
        // X^16 + X^12 + X^5 + 1
        return new AlgorytmCRC("CRC-16-CCITT", wielomianZPoteg(16, 16, 12, 5, 0));
    }

    public static AlgorytmCRC stworzCrc32() {
        // X^32 + X^26 + X^23 + X^22 + X^16 + X^12 + X^11 + X^10 + X^8 + X^7 + X^5 + X^4 + X^2 + X + 1
        return new AlgorytmCRC("CRC-32 IEEE 802.3", wielomianZPoteg(32, 32, 26, 23, 22, 16, 12, 11, 10, 8, 7, 5, 4, 2, 1, 0));
    }
}
