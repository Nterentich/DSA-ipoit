package by.it.group410971.teterich.lesson11;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class MyTreeSet<E extends Comparable<E>> implements Set<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int size;

    @SuppressWarnings("unchecked")
    public MyTreeSet() {
        this.elements = (E[]) new Comparable[DEFAULT_CAPACITY];
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public MyTreeSet(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.elements = (E[]) new Comparable[initialCapacity];
        this.size = 0;
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
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    // Бинарный поиск элемента, возвращает индекс, если найден,
    // или позицию для вставки, если не найден (отрицательное число)
    private int binarySearch(E element) {
        if (size == 0) {
            return -1; // Массив пуст
        }

        int left = 0;
        int right = size - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            @SuppressWarnings("unchecked")
            E midElement = (E) elements[mid];
            int cmp = element.compareTo(midElement);

            if (cmp == 0) {
                return mid; // Элемент найден
            } else if (cmp < 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        // Элемент не найден, возвращаем -(позиция для вставки + 1)
        return -(left + 1);
    }

    // Увеличиваем емкость массива при необходимости
    @SuppressWarnings("unchecked")
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = Math.max(elements.length * 2, minCapacity);
            Object[] newElements = new Comparable[newCapacity];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
        }
    }

    @Override
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        int index = binarySearch(element);

        if (index >= 0) {
            // Элемент уже существует
            return false;
        }

        // Вычисляем позицию для вставки
        int insertPos = -(index + 1);

        // Проверяем емкость
        ensureCapacity(size + 1);

        // Сдвигаем элементы вправо для освобождения места
        if (insertPos < size) {
            System.arraycopy(elements, insertPos, elements, insertPos + 1, size - insertPos);
        }

        // Вставляем элемент
        elements[insertPos] = element;
        size++;

        return true;
    }

    @Override
    public boolean remove(Object obj) {
        if (obj == null) {
            return false;
        }

        @SuppressWarnings("unchecked")
        E element = (E) obj;
        int index = binarySearch(element);

        if (index < 0) {
            // Элемент не найден
            return false;
        }

        // Удаляем элемент, сдвигая остальные влево
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }

        // Очищаем последний элемент
        elements[--size] = null;

        return true;
    }

    @Override
    public boolean contains(Object obj) {
        if (obj == null) {
            return false;
        }

        @SuppressWarnings("unchecked")
        E element = (E) obj;
        return binarySearch(element) >= 0;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(elements[i]);
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

        // Для эффективности используем временный массив
        Object[] temp = new Object[size];
        int newSize = 0;

        // Копируем только те элементы, которые не содержатся в c
        for (int i = 0; i < size; i++) {
            Object element = elements[i];
            if (!c.contains(element)) {
                temp[newSize++] = element;
            } else {
                modified = true;
            }
        }

        // Обновляем массив, если что-то удалили
        if (modified) {
            // Копируем обратно
            for (int i = 0; i < newSize; i++) {
                elements[i] = temp[i];
            }

            // Очищаем оставшиеся ячейки
            for (int i = newSize; i < size; i++) {
                elements[i] = null;
            }

            size = newSize;
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

        // Для эффективности используем временный массив
        Object[] temp = new Object[size];
        int newSize = 0;

        // Копируем только те элементы, которые содержатся в c
        for (int i = 0; i < size; i++) {
            Object element = elements[i];
            if (c.contains(element)) {
                temp[newSize++] = element;
            } else {
                modified = true;
            }
        }

        // Обновляем массив, если что-то удалили
        if (modified) {
            // Копируем обратно
            for (int i = 0; i < newSize; i++) {
                elements[i] = temp[i];
            }

            // Очищаем оставшиеся ячейки
            for (int i = newSize; i < size; i++) {
                elements[i] = null;
            }

            size = newSize;
        }

        return modified;
    }

    // Вспомогательный метод для получения элемента по индексу
    @SuppressWarnings("unchecked")
    private E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (E) elements[index];
    }

    // Методы интерфейса Set<E>, которые не требуются в задании

    @Override
    public java.util.Iterator<E> iterator() {
        throw new UnsupportedOperationException("Iterator not implemented");
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        System.arraycopy(elements, 0, array, 0, size);
        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("toArray with parameter not implemented");
    }
}