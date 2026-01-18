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

        // Сортируем компоненты по минимальной вершине в компоненте
        components.sort((comp1, comp2) -> {
            char min1 = comp1.iterator().next(); // TreeSet уже отсортирован
            char min2 = comp2.iterator().next();
            return Character.compare(min1, min2);
        });

        // Вывод
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < components.size(); i++) {
            Set<Character> comp = components.get(i);
            for (char c : comp) {
                result.append(c);
            }
            if (i < components.size() - 1) {
                result.append("\n");
            }
        }
        System.out.print(result.toString());
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