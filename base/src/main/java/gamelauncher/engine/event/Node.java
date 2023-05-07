package gamelauncher.engine.event;

/**
 * @author DasBabyPixel
 */
public interface Node {

    /**
     * @return the priority of this node
     */
    int priority();

    /**
     * @param event
     */
    void invoke(Event event);

}
