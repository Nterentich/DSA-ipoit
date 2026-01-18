package by.it.group410971.teterich.lesson13;

import java.util.*;

public class GraphC {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();

        input = input.replaceAll("\\s+", "");
        String[] edges = input.split(",");

        Map<Character, List<Character>> graph = new HashMap<>();
        Map<Character, List<Character>> reverseGraph = new HashMap<>();
        Set<Character> vertices = new TreeSet<>();

        for (String edge : edges) {
            String[] parts = edge.split("->");
            if (parts.length != 2) continue;

            char from = parts[0].charAt(0);
            char to = parts[1].charAt(0);

            vertices.add(from);
            vertices.add(to);

            if (!graph.containsKey(from)) {
                graph.put(from, new ArrayList<>());
            }
            graph.get(from).add(to);

            if (!reverseGraph.containsKey(to)) {
                reverseGraph.put(to, new ArrayList<>());
            }
            reverseGraph.get(to).add(from);
        }

        for (char v : vertices) {
            if (!graph.containsKey(v)) {
                graph.put(v, new ArrayList<>());
            }
            if (!reverseGraph.containsKey(v)) {
                reverseGraph.put(v, new ArrayList<>());
            }
        }

        // Алгоритм Косарайю
        List<Set<Character>> components = new ArrayList<>();
        Set<Character> visited = new HashSet<>();
        List<Character> order = new ArrayList<>();

        // Первый обход
        for (char v : vertices) {
            if (!visited.contains(v)) {
                dfs1(v, graph, visited, order);
            }
        }

        // Второй обход
        visited.clear();
        Collections.reverse(order);

        for (char v : order) {
            if (!visited.contains(v)) {
                Set<Character> comp = new TreeSet<>();
                dfs2(v, reverseGraph, visited, comp);
                components.add(comp);
            }
        }

        // Теперь нужно отсортировать компоненты в порядке от истока к стоку
        // Для этого строим граф конденсации и находим топологический порядок

        // Создаем отображение вершина -> компонента
        Map<Character, Integer> vertexToComponent = new HashMap<>();
        for (int i = 0; i < components.size(); i++) {
            for (char v : components.get(i)) {
                vertexToComponent.put(v, i);
            }
        }

        // Строим граф конденсации
        int compCount = components.size();
        List<Set<Integer>> condensation = new ArrayList<>();
        for (int i = 0; i < compCount; i++) {
            condensation.add(new HashSet<>());
        }

        for (Map.Entry<Character, List<Character>> entry : graph.entrySet()) {
            char from = entry.getKey();
            int fromComp = vertexToComponent.get(from);

            for (char to : entry.getValue()) {
                int toComp = vertexToComponent.get(to);
                if (fromComp != toComp) {
                    condensation.get(fromComp).add(toComp);
                }
            }
        }

        // Топологическая сортировка конденсации
        List<Integer> topoOrder = new ArrayList<>();
        boolean[] visitedComps = new boolean[compCount];

        for (int i = 0; i < compCount; i++) {
            if (!visitedComps[i]) {
                topologicalSort(i, condensation, visitedComps, topoOrder);
            }
        }

        Collections.reverse(topoOrder);

        // Вывод в порядке топологической сортировки
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < compCount; i++) {
            int compIndex = topoOrder.get(i);
            Set<Character> comp = components.get(compIndex);
            for (char c : comp) {
                result.append(c);
            }
            if (i < compCount - 1) {
                result.append("\n");
            }
        }
        System.out.print(result.toString());
    }

    private static void topologicalSort(int v, List<Set<Integer>> graph,
                                        boolean[] visited, List<Integer> order) {
        visited[v] = true;
        for (int neighbor : graph.get(v)) {
            if (!visited[neighbor]) {
                topologicalSort(neighbor, graph, visited, order);
            }
        }
        order.add(v);
    }

    private static void dfs1(char v, Map<Character, List<Character>> graph,
                             Set<Character> visited, List<Character> order) {
        visited.add(v);
        List<Character> neighbors = graph.get(v);
        if (neighbors != null) {
            for (char neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    dfs1(neighbor, graph, visited, order);
                }
            }
        }
        order.add(v);
    }

    private static void dfs2(char v, Map<Character, List<Character>> reverseGraph,
                             Set<Character> visited, Set<Character> comp) {
        visited.add(v);
        comp.add(v);
        List<Character> neighbors = reverseGraph.get(v);
        if (neighbors != null) {
            for (char neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    dfs2(neighbor, reverseGraph, visited, comp);
                }
            }
        }
    }
}