package gamelauncher.engine.util.collection;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @param <E>
 *
 * @author DasBabyPixel
 */
public class AtomicConcurrentLinkedDeque<E> implements Deque<E> {
	private final AtomicInteger size = new AtomicInteger();
	private final AtomicReference<Node<E>> head = new AtomicReference<>(null);
	private final AtomicReference<Node<E>> tail = new AtomicReference<>(null);

	@Override
	public void addFirst(E e) {
		offerFirst(e);
	}

	@Override
	public void addLast(E e) {
		offerLast(e);
	}

	@Override
	public boolean offerFirst(E e) {
		size.incrementAndGet();
		linkFirst(new Node<>(e));
		return true;
	}

	@Override
	public boolean offerLast(E e) {
		size.incrementAndGet();
		linkLast(new Node<>(e));
		return true;
	}

	@Override
	public E removeFirst() {
		E e = pollFirst();
		if (e == null)
			throw new NoSuchElementException();
		return e;
	}

	@Override
	public E removeLast() {
		E e = pollLast();
		if (e == null)
			throw new NoSuchElementException();
		return e;
	}

	@Override
	public E pollFirst() {
		while (true) {
			Node<E> node = head.get();
			if (head == null)
				return null;
			if (unlink(node)) {
				size.decrementAndGet();
				return node.item;
			}
		}
	}

	@Override
	public E pollLast() {
		while (true) {
			Node<E> node = tail.get();
			if (node == null)
				return null;
			if (unlink(node)) {
				size.decrementAndGet();
				return node.item;
			}
		}
	}

	@Override
	public E getFirst() {
		E e = peekFirst();
		if (e == null)
			throw new NoSuchElementException();
		return e;
	}

	@Override
	public E getLast() {
		E e = peekLast();
		if (e == null)
			throw new NoSuchElementException();
		return e;
	}

	@Override
	public E peekFirst() {
		Node<E> node = head.get();
		if (node == null)
			return null;
		return node.item;
	}

