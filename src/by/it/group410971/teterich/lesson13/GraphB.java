package by.it.group410971.teterich.lesson13;

import java.util.*;

public class GraphB {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Чтение строки с графом
        System.out.print("Введите граф в формате '1 -> 2, 1 -> 3, 2 -> 3': ");
        String input = scanner.nextLine().trim();

        // Парсинг строки и построение графа
        Map<Integer, List<Integer>> graph = new HashMap<>();

        // Удаляем все пробелы для упрощения парсинга
        input = input.replaceAll("\\s+", "");

        // Разбиваем по запятым на ребра
        String[] edges = input.split(",");

        for (String edge : edges) {
            String[] parts = edge.split("->");
            if (parts.length != 2) continue;

            int from = Integer.parseInt(parts[0]);
            int to = Integer.parseInt(parts[1]);

            // Добавляем ребро в граф
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);

            // Убедимся, что конечная вершина тоже есть в графе (даже если у нее нет исходящих ребер)
            graph.computeIfAbsent(to, k -> new ArrayList<>());
        }

        // Проверка на наличие циклов
        boolean hasCycle = hasCycle(graph);

        // Вывод результата
        System.out.println(hasCycle ? "yes" : "no");
    }

    // Метод для проверки наличия циклов с помощью DFS
    private static boolean hasCycle(Map<Integer, List<Integer>> graph) {
        // Состояния вершин: 0 - не посещена, 1 - в процессе обработки, 2 - обработана
        Map<Integer, Integer> state = new HashMap<>();

        for (Integer vertex : graph.keySet()) {
            state.put(vertex, 0);
        }

        // Проверяем каждую вершину
        for (Integer vertex : graph.keySet()) {
            if (state.get(vertex) == 0) {
                if (dfsHasCycle(vertex, graph, state)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean dfsHasCycle(int vertex, Map<Integer, List<Integer>> graph,
                                       Map<Integer, Integer> state) {
        // Помечаем вершину как обрабатываемую
        state.put(vertex, 1);

        // Проверяем всех соседей
        for (Integer neighbor : graph.getOrDefault(vertex, new ArrayList<>())) {
            if (state.get(neighbor) == 1) {
                // Нашли цикл - сосед находится в процессе обработки
                return true;
            }

            if (state.get(neighbor) == 0) {
                if (dfsHasCycle(neighbor, graph, state)) {
                    return true;
                }
            }
        }

        // Помечаем вершину как полностью обработанную
        state.put(vertex, 2);
        return false;
    }
}