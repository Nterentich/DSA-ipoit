package by.it.group410971.teterich.lesson02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class A_VideoRegistrator {

    public static void main(String[] args) {
        A_VideoRegistrator instance = new A_VideoRegistrator();
        double[] events = new double[]{1, 1.1, 1.6, 2.2, 2.4, 2.7, 3.9, 8.1, 9.1, 5.5, 3.7};
        List<Double> starts = instance.calcStartTimes(events, 1); //рассчитаем моменты старта, с длинной сеанса 1
        System.out.println(starts);                            //покажем моменты старта
    }

    List<Double> calcStartTimes(double[] events, double workDuration) {
        List<Double> result = new ArrayList<>();
        Arrays.sort(events); // Сортируем события по возрастанию времени

        int i = 0;
        while (i < events.length) {
            double startTime = events[i]; // Момент старта видеорегистратора
            result.add(startTime); // Добавляем момент старта в результат
            double endTime = startTime + workDuration; // Момент окончания работы видеорегистратора

            // Пропускаем все события, которые попадают в интервал работы видеорегистратора
            while (i < events.length && events[i] <= endTime) {
                i++;
            }
        }

        return result;
    }
}