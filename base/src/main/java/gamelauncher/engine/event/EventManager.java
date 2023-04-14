package gamelauncher.engine.event;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.Arrays;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.profiler.Profiler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author DasBabyPixel
 */
public class EventManager {

    private static final Logger logger = Logger.logger();

    private final Map<Object, Listener> listeners = new ConcurrentHashMap<>();

    private final Collection<Node> sorted = new CopyOnWriteArrayList<>();

    private final Profiler profiler;

    public EventManager(GameLauncher launcher) {
        this.profiler = launcher.profiler();
    }

    /**
     * Posts the event for this {@link EventManager}
     *
     * @param <T>   the type of event
     * @param event the event
     * @return the event
     */
    public <T extends Event> T post(T event) {
        profiler.begin("event", "post");
        for (Node node : sorted) {
            node.invoke(event);
        }
        profiler.end();
        return event;
    }

    /**
     * @param listener the listener to register
     */
    public void registerListener(Object listener) {
        Listener l = new Listener(listener);
        listeners.put(listener, l);
        sortMethodNodes();
    }

    /**
     * @param listener the listener to unregister
     */
    public void unregisterListener(Object listener) {
        listeners.remove(listener);
        sortMethodNodes();
    }

    private synchronized void sortMethodNodes() {
        List<Node> nodes = new ArrayList<>();
        for (Listener l : listeners.values()) {
            Collections.addAll(nodes, l.nodes);
        }
        nodes.sort((o1, o2) -> -Integer.compare(o1.priority(), o2.priority()));
        sorted.clear();
        sorted.addAll(nodes);
    }

    private class Listener {

        private final Node[] nodes;

        public Listener(Object listener) {
            this.nodes = findNodes(listener);
        }

        private Node[] findNodes(Object listener) {
            List<Node> nodes = new ArrayList<>();
            Class<?> clazz = listener.getClass();
            Collection<Method> methods = new HashSet<>();

            while (clazz != Object.class && clazz.getSuperclass() != null) {
                methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
                Class<?> c = clazz;
                clazz = clazz.getSuperclass();
                if (c == clazz) {
                    break;
                }
            }

            for (Method method : methods) {
                EventHandler handler = method.getAnnotation(EventHandler.class);
                if (handler == null)
                    continue;
                if (method.getParameterCount() != 1) {
                    logger.error("Invalid EventHandler: " + clazz.getName() + "#" + method.getName()
                            + " - More than 1 Parameters!");
                    continue;
                }
                Parameter param = method.getParameters()[0];
                if (!Event.class.isAssignableFrom(param.getType())) {
                    logger.error("Invalid EventHandler: " + clazz.getName() + "#" + method.getName()
                            + " - Param " + param.getType().getName() + " is not of type Event!");
                    continue;
                }
                nodes.add(new MethodNode(listener, method, param.getType(), handler.priority()));
            }
            if (listener instanceof Node) {
                Node node = (Node) listener;
                nodes.add(node);
            }
            return nodes.toArray(new Node[0]);
        }

    }

    private class MethodNode implements Node {

        private final Object owner;

        private final Method method;

        private final Class<?> param;

        private final int priority;

        public MethodNode(Object owner, Method method, Class<?> param, int priority) {
            this.owner = owner;
            this.method = method;
            this.param = param;
            this.priority = priority;
            this.method.setAccessible(true);
        }

        @Override
        public int priority() {
            return priority;
        }

        @Override
        public void invoke(Event event) {
            profiler.begin("event", event.getClass().getSimpleName());
            if (param.isInstance(event)) {
                try {
                    method.invoke(owner, event);
                } catch (Throwable ex) {
                    EventException e =
                            new EventException("Exception in Event: " + ex.getLocalizedMessage(),
                                    ex);
                    logger.error(e);
                }
            }
            profiler.end();
        }

    }

}