	@Override
	public E peekLast() {
		Node<E> node = tail.get();
		if (node == null)
			return null;
		return node.item;
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		Iterator<E> it = iterator();
		while (it.hasNext()) {
			E e = it.next();
			if (Objects.equals(e, o)) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		Iterator<E> it = descendingIterator();
		while (it.hasNext()) {
			E e = it.next();
			if (Objects.equals(e, o)) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean add(E e) {
		addLast(e);
		return true;
	}

	@Override
	public boolean offer(E e) {
		return offerLast(e);
	}

	@Override
	public E remove() {
		return removeFirst();
	}

	@Override
	public E poll() {
		return pollFirst();
	}

	@Override
	public E element() {
		return getFirst();
	}

	@Override
	public E peek() {
		return peekFirst();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		Iterator<? extends E> it = c.iterator();
		Node<E> head = null;
		Node<E> tail = null;
		int inc = 0;
		while (it.hasNext()) {
			inc++;
			Node<E> node = new Node<>(it.next());
			if (tail == null) {
				tail = head = node;
			} else {
				tail.next.set(node);
				node.prev.set(tail);
				tail = node;
			}
		}
		if (head == null) {
			return true;
		}
		size.addAndGet(inc);
		linkLast(head, tail);
		return true;
	}

	@Override
	public void push(E e) {
		addFirst(e);
	}

	@Override
	public E pop() {
		return removeFirst();
	}

	@Override
	public boolean remove(Object o) {
		return removeFirstOccurrence(o);
	}

	@Override
	public boolean contains(Object o) {
		for (E e : this) {
			if (Objects.equals(o, e)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int size() {
		return size.get();
	}

	@Override
	public Iterator<E> iterator() {
		return new Itr();
	}

	@Override
	public Iterator<E> descendingIterator() {
		return new DescendingItr();
	}

	@Override
	public boolean isEmpty() {
		return size.get() == 0;
	}

	@Override
	public Object[] toArray() {
		// Use ArrayList to deal with resizing.
		ArrayList<E> al = new ArrayList<E>();
		for (Node<E> p = first(); p != null; p = p.next.get()) {
			E item = p.item;
			if (item != null)
				al.add(item);
		}
		return al.toArray();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T @NotNull [] a) {
		// try to use sent-in array
		int k = 0;
		Node<E> p;
		for (p = first(); p != null && k < a.length; p = p.next.get()) {
			E item = p.item;
			if (item != null)
				a[k++] = (T) item;
		}
		if (p == null) {
			if (k < a.length)
				a[k] = null;
			return a;
		}

		// If doesn't fit, use ArrayList version
		ArrayList<E> al = new ArrayList<E>();
		for (Node<E> q = first(); q != null; q = q.next.get()) {
			E item = q.item;
			if (item != null)
				al.add(item);
		}
		return al.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			remove(o);
		}
		return changed;
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		boolean changed = false;
		Iterator<?> it = this.iterator();
		while (it.hasNext()) {
			if (!c.contains(it.next())) {
				it.remove();
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public void clear() {
		while (pollFirst() != null)
			;
	}

	private Node<E> first() {
		return head.get();
	}

	private Node<E> last() {
		return tail.get();
	}

	private void linkFirst(Node<E> newHead) {
		linkFirst(newHead, newHead);
	}

	private void linkFirst(Node<E> newHead, Node<E> linkTo) {
		while (true) {
			if (head.compareAndSet(null, newHead)) {
				tail.set(linkTo);
				return;
			}
			Node<E> h = first();
			if (h == null) {
				continue;
			}
			linkTo.next.set(h);
			if (h.prev.compareAndSet(null, linkTo)) {
				if (head.compareAndSet(h, newHead)) {
					return;
				}
				// head was updated
			}
			linkTo.prev.set(null);
		}
	}

	private void linkLast(Node<E> newTail) {
		linkLast(newTail, newTail);
	}

	private void linkLast(Node<E> linkTo, Node<E> newTail) {
		while (true) {
			if (head.compareAndSet(null, linkTo)) {
				tail.set(newTail);
				return;
			}

			Node<E> t = last();
			if (t == null) {
				continue;
			}
			linkTo.prev.set(t);
			if (t.next.compareAndSet(null, linkTo)) {
				if (tail.compareAndSet(t, newTail)) {
					return;
				}
				// tail was updated
			}
			linkTo.prev.set(null);
		}
	}

	private boolean unlink(Node<E> node) {
		begin:
		while (true) {
			Node<E> prev = node.prev.get();
			Node<E> next = node.next.get();
			if (prev == null && next == null) {
				while (true) {
					if (head.compareAndSet(node, null)) {
						if (tail.compareAndSet(node, null)) {
							return true;
						}
						throw new IllegalStateException("Weird nodes");
					}
					continue begin;
				}
			} else if (prev == null) {
				while (true) {
					if (head.compareAndSet(node, next)) {
						next.prev.set(null);
						node.prev.set(null);
						node.next.set(null);
						break;
					}
					prev = node.prev.get();
					if (prev == null) {
						if (node.next.get() == null) {
							// Node is already unlinked
							return false;
						}
						continue;
					}
					unlink(node);
				}
			} else if (next == null) {
				while (true) {
					if (tail.compareAndSet(node, prev)) {
						prev.next.set(null);
						node.prev.set(null);
						node.next.set(null);
						break;
					}
					next = node.next.get();
					if (next == null) {
						if (node.prev.get() == null) {
							// Node is already unlinked
							return false;
						}
						continue;
					}
				}
			} else {
				while (true) {
					if (prev.next.compareAndSet(node, next)) {
						if (!next.prev.compareAndSet(node, prev)) {
							if (next.prev.get() == prev) {
								// Somebody else did it for us, problematic
								throw new IllegalStateException("Broken nodes");
								//								return false;
							}
							// Somebody else updated it, reset values
							if (!prev.next.compareAndSet(next, node)) {
								throw new IllegalStateException(
										"Theoretically impossible, weird nodes");
							}
							continue begin;
						}
						node.prev.set(null);
						node.next.set(null);
						break;
					}
				}
			}
			break;
		}
		return true;
	}

	private static class Node<V> {

		private final AtomicReference<Node<V>> prev;

		private final AtomicReference<Node<V>> next;

		private final V item;

		public Node(V item) {
			this.item = item;
			this.prev = new AtomicReference<>();
			this.next = new AtomicReference<>();
		}

	}


	private class DescendingItr extends AbstractItr {

		@Override
		protected Node<E> beginNode() {
			return last();
		}

		@Override
		protected Node<E> nextNode(Node<E> node) {
			return node.prev.get();
		}

	}


	private class Itr extends AbstractItr {

		@Override
		protected Node<E> beginNode() {
			return first();
		}

		@Override
		protected Node<E> nextNode(Node<E> node) {
			return node.next.get();
		}

	}


	private abstract class AbstractItr implements Iterator<E> {

		private boolean first = true;
		private Node<E> curNode;
		private Node<E> nextNode;

		@Override
		public boolean hasNext() {
			if (first) {
				first = false;
				nextNode = beginNode();
			}
			return nextNode != null;
		}

		@Override
		public E next() {
			if (nextNode == null) {
				throw new NoSuchElementException();
			}
			curNode = nextNode;
			nextNode = nextNode(nextNode);
			return curNode.item;
		}

		@Override
		public void remove() {
			if (curNode == null)
				throw new IllegalStateException();
			if (unlink(curNode)) {
				size.decrementAndGet();
			}
			curNode = null;
		}

		protected abstract Node<E> beginNode();

		protected abstract Node<E> nextNode(Node<E> node);

	}

}
