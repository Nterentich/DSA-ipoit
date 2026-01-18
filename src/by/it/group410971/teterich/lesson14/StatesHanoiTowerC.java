package by.it.group410971.teterich.lesson14;

import java.util.Scanner;

public class StatesHanoiTowerC {

    static class State {
        int a, b, c;

        State(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        int maxHeight() {
            return Math.max(a, Math.max(b, c));
        }
    }

    static class DSU {
        int[] parent;
        int[] size;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int index) {
            if (parent[index] != index) {
                parent[index] = find(parent[index]);
            }
            return parent[index];
        }

        void union(int index1, int index2) {
            int root1 = find(index1);
            int root2 = find(index2);

            if (root1 != root2) {
                if (size[root1] < size[root2]) {
                    parent[root1] = root2;
                    size[root2] += size[root1];
                } else {
                    parent[root2] = root1;
                    size[root1] += size[root2];
                }
            }
        }
    }

    // Рекурсивная генерация состояний
    static int hanoi(int n, char from, char to, char aux,
                     State[] states, int[] a, int[] b, int[] c,
                     int step) {
        if (n == 0) {
            return step;
        }

        // Перемещаем n-1 дисков с from на aux
        step = hanoi(n - 1, from, aux, to, states, a, b, c, step);

        // Перемещаем 1 диск с from на to
        // Копируем предыдущие значения
        a[step] = a[step - 1];
        b[step] = b[step - 1];
        c[step] = c[step - 1];

        // Обновляем в зависимости от перемещения
        if (from == 'A') {
            a[step]--;
            if (to == 'B') b[step]++;
            else c[step]++;
        } else if (from == 'B') {
            b[step]--;
            if (to == 'A') a[step]++;
            else c[step]++;
        } else { // from == 'C'
            c[step]--;
            if (to == 'A') a[step]++;
            else b[step]++;
        }

        states[step] = new State(a[step], b[step], c[step]);
        step++;

        // Перемещаем n-1 дисков с aux на to
        step = hanoi(n - 1, aux, to, from, states, a, b, c, step);

        return step;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();

        if (N <= 0) {
            System.out.print("1");
            return;
        }

        int totalSteps = (1 << N) - 1;

        // Массивы для состояний
        State[] states = new State[totalSteps + 1];
        int[] a = new int[totalSteps + 1];
        int[] b = new int[totalSteps + 1];
        int[] c = new int[totalSteps + 1];

        // Начальное состояние
        a[0] = N;
        b[0] = 0;
        c[0] = 0;
        states[0] = new State(N, 0, 0);

        // Генерируем состояния
        hanoi(N, 'A', 'B', 'C', states, a, b, c, 1);

        // Создаем DSU
        DSU dsu = new DSU(totalSteps + 1);

        // Группируем состояния по максимальной высоте
        int[] firstIndex = new int[N + 1];
        for (int i = 0; i <= N; i++) {
            firstIndex[i] = -1;
        }

        // Пропускаем начальное состояние (индекс 0)
        for (int i = 1; i <= totalSteps; i++) {
            int maxH = states[i].maxHeight();
            if (firstIndex[maxH] == -1) {
                firstIndex[maxH] = i;
            } else {
                dsu.union(firstIndex[maxH], i);
            }
        }

        // Собираем размеры компонент
        int[] compSizes = new int[totalSteps + 1];
        for (int i = 1; i <= totalSteps; i++) {
            int root = dsu.find(i);
            compSizes[root]++;
        }

        // Собираем уникальные размеры
        int count = 0;
        int[] sizes = new int[totalSteps + 1];
        for (int i = 1; i <= totalSteps; i++) {
            if (compSizes[i] > 0) {
                sizes[count++] = compSizes[i];
            }
        }

        // Если нет состояний кроме начального
        if (count == 0) {
            System.out.print("1");
            return;
        }

        // Сортировка по ВОЗРАСТАНИЮ (пузырьком)
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (sizes[j] > sizes[j + 1]) { // Изменен знак на >
                    int temp = sizes[j];
                    sizes[j] = sizes[j + 1];
                    sizes[j + 1] = temp;
                }
            }
        }

        // Вывод
        for (int i = 0; i < count; i++) {
            System.out.print(sizes[i]);
            if (i < count - 1) {
                System.out.print(" ");
            }
        }
    }
}