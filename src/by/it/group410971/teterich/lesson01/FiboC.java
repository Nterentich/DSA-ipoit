package by.it.group410971.teterich.lesson01;

public class FiboC {

    private long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        FiboC fibo = new FiboC();
        int n = 55555;
        int m = 1000;
        System.out.printf("fasterC(%d)=%d \n\t time=%d \n\n", n, fibo.fasterC(n, m), fibo.time());
    }

    private long time() {
        return System.currentTimeMillis() - startTime;
    }

    long fasterC(long n, int m) {
        if (m == 1) {
            return 0;
        }
        long pisanoPeriod = getPisanoPeriod(m);
        long reducedN = n % pisanoPeriod;
        return fibonacciMod(reducedN, m);
    }

    private long getPisanoPeriod(int m) {
        long a = 0, b = 1, c;
        long period = 0;
        for (int i = 0; i < m * m; i++) {
            c = (a + b) % m;
            a = b;
            b = c;
            period++;
            if (a == 0 && b == 1) {
                return period;
            }
        }
        return period;
    }

    private long fibonacciMod(long n, int m) {
        if (n == 0) {
            return 0;
        }
        long a = 0, b = 1, c;
        for (long i = 2; i <= n; i++) {
            c = (a + b) % m;
            a = b;
            b = c;
        }
        return b;
    }
}