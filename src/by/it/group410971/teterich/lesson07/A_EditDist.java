package by.it.group410971.teterich.lesson07;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class A_EditDist {

    int getDistanceEdinting(String one, String two) {
        //!!!!!!!!!!!!!!!!!!!!!!!!!     НАЧАЛО ЗАДАЧИ     !!!!!!!!!!!!!!!!!!!!!!!!!
        int m = one.length();
        int n = two.length();

        // Создаем таблицу для хранения результатов подзадач
        int[][] dp = new int[m + 1][n + 1];

        // Заполняем dp[][] снизу вверх
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                // Если первая строка пустая, единственный вариант - вставить все символы второй строки
                if (i == 0) {
                    dp[i][j] = j;
                }
                // Если вторая строка пустая, единственный вариант - удалить все символы первой строки
                else if (j == 0) {
                    dp[i][j] = i;
                }
                // Если последние символы совпадают, игнорируем последний символ и рекурсивно проверяем оставшиеся
                else if (one.charAt(i - 1) == two.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                // Если последние символы разные, рассматриваем все возможности:
                // вставка, удаление, замена и выбираем минимальную стоимость
                else {
                    dp[i][j] = 1 + Math.min(Math.min(
                                    dp[i][j - 1],    // Вставка
                                    dp[i - 1][j]),    // Удаление
                            dp[i - 1][j - 1]); // Замена
                }
            }
        }

        int result = dp[m][n];
        //!!!!!!!!!!!!!!!!!!!!!!!!!     КОНЕЦ ЗАДАЧИ     !!!!!!!!!!!!!!!!!!!!!!!!!
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = A_EditDist.class.getResourceAsStream("dataABC.txt");
        A_EditDist instance = new A_EditDist();
        Scanner scanner = new Scanner(stream);
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
    }
}