package lt.banelis.aurelijus.data;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Aurelijus Banelis
 */
public class Hightlighter {
    private int synchronisation;
    private AbstractDataStructure source;
    private AbstractDataStructure destination;

    public Hightlighter(AbstractDataStructure source,
                        AbstractDataStructure destination) {
        this(source, destination, 0);
    }

    public Hightlighter(AbstractDataStructure source,
                        AbstractDataStructure destination,
                        int synchronisation) {
        this.synchronisation = synchronisation;
        this.source = source;
        this.destination = destination;
    }
    
    

    public void setSynchronisation(int synchronisation) {
        this.synchronisation = synchronisation;
    }
   
    public int getSynchronisation(AbstractDataStructure requester) {
        if (requester == destination) {
            return synchronisation;
        } else {
            return 0;
        }
    }
    
    public boolean isEqual(int offset) {
        Collection<Boolean> sourceHistory = source.viewHistory();
        Collection<Boolean> destinationeHistory = destination.viewHistory();
        return get(sourceHistory, offset) ==
           get(destinationeHistory, offset);
    }
    
    private Boolean get(Collection<Boolean> container, int offset) {
        if (container.size() <= offset || offset < 0) {
            return null;
        } else if (container instanceof List) {
            return ((List<Boolean>) container).get(offset);
        } else {
            int i = 0;
            for (Boolean bit : container) {
                if (i == offset) {
                    return bit;
                } else if (i > offset) {
                    return null;
                }
                i++;
            }
        }
        return null;
    }

    public boolean isDestination(AbstractDataStructure me) {
        return me == destination;
    }
}
