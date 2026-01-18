package by.it.group410971.teterich.lesson12;

import java.util.Map;

public class MyAvlMap implements Map<Integer, String> {
    private static class AvlNode {
        Integer key;
        String value;
        AvlNode left;
        AvlNode right;
        int height;

        AvlNode(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private AvlNode root;
    private int size;

    public MyAvlMap() {
        this.root = null;
        this.size = 0;
    }

    // Вспомогательные методы для AVL-дерева

    private int height(AvlNode node) {
        return node == null ? 0 : node.height;
    }

    private int balanceFactor(AvlNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(AvlNode node) {
        if (node != null) {
            node.height = Math.max(height(node.left), height(node.right)) + 1;
        }
    }

    // Повороты для балансировки

    private AvlNode rotateRight(AvlNode node) {
        AvlNode leftChild = node.left;
        AvlNode rightOfLeft = leftChild.right;

        // Выполняем поворот
        leftChild.right = node;
        node.left = rightOfLeft;

        // Обновляем высоты
        updateHeight(node);
        updateHeight(leftChild);

        return leftChild;
    }

    private AvlNode rotateLeft(AvlNode node) {
        AvlNode rightChild = node.right;
        AvlNode leftOfRight = rightChild.left;

        // Выполняем поворот
        rightChild.left = node;
        node.right = leftOfRight;

        // Обновляем высоты
        updateHeight(node);
        updateHeight(rightChild);

        return rightChild;
    }

    private AvlNode balance(AvlNode node) {
        if (node == null) {
            return null;
        }

        updateHeight(node);
        int balance = balanceFactor(node);

        // Левый левый случай
        if (balance > 1 && balanceFactor(node.left) >= 0) {
            return rotateRight(node);
        }

        // Левый правый случай
        if (balance > 1 && balanceFactor(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Правый правый случай
        if (balance < -1 && balanceFactor(node.right) <= 0) {
            return rotateLeft(node);
        }

        // Правый левый случай
        if (balance < -1 && balanceFactor(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // Поиск узла с минимальным ключом
    private AvlNode findMin(AvlNode node) {
        while (node != null && node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Основные операции

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        String[] oldValue = new String[1];
        root = put(root, key, value, oldValue);
        if (oldValue[0] == null) {
            size++;
        }
        return oldValue[0];
    }

    private AvlNode put(AvlNode node, Integer key, String value, String[] oldValue) {
        if (node == null) {
            return new AvlNode(key, value);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = put(node.left, key, value, oldValue);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value, oldValue);
        } else {
            // Ключ уже существует, обновляем значение
            oldValue[0] = node.value;
            node.value = value;
            return node;
        }

        return balance(node);
    }

    @Override
    public String remove(Object keyObj) {
        if (!(keyObj instanceof Integer)) {
            return null;
        }

        Integer key = (Integer) keyObj;
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        String[] removedValue = new String[1];
        root = remove(root, key, removedValue);
        if (removedValue[0] != null) {
            size--;
        }
        return removedValue[0];
    }

    private AvlNode remove(AvlNode node, Integer key, String[] removedValue) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = remove(node.left, key, removedValue);
        } else if (cmp > 0) {
            node.right = remove(node.right, key, removedValue);
        } else {
            // Найден узел для удаления
            removedValue[0] = node.value;

            // Узел с одним потомком или без потомков
            if (node.left == null || node.right == null) {
                AvlNode temp = (node.left != null) ? node.left : node.right;

                // Нет потомков
                if (temp == null) {
                    node = null;
                } else {
                    // Один потомок
                    node = temp;
                }
            } else {
                // Узел с двумя потомками: находим преемника (минимальный в правом поддереве)
                AvlNode temp = findMin(node.right);
                node.key = temp.key;
                node.value = temp.value;

                // Удаляем преемника
                String[] dummy = new String[1];
                node.right = remove(node.right, temp.key, dummy);
            }
        }

        if (node == null) {
            return null;
        }

        return balance(node);
    }

    @Override
    public String get(Object keyObj) {
        if (!(keyObj instanceof Integer)) {
            return null;
        }

        Integer key = (Integer) keyObj;
        if (key == null) {
            return null;
        }

        AvlNode node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(Object keyObj) {
        if (!(keyObj instanceof Integer)) {
            return false;
        }

        Integer key = (Integer) keyObj;
        if (key == null) {
            return false;
        }

        AvlNode node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                return true;
            }
        }
        return false;
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

    // Обход дерева для toString (in-order traversal)
    private void inorderTraversal(AvlNode node, StringBuilder sb) {
        if (node != null) {
            inorderTraversal(node.left, sb);

            if (sb.length() > 1) { // Уже есть хотя бы одна пара "ключ=значение"
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

    // Методы интерфейса Map, которые не требуются в задании

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("containsValue not implemented");
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
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
}