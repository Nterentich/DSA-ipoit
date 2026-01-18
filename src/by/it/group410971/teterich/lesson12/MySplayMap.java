package by.it.group410971.teterich.lesson12;

import java.util.Comparator;
import java.util.NavigableMap;

public class MySplayMap implements NavigableMap<Integer, String> {
    private static class SplayNode {
        Integer key;
        String value;
        SplayNode left;
        SplayNode right;
        SplayNode parent;

        SplayNode(Integer key, String value, SplayNode parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
    }

    private SplayNode root;
    private int size;

    public MySplayMap() {
        this.root = null;
        this.size = 0;
    }

    // Основная операция splay - поднимает узел в корень
    private void splay(SplayNode node) {
        while (node.parent != null) {
            if (node.parent.parent == null) {
                // Z-образный поворот
                if (node == node.parent.left) {
                    rotateRight(node.parent);
                } else {
                    rotateLeft(node.parent);
                }
            } else if (node == node.parent.left && node.parent == node.parent.parent.left) {
                // Z-Z-образный (левый-левый)
                rotateRight(node.parent.parent);
                rotateRight(node.parent);
            } else if (node == node.parent.right && node.parent == node.parent.parent.right) {
                // Z-Z-образный (правый-правый)
                rotateLeft(node.parent.parent);
                rotateLeft(node.parent);
            } else if (node == node.parent.right && node.parent == node.parent.parent.left) {
                // Z-образный (левый-правый)
                rotateLeft(node.parent);
                rotateRight(node.parent);
            } else {
                // Z-образный (правый-левый)
                rotateRight(node.parent);
                rotateLeft(node.parent);
            }
        }
        root = node;
    }

    private void rotateLeft(SplayNode node) {
        SplayNode rightChild = node.right;
        if (rightChild == null) return;

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

    private void rotateRight(SplayNode node) {
        SplayNode leftChild = node.left;
        if (leftChild == null) return;

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

    // Поиск узла с ключом (без splay)
    private SplayNode findNode(Integer key) {
        SplayNode node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                return node;
            }
        }
        return null;
    }

    // Поиск узла и splay к корню
    private SplayNode findAndSplay(Integer key) {
        SplayNode node = findNode(key);
        if (node != null) {
            splay(node);
        }
        return node;
    }

    // Поиск максимального узла в поддереве
    private SplayNode maximum(SplayNode node) {
        while (node != null && node.right != null) {
            node = node.right;
        }
        return node;
    }

    // Поиск минимального узла в поддереве
    private SplayNode minimum(SplayNode node) {
        while (node != null && node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Основные операции Map

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        if (root == null) {
            root = new SplayNode(key, value, null);
            size = 1;
            return null;
        }

        SplayNode node = root;
        SplayNode parent = null;

        // Поиск позиции для вставки
        while (node != null) {
            parent = node;
            int cmp = key.compareTo(node.key);

            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                // Ключ уже существует
                String oldValue = node.value;
                node.value = value;
                splay(node);
                return oldValue;
            }
        }

        // Вставка нового узла
        SplayNode newNode = new SplayNode(key, value, parent);
        if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        splay(newNode);
        size++;
        return null;
    }

    @Override
    public String remove(Object keyObj) {
        if (!(keyObj instanceof Integer key)) {
            return null;
        }

        SplayNode node = findAndSplay(key);
        if (node == null) {
            return null;
        }

        String oldValue = node.value;

        // Удаление узла
        if (node.left == null) {
            root = node.right;
            if (root != null) {
                root.parent = null;
            }
        } else if (node.right == null) {
            root = node.left;
            if (root != null) {
                root.parent = null;
            }
        } else {
            // Узел имеет оба поддерева
            SplayNode leftSubtree = node.left;
            leftSubtree.parent = null;

            // Находим максимальный узел в левом поддереве
            SplayNode maxLeft = maximum(leftSubtree);
            splay(maxLeft); // Теперь maxLeft - корень

            // Присоединяем правое поддерево удаляемого узла
            maxLeft.right = node.right;
            if (node.right != null) {
                node.right.parent = maxLeft;
            }

            root = maxLeft;
        }

        size--;
        return oldValue;
    }

    @Override
    public String get(Object keyObj) {
        if (!(keyObj instanceof Integer key)) {
            return null;
        }

        SplayNode node = findAndSplay(key);
        return node != null ? node.value : null;
    }

    @Override
    public boolean containsKey(Object keyObj) {
        if (!(keyObj instanceof Integer key)) {
            return false;
        }

        return findAndSplay(key) != null;
    }

    @Override
    public boolean containsValue(Object valueObj) {
        if (!(valueObj instanceof String value)) {
            return false;
        }

        return containsValue(root, value);
    }

    private boolean containsValue(SplayNode node, String value) {
        if (node == null) {
            return false;
        }

        if (value.equals(node.value)) {
            splay(node); // Splay найденный узел
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

    // Методы NavigableMap

    @Override
    public Integer firstKey() {
        if (root == null) {
            throw new java.util.NoSuchElementException("Map is empty");
        }

        SplayNode node = minimum(root);
        if (node != null) {
            splay(node);
        }
        return node != null ? node.key : null;
    }

    @Override
    public Integer lastKey() {
        if (root == null) {
            throw new java.util.NoSuchElementException("Map is empty");
        }

        SplayNode node = maximum(root);
        if (node != null) {
            splay(node);
        }
        return node != null ? node.key : null;
    }

    @Override
    public Integer lowerKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        SplayNode node = lowerNode(key);
        return node != null ? node.key : null;
    }

    private SplayNode lowerNode(Integer key) {
        SplayNode node = root;
        SplayNode result = null;

        while (node != null) {
            if (node.key.compareTo(key) < 0) {
                result = node;
                node = node.right;
            } else {
                node = node.left;
            }
        }

        if (result != null) {
            splay(result);
        }
        return result;
    }

    @Override
    public Integer floorKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        SplayNode node = floorNode(key);
        return node != null ? node.key : null;
    }

