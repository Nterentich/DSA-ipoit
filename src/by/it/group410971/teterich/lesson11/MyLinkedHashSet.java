package by.it.group410971.teterich.lesson11;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static class LinkedNode<E> {
        final E item;
        final int hash;
        LinkedNode<E> nextInBucket;  // следующий в той же корзине (при коллизиях)
        LinkedNode<E> nextInOrder;   // следующий в порядке добавления
        LinkedNode<E> prevInOrder;   // предыдущий в порядке добавления

        LinkedNode(E item, int hash, LinkedNode<E> nextInBucket) {
            this.item = item;
            this.hash = hash;
            this.nextInBucket = nextInBucket;
            this.nextInOrder = null;
            this.prevInOrder = null;
        }
    }

    private LinkedNode<E>[] table;
    private int size;
    private final float loadFactor;

    // Ссылки для поддержания порядка добавления
    private LinkedNode<E> firstInOrder;  // первый добавленный элемент
    private LinkedNode<E> lastInOrder;   // последний добавленный элемент

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        this.table = (LinkedNode<E>[]) new LinkedNode[DEFAULT_CAPACITY];
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.size = 0;
        this.firstInOrder = null;
        this.lastInOrder = null;
    }

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor must be positive");
        }
        this.table = (LinkedNode<E>[]) new LinkedNode[initialCapacity];
        this.loadFactor = loadFactor;
        this.size = 0;
        this.firstInOrder = null;
        this.lastInOrder = null;
    }

    public MyLinkedHashSet(int initialCapacity) {
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
            Arrays.fill(table, null);
            // Очищаем ссылки на двусвязный список порядка
            firstInOrder = null;
            lastInOrder = null;
            size = 0;
        }
    }

    private int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        LinkedNode<E>[] oldTable = table;
        int newCapacity = oldTable.length * 2;
        LinkedNode<E>[] newTable = (LinkedNode<E>[]) new LinkedNode[newCapacity];

        // Перехешируем все элементы
        for (LinkedNode<E> node : oldTable) {
            while (node != null) {
                LinkedNode<E> nextInBucket = node.nextInBucket;
                int newIndex = indexFor(node.hash, newCapacity);
                node.nextInBucket = newTable[newIndex];
                newTable[newIndex] = node;
                node = nextInBucket;
            }
        }

        table = newTable;
    }

    // Добавляем элемент в конец списка порядка
    private void addToOrderList(LinkedNode<E> node) {
        if (firstInOrder == null) {
            // Первый элемент
            firstInOrder = node;
            lastInOrder = node;
        } else {
            // Добавляем в конец
            node.prevInOrder = lastInOrder;
            lastInOrder.nextInOrder = node;
            lastInOrder = node;
        }
    }

    // Удаляем элемент из списка порядка
    private void removeFromOrderList(LinkedNode<E> node) {
        if (node.prevInOrder != null) {
            node.prevInOrder.nextInOrder = node.nextInOrder;
        } else {
            // Это первый элемент
            firstInOrder = node.nextInOrder;
        }

        if (node.nextInOrder != null) {
            node.nextInOrder.prevInOrder = node.prevInOrder;
        } else {
            // Это последний элемент
            lastInOrder = node.prevInOrder;
        }

        // Очищаем ссылки удаленного узла
        node.nextInOrder = null;
        node.prevInOrder = null;
    }

    @Override
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        int hash = hash(element);
        int index = indexFor(hash, table.length);

        // Проверяем, существует ли элемент
        LinkedNode<E> node = table[index];
        while (node != null) {
            if (node.hash == hash && node.item.equals(element)) {
                return false; // Элемент уже существует
            }
            node = node.nextInBucket;
        }

        // Проверяем, нужно ли увеличивать таблицу
        if (size >= table.length * loadFactor) {
            resize();
            index = indexFor(hash, table.length);
        }

        // Создаем новый узел и добавляем в начало цепочки
        LinkedNode<E> newNode = new LinkedNode<>(element, hash, table[index]);
        table[index] = newNode;

        // Добавляем в список порядка
        addToOrderList(newNode);

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

        LinkedNode<E> node = table[index];
        LinkedNode<E> prev = null;

        while (node != null) {
            if (node.hash == hash && node.item.equals(obj)) {
                // Удаляем из цепочки корзины
                if (prev == null) {
                    table[index] = node.nextInBucket;
                } else {
                    prev.nextInBucket = node.nextInBucket;
                }

                // Удаляем из списка порядка
                removeFromOrderList(node);

                size--;
                return true;
            }
            prev = node;
            node = node.nextInBucket;
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

        LinkedNode<E> node = table[index];
        while (node != null) {
            if (node.hash == hash && node.item.equals(obj)) {
                return true;
            }
            node = node.nextInBucket;
        }

        return false;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        LinkedNode<E> current = firstInOrder;
        boolean first = true;

        while (current != null) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(current.item);
            first = false;
            current = current.nextInOrder;
        }

        sb.append("]");
        return sb.toString();
    }

    // Методы для работы с коллекциями

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        if (c.isEmpty()) {
            return true;
        }

        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        if (c.isEmpty()) {
            return false;
        }

        boolean modified = false;
        for (E element : c) {
            if (add(element)) {
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

        if (c.isEmpty() || isEmpty()) {
            return false;
        }

        boolean modified = false;
        for (Object element : c) {
            if (remove(element)) {
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        if (isEmpty()) {
            return false;
        }

        if (c.isEmpty()) {
            clear();
            return true;
        }

        boolean modified = false;
        // Создаем копию элементов для безопасного удаления
        Object[] elements = toArrayInternal();

        for (Object element : elements) {
            if (!c.contains(element)) {
                remove(element);
                modified = true;
            }
        }

        return modified;
    }

    // Вспомогательный метод для toArray() в retainAll
    private Object[] toArrayInternal() {
        Object[] array = new Object[size];
        LinkedNode<E> current = firstInOrder;
        int i = 0;

        while (current != null) {
            array[i++] = current.item;
            current = current.nextInOrder;
        }

        return array;
    }

    // Методы интерфейса Set<E>, которые не требуются в задании

    @Override
    public java.util.Iterator<E> iterator() {
        throw new UnsupportedOperationException("Iterator not implemented");
    }

    @Override
    public Object[] toArray() {
        return toArrayInternal();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("toArray with parameter not implemented");
    }
}