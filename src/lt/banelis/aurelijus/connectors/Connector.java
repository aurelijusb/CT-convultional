package lt.banelis.aurelijus.connectors;

import java.util.Collection;

/**
 * Sąsaja skirta sugrupuoti duomenų transformacijas atliekančias klases.
 * 
 * Kadangi gali būti naudojami įvairūs duomenų formatai, reikalinga vieninga
 * sąsaja tų duomenų persiuntimui ir kodavimui.
 * 
 * @author Aurelijus Banelis
 */
public interface Connector  {
    /**
     * Dvejetainių duomenų transformacija.
     * 
     * Pavyzdžiui kodavimas, dekodavimas ar iškraipymas.
     * 
     * @param data  bitų seka, kurią reikia pakeisti.
     * @return      pakeista bitų seka.
     */
    public Collection<Boolean> transform(Collection<Boolean> data);
}
