package by.it.group410971.teterich.lesson13;

import java.util.*;

public class GraphA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();

        // Парсинг строки и построение графа
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        // Удаляем все пробелы
        input = input.replaceAll("\\s+", "");

        // Разбиваем по запятым на ребра
        String[] edges = input.split(",");

        for (String edge : edges) {
            // Разделяем на вершину и список смежных вершин
            String[] parts = edge.split("->");
            if (parts.length != 2) continue;

            String from = parts[0];
            String toPart = parts[1];

            // Инициализируем список смежности и inDegree
            if (!graph.containsKey(from)) {
                graph.put(from, new ArrayList<>());
            }
            if (!inDegree.containsKey(from)) {
                inDegree.put(from, 0);
            }

            // Разбиваем список смежных вершин (могут быть несколько)
            // Используем регулярное выражение, которое разделяет по всем не-цифро-буквенным символам
            String[] toVertices = toPart.split("[^\\w]+");

            for (String toStr : toVertices) {
                if (!toStr.isEmpty()) {
                    String to = toStr;

                    // Добавляем ребро в граф
                    graph.get(from).add(to);

                    // Увеличиваем inDegree для вершины to
                    inDegree.put(to, inDegree.getOrDefault(to, 0) + 1);

                    // Убедимся, что from также есть в inDegree
                    inDegree.putIfAbsent(from, 0);
                }
            }
        }

        // Добавляем вершины без ребер в граф
        for (String vertex : inDegree.keySet()) {
            graph.putIfAbsent(vertex, new ArrayList<>());
        }

        // Алгоритм Кана для топологической сортировки
        List<String> result = topologicalSort(graph, inDegree);

        // Вывод результата через пробел
        if (result.size() > 0) {
            System.out.print(result.get(0));
            for (int i = 1; i < result.size(); i++) {
                System.out.print(" " + result.get(i));
            }
            System.out.println();
        }
    }

    private static List<String> topologicalSort(Map<String, List<String>> graph,
                                                Map<String, Integer> inDegree) {
        List<String> result = new ArrayList<>();

        // Используем PriorityQueue для лексикографического порядка
        // Создаем компаратор, который сначала сравнивает как числа, если возможно
        PriorityQueue<String> queue = new PriorityQueue<>(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                // Пытаемся сравнить как числа
                try {
                    Integer n1 = Integer.parseInt(s1);
                    Integer n2 = Integer.parseInt(s2);
                    return n1.compareTo(n2);
                } catch (NumberFormatException e) {
                    // Если не числа, сравниваем как строки
                    return s1.compareTo(s2);
                }
            }
        });

        // Добавляем вершины с inDegree = 0
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            // Уменьшаем inDegree для всех смежных вершин
            if (graph.containsKey(current)) {
                for (String neighbor : graph.get(current)) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);

                    // Если inDegree стал 0, добавляем в очередь
                    if (inDegree.get(neighbor) == 0) {
                        queue.offer(neighbor);
                    }
                }
            }
        }

        return result;
    }
}