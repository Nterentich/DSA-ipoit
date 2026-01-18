package by.it.group410971.teterich.lesson14;

import java.util.*;

public class PointsA {
    static class Point {
        int x, y, z;
        int parent;
        int rank;

        Point(int x, int y, int z, int index) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.parent = index;
            this.rank = 0;
        }

        double distanceTo(Point other) {
            long dx = this.x - other.x;
            long dy = this.y - other.y;
            long dz = this.z - other.z;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        double D = scanner.nextDouble();
        int N = scanner.nextInt();

        Point[] points = new Point[N];

        for (int i = 0; i < N; i++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            int z = scanner.nextInt();
            points[i] = new Point(x, y, z, i);
        }

        // Объединяем точки, если расстояние меньше D
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                if (points[i].distanceTo(points[j]) < D) {
                    union(points, i, j);
                }
            }
        }

        // Собираем размеры кластеров
        Map<Integer, Integer> clusterSizes = new HashMap<>();
        for (int i = 0; i < N; i++) {
            int root = find(points, i);
            clusterSizes.put(root, clusterSizes.getOrDefault(root, 0) + 1);
        }

        // Собираем размеры в список
        List<Integer> sizes = new ArrayList<>();
        for (Integer size : clusterSizes.values()) {
            if (size > 0) {
                sizes.add(size);
            }
        }

        // Сортируем по УБЫВАНИЮ (обратный порядок)
        Collections.sort(sizes, Collections.reverseOrder());

        // Выводим результат
        for (int i = 0; i < sizes.size(); i++) {
            System.out.print(sizes.get(i));
            if (i < sizes.size() - 1) {
                System.out.print(" ");
            }
        }
    }

    private static int find(Point[] points, int index) {
        if (points[index].parent != index) {
            points[index].parent = find(points, points[index].parent);
        }
        return points[index].parent;
    }

    private static void union(Point[] points, int a, int b) {
        int rootA = find(points, a);
        int rootB = find(points, b);

        if (rootA != rootB) {
            // Объединение с эвристикой ранга
            if (points[rootA].rank < points[rootB].rank) {
                points[rootA].parent = rootB;
            } else if (points[rootA].rank > points[rootB].rank) {
                points[rootB].parent = rootA;
            } else {
                points[rootB].parent = rootA;
                points[rootA].rank++;
            }
        }
    }
}