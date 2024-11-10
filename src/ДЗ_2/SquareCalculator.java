package ДЗ_2;

import java.util.*;
import java.util.concurrent.*;

public class SquareCalculator implements Callable<List<Double>> {
    private final double[] numbers;

    public SquareCalculator(double[] numbers) {
        this.numbers = numbers;
    }

    @Override
    public List<Double> call() {
        CopyOnWriteArraySet<Double> squaredSet = new CopyOnWriteArraySet<>();
        for (double num : numbers) {
            squaredSet.add(num * num); // Обчислюємо квадрат вручну і додаємо до Set
        }
        return new ArrayList<>(squaredSet);
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime(); // Початковий час

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть мінімальне значення діапазону: ");
        double minRange = scanner.nextDouble();
        System.out.print("Введіть максимальне значення діапазону: ");
        double maxRange = scanner.nextDouble();

        Random random = new Random();
        int arraySize = 10;
        double[] numbers = new double[arraySize];
        for (int i = 0; i < arraySize; i++) {
            numbers[i] = minRange + (maxRange - minRange) * random.nextDouble();
        }

        System.out.println("Початковий масив: " + formatArray(numbers));

        // Розбиваємо числа на дві групи
        List<Double> group1 = new ArrayList<>();
        List<Double> group2 = new ArrayList<>();
        for (double num : numbers) {
            if (num >= 0 && num <= 49.5) {
                group1.add(num);
            } else {
                group2.add(num);
            }
        }

        System.out.println("Потік 1 (0 до 49.5): " + formatArray(group1));
        System.out.println("Потік 2 (інші): " + formatArray(group2));

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<List<Double>>> futures = new ArrayList<>();

        // Запускаємо обчислення для кожної групи
        Future<List<Double>> future1 = executorService.submit(new SquareCalculator(group1.stream().mapToDouble(Double::doubleValue).toArray()));
        futures.add(future1);
        Future<List<Double>> future2 = executorService.submit(new SquareCalculator(group2.stream().mapToDouble(Double::doubleValue).toArray()));
        futures.add(future2);

        CopyOnWriteArraySet<Double> finalResults = new CopyOnWriteArraySet<>();
        try {
            for (Future<List<Double>> future : futures) {
                if (!future.isCancelled()) {
                    List<Double> result = future.get(); // Блокуючий виклик
                    finalResults.addAll(result);
                    System.out.println("Результат: " + formatArray(result));
                }
                if (future.isDone()) {
                    System.out.println("Завдання виконано успішно.");
                } else {
                    System.out.println("Завдання ще виконується.");
                }
            }
        } catch (Exception e) {
            System.out.println("Виникла помилка: " + e.getMessage());
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }

        System.out.println("Кінцевий масив унікальних квадратів: " + formatArray(finalResults));

        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Час виконання програми: " + duration + " мс");
    }

    private static String formatArray(double[] array) {
        StringBuilder formatted = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            formatted.append(String.format("%.2f", array[i]));
            if (i < array.length - 1) {
                formatted.append(", ");
            }
        }
        formatted.append("]");
        return formatted.toString();
    }

    private static String formatArray(Collection<Double> collection) {
        StringBuilder formatted = new StringBuilder("[");
        int i = 0;
        for (Double num : collection) {
            formatted.append(String.format("%.2f", num));
            if (i < collection.size() - 1) {
                formatted.append(", ");
            }
            i++;
        }
        formatted.append("]");
        return formatted.toString();
    }
}
