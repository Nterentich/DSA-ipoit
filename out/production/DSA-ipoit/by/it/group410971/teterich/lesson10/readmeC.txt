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

    // Конструктор без компаратора (естественный порядок)
    public MyPriorityQueue() {
        this(DEFAULT_CAPACITY, null);
    }

    // Конструктор с начальной емкостью
    public MyPriorityQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    // Конструктор с компаратором
    public MyPriorityQueue(Comparator<? super E> comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    // Основной конструктор
    public MyPriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be at least 1");
        }
        this.heap = new Object[initialCapacity];
        this.size = 0;
        this.comparator = comparator;
    }

    // Конструктор для создания из коллекции
    @SuppressWarnings("unchecked")
    public MyPriorityQueue(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        this.comparator = null;
        this.heap = c.toArray();
        this.size = c.size();

        // Построение кучи
        heapify();
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
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (heap[i] == null) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals((E) heap[i])) {
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

        // Добавляем элемент в конец
        int i = size;
        heap[i] = e;
        size++;

        // Просеиваем вверх
        siftUp(i, e);

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E poll() {
        if (size == 0) {
            return null;
        }

        int s = --size;
        E result = (E) heap[0];
        E x = (E) heap[s];
        heap[s] = null;

        if (s != 0) {
            siftDown(0, x);
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E remove() {
        if (size == 0) {
            throw new NoSuchElementException("Priority queue is empty");
        }
        return poll();
    }

    @Override
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }

        // Ищем элемент
        for (int i = 0; i < size; i++) {
            if (o.equals((E) heap[i])) {
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
        for (Object element : c) {
            while (remove(element)) {
                modified = true;
            }
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

        // Собираем элементы, которые нужно оставить
        for (int i = 0; i < size; i++) {
            E element = (E) heap[i];
            if (c.contains(element)) {
                temp[newSize++] = element;
            } else {
                modified = true;
            }
        }

        if (modified) {
            // Заменяем кучу новой
            heap = temp;
            size = newSize;
            heapify(); // Перестраиваем кучу
        }

        return modified;
    }

    // Вспомогательные методы для работы с кучей

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > heap.length) {
            int newCapacity = Math.max(heap.length * 2, heap.length + (heap.length >> 1));
            if (newCapacity < 0) { // Переполнение
                newCapacity = Integer.MAX_VALUE;
            }
            Object[] newHeap = new Object[newCapacity];
            System.arraycopy(heap, 0, newHeap, 0, size);
            heap = newHeap;
        }
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int k, E x) {
        if (comparator != null) {
            siftUpUsingComparator(k, x);
        } else {
            siftUpComparable(k, x);
        }
    }

    @SuppressWarnings("unchecked")
    private void siftUpComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = heap[parent];
            if (key.compareTo((E) e) >= 0) {
                break;
            }
            heap[k] = e;
            k = parent;
        }
        heap[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void siftUpUsingComparator(int k, E x) {
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = heap[parent];
            if (comparator.compare(x, (E) e) >= 0) {
                break;
            }
            heap[k] = e;
            k = parent;
        }
        heap[k] = x;
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int k, E x) {
        if (comparator != null) {
            siftDownUsingComparator(k, x);
        } else {
            siftDownComparable(k, x);
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDownComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        int half = size >>> 1; // Пока узел имеет хотя бы одного ребенка
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = heap[child];
            int right = child + 1;

            if (right < size &&
                ((Comparable<? super E>) c).compareTo((E) heap[right]) > 0) {
                child = right;
                c = heap[child];
            }

            if (key.compareTo((E) c) <= 0) {
                break;
            }

            heap[k] = c;
            k = child;
        }
        heap[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void siftDownUsingComparator(int k, E x) {
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = heap[child];
            int right = child + 1;

            if (right < size &&
                comparator.compare((E) c, (E) heap[right]) > 0) {
                child = right;
                c = heap[child];
            }

            if (comparator.compare(x, (E) c) <= 0) {
                break;
            }

            heap[k] = c;
            k = child;
        }
        heap[k] = x;
    }

    @SuppressWarnings("unchecked")
    private void heapify() {
        // Просеиваем все элементы с середины до начала
        for (int i = (size >>> 1) - 1; i >= 0; i--) {
            siftDown(i, (E) heap[i]);
        }
    }

    @SuppressWarnings("unchecked")
    private E removeAt(int i) {
        int s = --size;
        if (s == i) { // Удаляем последний элемент
            heap[i] = null;
        } else {
            E moved = (E) heap[s];
            heap[s] = null;
            siftDown(i, moved);
            if (heap[i] == moved) {
                siftUp(i, moved);
            }
        }
        return (i < size) ? (E) heap[i] : null;
    }

    /////////////////////////////////////////////////////////////////////////
    // Остальные методы интерфейса Queue<E> - оставлены нереализованными  //
    // так как требуются только указанные в задании методы               //
    /////////////////////////////////////////////////////////////////////////

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

    @Override
    public boolean removeAll(java.util.Collection<?> c) {
        // Реализован выше, но нужно объявить для интерфейса
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(java.util.Collection<?> c) {
        // Реализован выше, но нужно объявить для интерфейса
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {
        // Реализован выше, но нужно объявить для интерфейса
        throw new UnsupportedOperationException();
    }
}