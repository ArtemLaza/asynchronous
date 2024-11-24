package ДЗ_3_1;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;
public class Task_1 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        Scanner scanner = new Scanner(System.in);

        // Отримання кількості рядків
        int rows = getPositiveInt(scanner, "Введіть кількість рядків (позитивне число): ");

        // Отримання кількості стовпців
        int cols = getPositiveInt(scanner, "Введіть кількість стовпців (позитивне число): ");

        // Отримання мінімального значення
        System.out.print("Введіть мінімальне значення елементів: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Помилка! Введіть ціле число:");
            scanner.next();
        }
        int minVal = scanner.nextInt();

        // Отримання максимального значення
        int maxVal;
        do {
            System.out.print("Введіть максимальне значення елементів (більше за мінімальне): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Помилка! Введіть ціле число:");
                scanner.next();
            }
            maxVal = scanner.nextInt();
            if (maxVal <= minVal) {
                System.out.println("Помилка! Максимальне значення має бути більше за мінімальне.");
            }
        } while (maxVal <= minVal);

        scanner.close();


        // Генерація матриці
        long startGenerateMatrix = System.currentTimeMillis();
        int[][] matrix = generateMatrix(rows, cols, minVal, maxVal);
        long endGenerateMatrix = System.currentTimeMillis();
        System.out.println("Згенерована матриця:");
        printMatrix(matrix);
        System.out.println("Час виконання (Generate Matrix): " + (endGenerateMatrix - startGenerateMatrix) + " ms");

        // Work Dealing (Executor Service)
        ExecutorService executor = Executors.newFixedThreadPool(1);
        long startWorkDealing = System.currentTimeMillis();
        int[] sumsWorkDealing = workDealing(matrix, executor);
        long endWorkDealing = System.currentTimeMillis();
        executor.shutdown();

        // Вивід результатів Work Dealing
        System.out.println("\nСуми стовпців (Work Dealing):");
        printArray(sumsWorkDealing);
        System.out.println("Час виконання (Work Dealing): " + (endWorkDealing - startWorkDealing) + " ms");

        // Work Stealing (Fork/Join Framework)
        long startWorkStealing = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool(availableProcessors);
        WorkStealing task = new WorkStealing(matrix, 0, cols);
        long endWorkStealing = System.currentTimeMillis();

        // Вивід результатів Work Stealing
        System.out.println("\nСуми стовпців (Work Stealing):");
        printArray(forkJoinPool.invoke(task));
        System.out.println("Час виконання (Work Stealing): " + (endWorkStealing - startWorkStealing) + " ms");
    }
    private static int getPositiveInt(Scanner scanner, String message) {
        int value;
        do {
            System.out.print(message);
            while (!scanner.hasNextInt()) {
                System.out.println("Помилка! Введіть ціле число:");
                scanner.next();
            }
            value = scanner.nextInt();
            if (value <= 0) {
                System.out.println("Помилка! Число має бути більше за 0.");
            }
        } while (value <= 0);
        return value;
    }

    // Генерація матриці
    private static int[][] generateMatrix(int rows, int cols, int minVal, int maxVal) {
        Random random = new Random();
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(maxVal - minVal + 1) + minVal;
            }
        }
        return matrix;
    }

    // Обчислення сум Work Dealing (Executor Service)
    private static int[] workDealing(int[][] matrix, ExecutorService executor) throws ExecutionException, InterruptedException {
        int cols = matrix[0].length;
        Future<Integer>[] futures = new Future[cols];
        int[] sums = new int[cols];

        for (int col = 0; col < cols; col++) {
            final int columnIndex = col;
            futures[col] = executor.submit(() -> {
                int sum = 0;
                for (int[] row : matrix) {
                    sum += row[columnIndex];
                }
                return sum;
            });
        }

        for (int col = 0; col < cols; col++) {
            sums[col] = futures[col].get();
        }
        return sums;
    }



    // Вивід матриці
    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.printf("%10d", val);
            }
            System.out.println();
        }
    }

    // Вивід масиву
    private static void printArray(int[] array) {
        for (int val : array) {
            System.out.printf("%10d", val);
        }
        System.out.println();
    }
}