    private SplayNode floorNode(Integer key) {
        SplayNode node = root;
        SplayNode result = null;

        while (node != null) {
            int cmp = node.key.compareTo(key);
            if (cmp <= 0) {
                result = node;
                if (cmp == 0) {
                    break;
                }
                node = node.right;
            } else {
                node = node.left;
            }
        }

        if (result != null) {
            splay(result);
        }
        return result;
    }

    @Override
    public Integer ceilingKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        SplayNode node = ceilingNode(key);
        return node != null ? node.key : null;
    }

    private SplayNode ceilingNode(Integer key) {
        SplayNode node = root;
        SplayNode result = null;

        while (node != null) {
            int cmp = node.key.compareTo(key);
            if (cmp >= 0) {
                result = node;
                if (cmp == 0) {
                    break;
                }
                node = node.left;
            } else {
                node = node.right;
            }
        }

        if (result != null) {
            splay(result);
        }
        return result;
    }

    @Override
    public Integer higherKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        SplayNode node = higherNode(key);
        return node != null ? node.key : null;
    }

    private SplayNode higherNode(Integer key) {
        SplayNode node = root;
        SplayNode result = null;

        while (node != null) {
            if (node.key.compareTo(key) > 0) {
                result = node;
                node = node.left;
            } else {
                node = node.right;
            }
        }

        if (result != null) {
            splay(result);
        }
        return result;
    }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey) {
        return headMap(toKey, false);
    }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) {
        if (toKey == null) {
            throw new NullPointerException("toKey cannot be null");
        }

        MySplayMap result = new MySplayMap();
        addToHeadMap(root, toKey, inclusive, result);
        return result;
    }

    private void addToHeadMap(SplayNode node, Integer toKey, boolean inclusive, MySplayMap result) {
        if (node == null) {
            return;
        }

        int cmp = node.key.compareTo(toKey);

        if (cmp < 0 || (inclusive && cmp == 0)) {
            result.put(node.key, node.value);
            addToHeadMap(node.left, toKey, inclusive, result);
            addToHeadMap(node.right, toKey, inclusive, result);
        } else {
            addToHeadMap(node.left, toKey, inclusive, result);
        }
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey) {
        return tailMap(fromKey, true);
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) {
        if (fromKey == null) {
            throw new NullPointerException("fromKey cannot be null");
        }

        MySplayMap result = new MySplayMap();
        addToTailMap(root, fromKey, inclusive, result);
        return result;
    }

    private void addToTailMap(SplayNode node, Integer fromKey, boolean inclusive, MySplayMap result) {
        if (node == null) {
            return;
        }

        int cmp = node.key.compareTo(fromKey);

        if (cmp > 0 || (inclusive && cmp == 0)) {
            result.put(node.key, node.value);
            addToTailMap(node.left, fromKey, inclusive, result);
            addToTailMap(node.right, fromKey, inclusive, result);
        } else {
            addToTailMap(node.right, fromKey, inclusive, result);
        }
    }

    // Обход дерева для toString (in-order traversal)
    private void inorderTraversal(SplayNode node, StringBuilder sb) {
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

    // Методы интерфейса Map и NavigableMap, которые не требуются в задании

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
    public NavigableMap<Integer, String> descendingMap() {
        throw new UnsupportedOperationException("descendingMap not implemented");
    }

    @Override
    public java.util.NavigableSet<Integer> navigableKeySet() {
        throw new UnsupportedOperationException("navigableKeySet not implemented");
    }

    @Override
    public java.util.NavigableSet<Integer> descendingKeySet() {
        throw new UnsupportedOperationException("descendingKeySet not implemented");
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive,
                                                Integer toKey, boolean toInclusive) {
        throw new UnsupportedOperationException("subMap not implemented");
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        throw new UnsupportedOperationException("subMap not implemented");
    }

    @Override
    public Entry<Integer, String> lowerEntry(Integer key) {
        throw new UnsupportedOperationException("lowerEntry not implemented");
    }

    @Override
    public Entry<Integer, String> floorEntry(Integer key) {
        throw new UnsupportedOperationException("floorEntry not implemented");
    }

    @Override
    public Entry<Integer, String> ceilingEntry(Integer key) {
        throw new UnsupportedOperationException("ceilingEntry not implemented");
    }

    @Override
    public Entry<Integer, String> higherEntry(Integer key) {
        throw new UnsupportedOperationException("higherEntry not implemented");
    }

    @Override
    public Entry<Integer, String> firstEntry() {
        throw new UnsupportedOperationException("firstEntry not implemented");
    }

    @Override
    public Entry<Integer, String> lastEntry() {
        throw new UnsupportedOperationException("lastEntry not implemented");
    }

    @Override
    public Entry<Integer, String> pollFirstEntry() {
        throw new UnsupportedOperationException("pollFirstEntry not implemented");
    }

    @Override
    public Entry<Integer, String> pollLastEntry() {
        throw new UnsupportedOperationException("pollLastEntry not implemented");
    }
}