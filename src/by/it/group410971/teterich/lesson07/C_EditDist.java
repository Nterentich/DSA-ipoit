package by.it.group410971.teterich.lesson07;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class C_EditDist {

    String getDistanceEdinting(String one, String two) {
        int m = one.length();
        int n = two.length();

        // Создаем матрицу для хранения расстояний
        int[][] dp = new int[m + 1][n + 1];
        // Матрица для хранения операций
        String[][] operations = new String[m + 1][n + 1];

        // Инициализация базовых случаев
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
            if (i > 0) {
                operations[i][0] = "-" + one.charAt(i - 1);
            }
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
            if (j > 0) {
                operations[0][j] = "+" + two.charAt(j - 1);
            }
        }

        // Заполняем матрицу
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (one.charAt(i - 1) == two.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                    operations[i][j] = "#";
                } else {
                    int insert = dp[i][j - 1];
                    int delete = dp[i - 1][j];
                    int replace = dp[i - 1][j - 1];

                    if (insert <= delete && insert <= replace) {
                        dp[i][j] = insert + 1;
                        operations[i][j] = "+" + two.charAt(j - 1);
                    } else if (delete <= insert && delete <= replace) {
                        dp[i][j] = delete + 1;
                        operations[i][j] = "-" + one.charAt(i - 1);
                    } else {
                        dp[i][j] = replace + 1;
                        operations[i][j] = "~" + two.charAt(j - 1);
                    }
                }
            }
        }

        // Восстанавливаем последовательность операций
        StringBuilder sb = new StringBuilder();
        int i = m, j = n;
        while (i > 0 || j > 0) {
            String op = operations[i][j];
            if (op == null) {
                break;
            }
            sb.insert(0, op + ",");

            if (op.startsWith("#")) {
                i--;
                j--;
            } else if (op.startsWith("+")) {
                j--;
            } else if (op.startsWith("-")) {
                i--;
            } else if (op.startsWith("~")) {
                i--;
                j--;
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_EditDist.class.getResourceAsStream("dataABC.txt");
        C_EditDist instance = new C_EditDist();
        Scanner scanner = new Scanner(stream);
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(),scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(),scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(),scanner.nextLine()));
    }
}