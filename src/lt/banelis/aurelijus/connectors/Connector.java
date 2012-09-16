package lt.banelis.aurelijus.connectors;

import java.util.Collection;

/**
 *
 * @author Aurelijus Banelis
 */
public interface Connector  {
    public Collection<Boolean> transform(Collection<Boolean> data);
}
