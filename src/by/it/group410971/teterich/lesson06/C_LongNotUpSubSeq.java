package by.it.group410971.teterich.lesson06;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class C_LongNotUpSubSeq {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_LongNotUpSubSeq.class.getResourceAsStream("dataC.txt");
        int[] result = getNotUpSeqAndIndices(stream);
        System.out.println(result[0]);  // Длина подпоследовательности
        for (int i = 1; i < result.length; i++) {
            System.out.print(result[i] + " ");  // Индексы элементов
        }
    }

    public static int[] getNotUpSeqAndIndices(InputStream stream) throws FileNotFoundException {
        Scanner scanner = new Scanner(stream);
        int n = scanner.nextInt();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = scanner.nextInt();
        }

        int[] dp = new int[n];
        int[] prev = new int[n];
        Arrays.fill(prev, -1);
        int maxLen = 0;
        int lastIdx = -1;

        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (arr[j] >= arr[i] && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    prev[i] = j;
                }
            }
            if (dp[i] > maxLen) {
                maxLen = dp[i];
                lastIdx = i;
            }
        }

        // Восстановление последовательности
        List<Integer> indices = new ArrayList<>();
        while (lastIdx != -1) {
            indices.add(lastIdx + 1);  // +1 для индексации с 1
            lastIdx = prev[lastIdx];
        }
        Collections.reverse(indices);

        // Формируем результат [длина, индексы...]
        int[] result = new int[indices.size() + 1];
        result[0] = maxLen;
        for (int i = 0; i < indices.size(); i++) {
            result[i + 1] = indices.get(i);
        }

        return result;
    }

    // Метод для теста (возвращает только длину)
    public static int getNotUpSeqSize(InputStream stream) throws FileNotFoundException {
        return getNotUpSeqAndIndices(stream)[0];
    }
}