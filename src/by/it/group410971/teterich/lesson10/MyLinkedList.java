package by.it.group410971.teterich.lesson10;

import java.util.Deque;
import java.util.NoSuchElementException;

public class MyLinkedList<E> implements Deque<E> {

    // Внутренний класс Node для узла двунаправленного списка
    private static class Node<E> {
        E data;
        Node<E> prev;
        Node<E> next;

        Node(E data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    private Node<E> head; // первый элемент
    private Node<E> tail; // последний элемент
    private int size;     // количество элементов

    public MyLinkedList() {
        head = null;
        tail = null;
        size = 0;
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
        Node<E> current = head;

        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    // Метод для удаления элемента по индексу
    public E remove(int index) {
        checkIndex(index);

        if (index == 0) {
            return removeFirstImpl();
        } else if (index == size - 1) {
            return removeLastImpl();
        }

        Node<E> current = getNodeAtIndex(index);
        return unlink(current);
    }

    // Исправленный метод remove - должен переопределять remove(Object) из интерфейса
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            // Удаление первого null элемента
            for (Node<E> current = head; current != null; current = current.next) {
                if (current.data == null) {
                    unlink(current);
                    return true;
                }
            }
        } else {
            // Удаление элемента по equals
            for (Node<E> current = head; current != null; current = current.next) {
                if (o.equals(current.data)) {
                    unlink(current);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addFirst(E element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        Node<E> newNode = new Node<>(element);

        if (head == null) {
            // Список пуст
            head = newNode;
            tail = newNode;
        } else {
            // Добавляем в начало
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }

        size++;
    }

    @Override
    public void addLast(E element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        Node<E> newNode = new Node<>(element);

        if (tail == null) {
            // Список пуст
            head = newNode;
            tail = newNode;
        } else {
            // Добавляем в конец
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }

        size++;
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E getFirst() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        return head.data;
    }

    @Override
    public E getLast() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty");
        }
        return tail.data;
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        if (head == null) {
            return null;
        }

        E data = head.data;
        Node<E> next = head.next;

        // Помощь сборщику мусора
        head.data = null;
        head.next = null;

        head = next;

        if (head == null) {
            // Список стал пустым
            tail = null;
        } else {
            head.prev = null;
        }

        size--;
        return data;
    }

    @Override
    public E pollLast() {
        if (tail == null) {
            return null;
        }

        E data = tail.data;
        Node<E> prev = tail.prev;

        // Помощь сборщику мусора
        tail.data = null;
        tail.prev = null;

        tail = prev;

        if (tail == null) {
            // Список стал пустым
            head = null;
        } else {
            tail.next = null;
        }

        size--;
        return data;
    }

    // Вспомогательные методы

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private Node<E> getNodeAtIndex(int index) {
        Node<E> current;

        // Оптимизация: начинаем поиск с ближайшего конца
        if (index < (size >> 1)) {
            // Ищем от начала
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            // Ищем от конца
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }

        return current;
    }

    private E unlink(Node<E> node) {
        E data = node.data;
        Node<E> prev = node.prev;
        Node<E> next = node.next;

        // Помощь сборщику мусора
        node.data = null;
        node.prev = null;
        node.next = null;

        if (prev == null) {
            // Удаляем первый элемент
            head = next;
        } else {
            prev.next = next;
        }

        if (next == null) {
            // Удаляем последний элемент
            tail = prev;
        } else {
            next.prev = prev;
        }

        size--;
        return data;
    }

    // Переименованный метод, чтобы избежать конфликта с removeFirst() из интерфейса
    private E removeFirstImpl() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }

        E data = head.data;
        Node<E> next = head.next;

        // Помощь сборщику мусора
        head.data = null;
        head.next = null;

        head = next;

        if (head == null) {
            tail = null;
        } else {
            head.prev = null;
        }

        size--;
        return data;
    }

    // Переименованный метод, чтобы избежать конфликта с removeLast() из интерфейса
    private E removeLastImpl() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty");
        }

        E data = tail.data;
        Node<E> prev = tail.prev;

        // Помощь сборщику мусора
        tail.data = null;
        tail.prev = null;

        tail = prev;

        if (tail == null) {
            head = null;
        } else {
            tail.next = null;
        }

        size--;
        return data;
    }

    // Остальные методы интерфейса Deque<E> - оставлены нереализованными

    @Override
    public boolean offer(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offerFirst(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offerLast(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E removeFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E removeLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E peek() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E peekFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E peekLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void push(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pop() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Iterator<E> descendingIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(java.util.Collection<?> c) {
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