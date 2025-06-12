package by.it.group410971.teterich.lesson08;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class B_Knapsack {

    int getMaxWeight(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        int W = scanner.nextInt();  // Вместимость рюкзака
        int n = scanner.nextInt();  // Количество слитков
        int[] w = new int[n];      // Веса слитков

        for (int i = 0; i < n; i++) {
            w[i] = scanner.nextInt();
        }

        // Массив для хранения максимального веса для каждой вместимости
        int[] dp = new int[W + 1];

        // Заполняем массив dp
        for (int i = 0; i < n; i++) {
            for (int j = W; j >= w[i]; j--) {
                if (dp[j - w[i]] + w[i] > dp[j]) {
                    dp[j] = dp[j - w[i]] + w[i];
                }
            }
        }

        return dp[W];
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = B_Knapsack.class.getResourceAsStream("dataB.txt");
        B_Knapsack instance = new B_Knapsack();
        int res = instance.getMaxWeight(stream);
        System.out.println(res);
    }
}