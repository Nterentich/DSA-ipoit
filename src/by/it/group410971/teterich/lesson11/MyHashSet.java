package by.it.group410971.teterich.lesson11;

import java.util.Arrays;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Node<E>[] table;
    private int size;
    private final float loadFactor;

    private static class Node<E> {
        final E item;
        final int hash;
        Node<E> next;

        Node(E item, int hash, Node<E> next) {
            this.item = item;
            this.hash = hash;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    public MyHashSet() {
        this.table = (Node<E>[]) new Node[DEFAULT_CAPACITY];
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public MyHashSet(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor must be positive");
        }
        this.table = (Node<E>[]) new Node[initialCapacity];
        this.loadFactor = loadFactor;
        this.size = 0;
    }

    public MyHashSet(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
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
    public void clear() {
        if (size > 0) {
            Arrays.fill(table, null);  // Исправлено: замена цикла на Arrays.fill()
            size = 0;
        }
    }

    private int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        // Spread bits to better distribute hash codes
        return h ^ (h >>> 16);
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;
        int newCapacity = oldTable.length * 2;
        Node<E>[] newTable = (Node<E>[]) new Node[newCapacity];

        // Rehash all elements
        for (Node<E> head : oldTable) {  // Исправлено: использование enhanced for
            Node<E> node = head;
            while (node != null) {
                Node<E> next = node.next;
                int newIndex = indexFor(node.hash, newCapacity);
                node.next = newTable[newIndex];
                newTable[newIndex] = node;
                node = next;
            }
        }

        table = newTable;
    }

    @Override
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        int hash = hash(element);
        int index = indexFor(hash, table.length);

        // Check if element already exists
        Node<E> node = table[index];
        while (node != null) {
            if (node.hash == hash && node.item.equals(element)) {
                return false; // Element already exists
            }
            node = node.next;
        }

        // Check if resize is needed
        if (size >= table.length * loadFactor) {
            resize();
            index = indexFor(hash, table.length);
        }

        // Add new element at the beginning of the chain
        Node<E> newNode = new Node<>(element, hash, table[index]);
        table[index] = newNode;
        size++;
        return true;
    }

    @Override
    public boolean remove(Object obj) {
        if (obj == null) {
            return false;
        }

        int hash = hash(obj);
        int index = indexFor(hash, table.length);

        Node<E> node = table[index];
        Node<E> prev = null;

        while (node != null) {
            if (node.hash == hash && node.item.equals(obj)) {
                if (prev == null) {
                    // Removing first node in chain
                    table[index] = node.next;
                } else {
                    prev.next = node.next;
                }
                size--;
                return true;
            }
            prev = node;
            node = node.next;
        }

        return false;
    }

    @Override
    public boolean contains(Object obj) {
        if (obj == null) {
            return false;
        }

        int hash = hash(obj);
        int index = indexFor(hash, table.length);

        Node<E> node = table[index];
        while (node != null) {
            if (node.hash == hash && node.item.equals(obj)) {
                return true;
            }
            node = node.next;
        }

        return false;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (Node<E> head : table) {  // Исправлено: использование enhanced for
            Node<E> node = head;
            while (node != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(node.item);
                first = false;
                node = node.next;
            }
        }

        sb.append("]");
        return sb.toString();
    }

    // Методы интерфейса Set<E>, которые не требуются в задании
    // Реализованы с выбрасыванием исключения для соблюдения контракта интерфейса

    @Override
    public java.util.Iterator<E> iterator() {
        throw new UnsupportedOperationException("Iterator not implemented");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("toArray not implemented");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("toArray not implemented");
    }

    @Override
    public boolean containsAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException("containsAll not implemented");
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {
        throw new UnsupportedOperationException("addAll not implemented");
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException("retainAll not implemented");
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException("removeAll not implemented");
    }
}