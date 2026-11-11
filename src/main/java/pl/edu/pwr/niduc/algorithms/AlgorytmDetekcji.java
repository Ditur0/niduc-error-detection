package pl.edu.pwr.niduc.algorithms;

import pl.edu.pwr.niduc.core.Wynik;

/**
 * Wspolny interfejs dla kazdego algorytmu
 */
public interface AlgorytmDetekcji {
    Wynik koduj(boolean[] probka);
    boolean sprawdz(Wynik wynikDoSprawdzenia);
    String getNazwa();
}
