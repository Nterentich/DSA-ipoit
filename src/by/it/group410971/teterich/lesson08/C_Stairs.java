package by.it.group410971.teterich.lesson08;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class C_Stairs {

    int getMaxSum(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        int n = scanner.nextInt();
        int[] stairs = new int[n];
        for (int i = 0; i < n; i++) {
            stairs[i] = scanner.nextInt();
        }

        if (n == 0) return 0;
        if (n == 1) return Math.max(0, stairs[0]);

        // Массив для хранения максимальной суммы на каждой ступеньке
        int[] dp = new int[n];
        dp[0] = stairs[0];
        dp[1] = Math.max(stairs[0] + stairs[1], stairs[1]);

        for (int i = 2; i < n; i++) {
            // Максимум из: пришли с i-1 ступеньки или с i-2 ступеньки
            dp[i] = Math.max(dp[i-1], dp[i-2]) + stairs[i];
        }

        return Math.max(dp[n-1], dp[n-2]);
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_Stairs.class.getResourceAsStream("dataC.txt");
        C_Stairs instance = new C_Stairs();
        int res = instance.getMaxSum(stream);
        System.out.println(res);
    }
}