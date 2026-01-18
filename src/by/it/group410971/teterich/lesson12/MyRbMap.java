package by.it.group410971.teterich.lesson12;

import java.util.Comparator;
import java.util.SortedMap;

public class MyRbMap implements SortedMap<Integer, String> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private static class RbNode {
        Integer key;
        String value;
        RbNode left;
        RbNode right;
        RbNode parent;
        boolean color;

        RbNode(Integer key, String value, boolean color, RbNode parent) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.parent = parent;
        }
    }

    private RbNode root;
    private int size;

    public MyRbMap() {
        this.root = null;
        this.size = 0;
    }

    // Вспомогательные методы для красно-черного дерева

    private boolean isRed(RbNode node) {
        return node != null && node.color == RED;
    }

    private void rotateLeft(RbNode node) {
        RbNode rightChild = node.right;
        node.right = rightChild.left;
        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }

        rightChild.parent = node.parent;
        if (node.parent == null) {
            root = rightChild;
        } else if (node == node.parent.left) {
            node.parent.left = rightChild;
        } else {
            node.parent.right = rightChild;
        }

        rightChild.left = node;
        node.parent = rightChild;
    }

    private void rotateRight(RbNode node) {
        RbNode leftChild = node.left;
        node.left = leftChild.right;
        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }

        leftChild.parent = node.parent;
        if (node.parent == null) {
            root = leftChild;
        } else if (node == node.parent.right) {
            node.parent.right = leftChild;
        } else {
            node.parent.left = leftChild;
        }

        leftChild.right = node;
        node.parent = leftChild;
    }

    private void fixInsert(RbNode node) {
        while (node != root && isRed(node.parent)) {
            if (node.parent == node.parent.parent.left) {
                RbNode uncle = node.parent.parent.right;

                // Случай 1: дядя красный
                if (isRed(uncle)) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    // Случай 2: дядя черный и узел - правый потомок
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }

                    // Случай 3: дядя черный и узел - левый потомок
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                RbNode uncle = node.parent.parent.left;

                // Случай 1: дядя красный
                if (isRed(uncle)) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    // Случай 2: дядя черный и узел - левый потомок
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    }

                    // Случай 3: дядя черный и узел - правый потомок
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }

        root.color = BLACK;
    }

    private void transplant(RbNode u, RbNode v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }

        if (v != null) {
            v.parent = u.parent;
        }
    }

    private RbNode minimum(RbNode node) {
        while (node != null && node.left != null) {
            node = node.left;
        }
        return node;
    }

    private void fixDelete(RbNode node) {
        while (node != root && !isRed(node)) {
            if (node == node.parent.left) {
                RbNode sibling = node.parent.right;

                // Случай 1: брат красный
                if (isRed(sibling)) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateLeft(node.parent);
                    sibling = node.parent.right;
                }

                // Случай 2: оба ребенка брата черные
                if (!isRed(sibling.left) && !isRed(sibling.right)) {
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    // Случай 3: правый ребенок брата черный
                    if (!isRed(sibling.right)) {
                        sibling.left.color = BLACK;
                        sibling.color = RED;
                        rotateRight(sibling);
                        sibling = node.parent.right;
                    }

                    // Случай 4
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    sibling.right.color = BLACK;
                    rotateLeft(node.parent);
                    node = root;
                }
            } else {
                RbNode sibling = node.parent.left;

                // Случай 1: брат красный
                if (isRed(sibling)) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateRight(node.parent);
                    sibling = node.parent.left;
                }

                // Случай 2: оба ребенка брата черные
                if (!isRed(sibling.right) && !isRed(sibling.left)) {
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    // Случай 3: левый ребенок брата черный
                    if (!isRed(sibling.left)) {
                        sibling.right.color = BLACK;
                        sibling.color = RED;
                        rotateLeft(sibling);
                        sibling = node.parent.left;
                    }

                    // Случай 4
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    sibling.left.color = BLACK;
                    rotateRight(node.parent);
                    node = root;
                }
            }
        }

        if (node != null) {
            node.color = BLACK;
        }
    }

    // Основные операции Map

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        RbNode parent = null;
        RbNode current = root;

        // Поиск позиции для вставки
        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);

            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                // Ключ уже существует, обновляем значение
                String oldValue = current.value;
                current.value = value;
                return oldValue;
            }
        }

        // Создаем новый узел (красный по умолчанию)
        RbNode newNode = new RbNode(key, value, RED, parent);

        if (parent == null) {
            root = newNode;
        } else if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        // Исправляем нарушения свойств красно-черного дерева
        fixInsert(newNode);
        size++;

        return null;
    }

    @Override
    public String remove(Object keyObj) {
        if (!(keyObj instanceof Integer key)) {
            return null;
        }

        RbNode node = findNode(key);

        if (node == null) {
            return null;
        }

        String oldValue = node.value;
        deleteNode(node);
        size--;

        return oldValue;
    }

    private RbNode findNode(Integer key) {
        RbNode current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return current;
            }
        }
        return null;
    }

    private void deleteNode(RbNode node) {
        RbNode y = node;
        RbNode x;
        boolean yOriginalColor = y.color;

        if (node.left == null) {
            x = node.right;
            transplant(node, node.right);
        } else if (node.right == null) {
            x = node.left;
            transplant(node, node.left);
        } else {
            y = minimum(node.right);
            yOriginalColor = y.color;
            x = y.right;

            if (y.parent == node) {
                if (x != null) {
                    x.parent = y;
                }
            } else {
                transplant(y, y.right);
                y.right = node.right;
                y.right.parent = y;
            }

            transplant(node, y);
            y.left = node.left;
            y.left.parent = y;
            y.color = node.color;
        }

        if (yOriginalColor == BLACK && x != null) {
            fixDelete(x);
        }
    }

    @Override
    public String get(Object keyObj) {
        if (!(keyObj instanceof Integer key)) {
            return null;
        }

        RbNode node = findNode(key);
        return node != null ? node.value : null;
    }

    @Override
    public boolean containsKey(Object keyObj) {
        if (!(keyObj instanceof Integer key)) {
            return false;
        }

        return findNode(key) != null;
    }

    @Override
    public boolean containsValue(Object valueObj) {
        if (!(valueObj instanceof String value)) {
            return false;
        }

        return containsValue(root, value);
    }

    private boolean containsValue(RbNode node, String value) {
        if (node == null) {
            return false;
        }

        if (value.equals(node.value)) {
            return true;
        }

        return containsValue(node.left, value) || containsValue(node.right, value);
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
        root = null;
        size = 0;
    }

    // Методы SortedMap

    @Override
    public Integer firstKey() {
        if (root == null) {
            throw new java.util.NoSuchElementException("Map is empty");
        }

        RbNode node = minimum(root);
        return node.key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) {
            throw new java.util.NoSuchElementException("Map is empty");
        }

        RbNode node = root;
        while (node.right != null) {
            node = node.right;
        }
        return node.key;
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        if (toKey == null) {
            throw new NullPointerException("toKey cannot be null");
        }

        MyRbMap result = new MyRbMap();
        addToHeadMap(root, toKey, result);
        return result;
    }

    private void addToHeadMap(RbNode node, Integer toKey, MyRbMap result) {
        if (node == null) {
            return;
        }

        if (node.key.compareTo(toKey) < 0) {
            result.put(node.key, node.value);
            addToHeadMap(node.left, toKey, result);
            addToHeadMap(node.right, toKey, result);
        } else {
            addToHeadMap(node.left, toKey, result);
        }
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        if (fromKey == null) {
            throw new NullPointerException("fromKey cannot be null");
        }

        MyRbMap result = new MyRbMap();
        addToTailMap(root, fromKey, result);
        return result;
    }

    private void addToTailMap(RbNode node, Integer fromKey, MyRbMap result) {
        if (node == null) {
            return;
        }

        if (node.key.compareTo(fromKey) >= 0) {
            result.put(node.key, node.value);
            addToTailMap(node.left, fromKey, result);
            addToTailMap(node.right, fromKey, result);
        } else {
            addToTailMap(node.right, fromKey, result);
        }
    }

    // Обход дерева для toString (in-order traversal)
    private void inorderTraversal(RbNode node, StringBuilder sb) {
        if (node != null) {
            inorderTraversal(node.left, sb);

            if (sb.length() > 1) {
                sb.append(", ");
            }

            sb.append(node.key).append("=").append(node.value);

            inorderTraversal(node.right, sb);
        }
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        inorderTraversal(root, sb);
        sb.append("}");
        return sb.toString();
    }

    // Методы интерфейса Map и SortedMap, которые не требуются в задании

    @Override
    public void putAll(java.util.Map<? extends Integer, ? extends String> m) {
        throw new UnsupportedOperationException("putAll not implemented");
    }

    @Override
    public java.util.Set<Integer> keySet() {
        throw new UnsupportedOperationException("keySet not implemented");
    }

    @Override
    public java.util.Collection<String> values() {
        throw new UnsupportedOperationException("values not implemented");
    }

    @Override
    public java.util.Set<Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException("entrySet not implemented");
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null; // используем естественный порядок
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        throw new UnsupportedOperationException("subMap not implemented");
    }
}