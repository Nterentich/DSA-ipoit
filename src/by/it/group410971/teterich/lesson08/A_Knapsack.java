package by.it.group410971.teterich.lesson08;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class A_Knapsack {

    int getMaxWeight(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        int W = scanner.nextInt();  // Вместимость рюкзака
        int n = scanner.nextInt();  // Количество типов слитков
        int[] w = new int[n];      // Веса слитков

        for (int i = 0; i < n; i++) {
            w[i] = scanner.nextInt();
        }

        // Массив для хранения максимального веса для каждой вместимости
        int[] dp = new int[W + 1];

        // Заполняем массив dp
        for (int i = 1; i <= W; i++) {
            for (int j = 0; j < n; j++) {
                if (w[j] <= i) {
                    dp[i] = Math.max(dp[i], dp[i - w[j]] + w[j]);
                }
            }
        }

        return dp[W];
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = A_Knapsack.class.getResourceAsStream("dataA.txt");
        A_Knapsack instance = new A_Knapsack();
        int res = instance.getMaxWeight(stream);
        System.out.println(res);
    }
}