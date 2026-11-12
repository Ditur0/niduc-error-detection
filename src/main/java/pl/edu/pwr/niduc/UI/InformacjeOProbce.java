package pl.edu.pwr.niduc.UI;

import pl.edu.pwr.niduc.core.Blad;
import pl.edu.pwr.niduc.core.Wynik;

public class InformacjeOProbce
{
    public int id;
    public Wynik probka_bez_bledu;
    public Blad probka_z_bledem;
    public int ilosc_bledow;
    public boolean wykryto_blad;
    public boolean kolizja;

    public InformacjeOProbce(int id, Wynik wynik, Blad blad, int ilosc_bledow, boolean wykryto, boolean kolizja)
    {
        this.id = id;
        this.probka_bez_bledu = wynik;
        this.probka_z_bledem = blad;
        this.ilosc_bledow = ilosc_bledow;
        this.wykryto_blad = wykryto;
        this.kolizja = kolizja;


    }


}
