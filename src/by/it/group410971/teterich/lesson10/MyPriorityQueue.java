package by.it.group410971.teterich.lesson10;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class MyPriorityQueue<E> implements Queue<E> {
    private static final int DEFAULT_CAPACITY = 11;
    private Object[] heap;
    private int size;
    private final Comparator<? super E> comparator;

    public MyPriorityQueue() {
        this(DEFAULT_CAPACITY, null);
    }

    public MyPriorityQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    public MyPriorityQueue(Comparator<? super E> comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    public MyPriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be at least 1");
        }
        this.heap = new Object[initialCapacity];
        this.size = 0;
        this.comparator = comparator;
    }

    public MyPriorityQueue(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        this.comparator = null;
        this.heap = c.toArray();
        this.size = c.size();

        buildHeap();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(heap[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (heap[i] == null) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(heap[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean add(E e) {
        return offer(e);
    }

    @Override
    public boolean offer(E e) {
        if (e == null) {
            throw new NullPointerException("Null elements are not allowed in priority queue");
        }

        ensureCapacity(size + 1);

        int i = size;
        heap[i] = e;
        size++;

        siftUp(i, e);

        return true;
    }

    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }

        int s = --size;
        @SuppressWarnings("unchecked")
        E result = (E) heap[0];
        @SuppressWarnings("unchecked")
        E x = (E) heap[s];
        heap[s] = null;

        if (s != 0) {
            siftDown(0, x);
        }

        return result;
    }

    @Override
    public E remove() {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue is empty");
        }
        return poll();
    }

    @Override
    public E peek() {
        return (size == 0) ? null : (E) heap[0];
    }

    @Override
    public E element() {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue is empty");
        }
        return peek();
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (o.equals(heap[i])) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }
        if (c == this) {
            throw new IllegalArgumentException("Cannot add collection to itself");
        }

        boolean modified = false;
        for (E element : c) {
            if (element == null) {
                throw new NullPointerException("Null elements are not allowed");
            }
            if (offer(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        boolean modified = false;

        // Создаем новый массив для элементов, которые нужно сохранить
        Object[] newHeap = new Object[heap.length];
        int newSize = 0;

        // Копируем элементы, которые не содержатся в коллекции c
        for (int i = 0; i < size; i++) {
            Object element = heap[i];
            if (!c.contains(element)) {
                newHeap[newSize++] = element;
            } else {
                modified = true;
            }
        }

        if (modified) {
            // Заменяем старую кучу новой
            heap = newHeap;
            size = newSize;
            buildHeap();
        }

        return modified;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        boolean modified = false;
        Object[] temp = new Object[size];
        int newSize = 0;

        for (int i = 0; i < size; i++) {
            Object element = heap[i];
            if (c.contains(element)) {
                temp[newSize++] = element;
            } else {
                modified = true;
            }
        }

        if (modified) {
            heap = temp;
            size = newSize;
            buildHeap();
        }

        return modified;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > heap.length) {
            int newCapacity = Math.max(heap.length * 2, heap.length + (heap.length >> 1));
            if (newCapacity < 0) {
                newCapacity = Integer.MAX_VALUE;
            }
            Object[] newHeap = new Object[newCapacity];
            System.arraycopy(heap, 0, newHeap, 0, size);
            heap = newHeap;
        }
    }

    private void siftUp(int k, E x) {
        if (comparator != null) {
            siftUpUsingComparator(k, x);
        } else {
            siftUpComparable(k, x);
        }
    }

    private void siftUpComparable(int k, E x) {
        @SuppressWarnings("unchecked")
        Comparable<? super E> key = (Comparable<? super E>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = heap[parent];
            @SuppressWarnings("unchecked")
            E elem = (E) e;
            if (key.compareTo(elem) >= 0) {
                break;
            }
            heap[k] = e;
            k = parent;
        }
        heap[k] = key;
    }

    private void siftUpUsingComparator(int k, E x) {
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = heap[parent];
            @SuppressWarnings("unchecked")
            E elem = (E) e;
            if (comparator.compare(x, elem) >= 0) {
                break;
            }
            heap[k] = e;
            k = parent;
        }
        heap[k] = x;
    }

    private void siftDown(int k, E x) {
        if (comparator != null) {
            siftDownUsingComparator(k, x);
        } else {
            siftDownComparable(k, x);
        }
    }

    private void siftDownComparable(int k, E x) {
        @SuppressWarnings("unchecked")
        Comparable<? super E> key = (Comparable<? super E>) x;
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = heap[child];
            int right = child + 1;

            if (right < size) {
                @SuppressWarnings("unchecked")
                Comparable<? super E> leftChild = (Comparable<? super E>) c;
                @SuppressWarnings("unchecked")
                E rightElem = (E) heap[right];
                if (leftChild.compareTo(rightElem) > 0) {
                    child = right;
                    c = heap[child];
                }
            }

            @SuppressWarnings("unchecked")
            E childElem = (E) c;
            if (key.compareTo(childElem) <= 0) {
                break;
            }

            heap[k] = c;
            k = child;
        }
        heap[k] = key;
    }

    private void siftDownUsingComparator(int k, E x) {
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = heap[child];
            int right = child + 1;

            if (right < size) {
                @SuppressWarnings("unchecked")
                E leftChild = (E) c;
                @SuppressWarnings("unchecked")
                E rightChild = (E) heap[right];
                if (comparator.compare(leftChild, rightChild) > 0) {
                    child = right;
                    c = heap[child];
                }
            }

            @SuppressWarnings("unchecked")
            E childElem = (E) c;
            if (comparator.compare(x, childElem) <= 0) {
                break;
            }

            heap[k] = c;
            k = child;
        }
        heap[k] = x;
    }

    private void buildHeap() {
        for (int i = (size >>> 1) - 1; i >= 0; i--) {
            @SuppressWarnings("unchecked")
            E elem = (E) heap[i];
            siftDown(i, elem);
        }
    }

    private void removeAt(int i) {
        int s = --size;
        if (s == i) {
            heap[i] = null;
        } else {
            @SuppressWarnings("unchecked")
            E moved = (E) heap[s];
            heap[s] = null;
            siftDown(i, moved);
            if (heap[i] == moved) {
                siftUp(i, moved);
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }
}