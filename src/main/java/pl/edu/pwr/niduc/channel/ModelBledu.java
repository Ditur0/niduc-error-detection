package pl.edu.pwr.niduc.channel;

import pl.edu.pwr.niduc.core.Blad;
import pl.edu.pwr.niduc.core.Wynik;

/**
 * Wzorzec projektowy -> strategia
 * Kazdy model bledu przyjuje zakodowana ramke i ziarno zeby rozne algorytmy byly testowane w identycznych warunkach
 */
public interface ModelBledu {
    Blad zaszum(Wynik oryginalnyWynik, long seed);
    String getNazwa();
}
