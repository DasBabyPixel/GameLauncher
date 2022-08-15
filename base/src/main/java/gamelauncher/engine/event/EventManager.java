package gamelauncher.engine.event;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.util.Arrays;

/**
 * @author DasBabyPixel
 *
 */
public class EventManager {

	private final Map<Object, Listener> listeners = new ConcurrentHashMap<>();
	private final Collection<Node> sorted = ConcurrentHashMap.newKeySet();

	/**
	 */
	public EventManager() {
	}

	/**
	 * Posts the event for this {@link EventManager}
	 * @param <T>
	 * @param event
	 * @return the event
	 */
	public <T extends Event> T post(T event) {
		for (Node node : sorted) {
			node.invoke(event);
		}
		return event;
	}

	/**
	 * @param listener
	 */
	public void registerListener(Object listener) {
		Listener l = new Listener(listener);
		listeners.put(listener, l);
		sortMethodNodes();
	}

	/**
	 * @param listener
	 */
	public void unregisterListener(Object listener) {
		listeners.remove(listener);
		sortMethodNodes();
	}

	private synchronized void sortMethodNodes() {
		List<Node> nodes = new ArrayList<>();
		for (Listener l : listeners.values()) {
			for (Node m : l.nodes) {
				nodes.add(m);
			}
		}
		Collections.sort(nodes, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return -Integer.valueOf(o1.priority()).compareTo(Integer.valueOf(o2.priority()));
			}
		});
		sorted.clear();
		sorted.addAll(nodes);
	}

	private static class Listener {
		private final Node[] nodes;

		public Listener(Object listener) {
			this.nodes = findNodes(listener);
		}

		private static Node[] findNodes(Object listener) {
			List<Node> nodes = new ArrayList<>();
			Class<?> clazz = listener.getClass();
			Collection<Method> methods = new HashSet<>();

			while (clazz != Object.class && clazz != null) {
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
					System.out.println("Invalid EventHandler: " + clazz.getName() + "#" + method.getName()
							+ " - More than 1 Parameters!");
					continue;
				}
				Parameter param = method.getParameters()[0];
				if (!Event.class.isAssignableFrom(param.getType())) {
					System.out.println("Invalid EventHandler: " + clazz.getName() + "#" + method.getName() + " - Param "
							+ param.getType().getName() + " is not of type Event!");
					continue;
				}
				nodes.add(new MethodNode(listener, method, param.getType(), handler.priority()));
			}
			if (listener instanceof Node) {
				Node node = (Node) listener;
				nodes.add(node);
			}
			return nodes.toArray(new Node[nodes.size()]);
		}
	}

	private static class MethodNode implements Node {

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
			if (param.isInstance(event)) {
				try {
					method.invoke(owner, event);
				} catch (Throwable ex) {
					EventException e = new EventException("Exception in Event: " + ex.getLocalizedMessage(), ex);
					e.printStackTrace();
				}
			}
		}
	}
}
