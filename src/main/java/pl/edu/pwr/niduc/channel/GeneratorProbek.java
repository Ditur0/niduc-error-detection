package pl.edu.pwr.niduc.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneratorProbek {

    private final Random random;

    public GeneratorProbek(long seed) {
        this.random = new Random(seed);
    }

    /**
     * @param k - ilosc probek
     * @param n - dlugosc probki
     */
    // Generuje liste probek, kazda probka to liste [true, false, false, ..., n]
    public List<boolean[]> generujProbki(int k, int n) {
        List<boolean[]> listaProbek = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            boolean[] probka = new boolean[n];
            for (int j = 0; j < n; j++) {
                probka[j] = random.nextBoolean();
            }
            listaProbek.add(probka);
        }
        return listaProbek;
    }
}
