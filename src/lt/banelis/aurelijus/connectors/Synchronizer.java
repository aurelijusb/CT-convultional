package lt.banelis.aurelijus.connectors;

import java.util.ArrayList;
import java.util.Collection;
import lt.banelis.aurelijus.data.AbstractDataStructure;

/**
 * Class, that stores synchronization data to compare data structures.
 * 
 * @author Aurelijus Banelis
 */
public class Synchronizer {
    private int synchronisation;
    private int received;
    public static double progress = 0;
    private static Runnable progressUpdater = null;
    
    public Synchronizer(int synchronisation) {
        this.synchronisation = synchronisation;
    }
    
    
    /*
     * Positions
     */

    public void setSynchronisation(int synchronisation) {
        this.synchronisation = synchronisation;
    }
   
    public int getSynchronisation() {
        return synchronisation;
    }

    public void increaseReceived(int difference) {
        received += difference;
    }

    public void reset() {
        received = 0;
    }
    
    
    /*
     * Devices
     */
    
    /**
     * Initiates synchronization in data structures.
     */
    public void chainDevices(AbstractDataStructure source,
                             AbstractDataStructure destination) {
        source.setSyncronizer(this, destination, false);
        destination.setSyncronizer(this, source, true);
    }
    
    /**
     * Removes state machines initial registers' data from whole stream.
     */
    public Collection<Boolean> synchronize(Collection<Boolean> data) {
        int toSynchronise = synchronisation - received;
        received += data.size();
        if (toSynchronise < 0) {
            /* No syncronisation needed */
            return data;
        } else if (data.size() <= toSynchronise) {
            /* No usefull data received */
            data.clear();
            return data;
        } else {
            /* Part of the data is usefull */
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
     * Generates stream of useless data used for synchronization.
     * 
     * This data should be encoded and passed through channel, so decoder could
     * finish retrieving all useful data from it's registers.
     */
    public Collection<Boolean> dataToSynchronize() {
        ArrayList<Boolean> result = new ArrayList<Boolean>(synchronisation);
        for (int i = 0; i < synchronisation; i++) {
            result.add(Boolean.FALSE);
        }
        return result;
    }
    
    
    /*
     * Progress visalization
     */

    public static void setProgressUpdater(Runnable progressUpdater) {
        Synchronizer.progressUpdater = progressUpdater;
    }
    
    public static void updateProgress() {
        if (progressUpdater != null) {
            progressUpdater.run();
        }
    }
}
