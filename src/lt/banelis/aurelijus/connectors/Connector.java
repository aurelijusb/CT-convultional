package lt.banelis.aurelijus.connectors;

import java.util.Collection;

/**
 * Common functions for binary transformation classes.
 * 
 * @author Aurelijus Banelis
 */
public interface Connector  {
    public Collection<Boolean> transform(Collection<Boolean> data);
}
