package lt.banelis.aurelijus.connectors;

import java.util.ArrayList;
import java.util.Collection;
import lt.banelis.aurelijus.data.AbstractDataStructure;

/**
 * Klasė skirta pradinių duomenų ir rezultatų palyginimui bei programos eigos
 * sekimui.
 * 
 * @author Aurelijus Banelis
 */
public class Synchronizer {
    private int synchronisation;
    private int received;
    public static double progress = 0;
    private static Runnable progressUpdater = null;
    
    /**
     * Sukuriamas palyginimui skirtas objektas.
     * 
     * @param synchronisation   per kiek bitų vėluoja rezultatas nuo pradinių
     *                          duomenų
     */
    public Synchronizer(int synchronisation) {
        this.synchronisation = synchronisation;
    }
    
    
    /*
     * Funkcijos skirtos pozicijos nustatymui
     */

    /**
     * Nustatyti vėlavimą
     * 
     * @param synchronisation   per kiek bitų vėluoja rezultatas nuo pradinių
     *                          duomenų
     */
    public void setSynchronisation(int synchronisation) {
        this.synchronisation = synchronisation;
    }
   
    /**
     * Vėlavimo įvertinimas.
     * 
     * @return  per kiek bitų vėluoja rezultatas nuo pradinių duomenų
     */
    public int getSynchronisation() {
        return synchronisation;
    }

    /**
     * Padidinamas priimtų bitų skaitliukas.
     * 
     * Skaitliukas naudojamas nenaudingų (busenos mašinos pradinių reikšmių)
     * bitų ignoravimui.
     * 
     * @param difference    kiek bitų priimta.
     */
    public void increaseReceived(int difference) {
        received += difference;
    }

    /**
     * Atstatomas priimtų bitų skaitliukas.
     * 
     * Skaitliukas naudojamas nenaudingų (busenos mašinos pradinių reikšmių)
     * bitų ignoravimui.
     */
    public void reset() {
        received = 0;
    }
    
    
    /*
     * Funkcijos skirtos darbui su siuntėju ir gavėju
     */
    
    /**
     * Susiejamas siuntėjas, gavėjas ir ši klasė
     * 
     * @param source        siuntėjo objetas.
     * @param destination   gavėjo objektas.
     */
    public void chainDevices(AbstractDataStructure source,
                             AbstractDataStructure destination) {
        source.setSyncronizer(this, destination, false);
        destination.setSyncronizer(this, source, true);
    }
    
    /**
     * Ištrinami nenaudinti duomenų srauto bitai.
     * 
     * Nenaudingi bitai atsiranda dėl to, kad pradžioje užkoduojamos koduotojo
     * pradinės registrų reikšmės, o tik vėliau praeina ir naudinga koduojama
     * informacija.
     * 
     * @param data  jau dekoduotų duomenų srautas.
     * @return      visas arba dalis pradinio srauto, panaikinus neinformatyvius
     *              bitus.
     */
    public Collection<Boolean> synchronize(Collection<Boolean> data) {
        int toSynchronise = synchronisation - received;
        received += data.size();
        if (toSynchronise < 0) {
            /* Sulyginimo nereikia */
            return data;
        } else if (data.size() <= toSynchronise) {
            /* Visi gauti bitai yra neinformatyvūs */
            data.clear();
            return data;
        } else {
            /* Dalis gautų bitų yra informatyvūs, dalis - ne */
            int usefullSize = data.size() - toSynchronise;
            ArrayList<Boolean> newData = new ArrayList<Boolean>(usefullSize);
            int i = 0;
            for (Boolean bit : data) {
                if (i >= toSynchronise) {
                    newData.add(bit);
                }
                i++;
            }
            return newData;
        }
    }
    
    /**
     * Sugeneruojama bitų seka, skirta užbaigti paskutinį pranešimą.
     * 
     * Kadangi dekoaviams turi savo atmintį (vėluoja), reikalingi papildomi
     * bitai, kad iš dekodatoriaus išeitų paskutinis naudingas bitas.
     * 
     * @return bitų seka, skirta užbaigti paskutinį pranešimą
     */
    public Collection<Boolean> dataToSynchronize() {
        ArrayList<Boolean> result = new ArrayList<Boolean>(synchronisation);
        for (int i = 0; i < synchronisation; i++) {
            result.add(Boolean.FALSE);
        }
        return result;
    }
    
    
    /*
     * Funkcijos skirtos einamosios būsenos vaizdavimui
     */

    /**
     * Priskiriama funkcija, skirta informavimui apie pasikeitusią būseną/eigą.
     * 
     * @param progressUpdater  klasė su metodu, skirtu vizualizacijai.
     */
    public static void setProgressUpdater(Runnable progressUpdater) {
        Synchronizer.progressUpdater = progressUpdater;
    }
    
    /**
     * Išviečiama būsenos/eigos vaizdavimui skirta funkcija.
     */
    public static void updateProgress() {
        if (progressUpdater != null) {
            progressUpdater.run();
        }
    }
}
