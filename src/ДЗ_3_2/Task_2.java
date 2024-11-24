package ДЗ_3_2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Task_2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Введення директорії
        System.out.print("Введіть шлях до директорії: ");
        String directoryPath = scanner.nextLine();
        File rootDir = new File(directoryPath);

        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.out.println("Помилка! Введено некоректний шлях до директорії.");
            return;
        }

        // Введення розміру файлу
        System.out.print("Введіть мінімальний розмір файлу (в байтах): ");
        long minFileSize;
        while (!scanner.hasNextLong()) {
            System.out.println("Помилка! Введіть ціле число:");
            scanner.next();
        }
        minFileSize = scanner.nextLong();

        // Створення ExecutorService
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Integer>> results = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        for (File file : rootDir.listFiles()) {//rootDir.listFiles(): метод повертає масив файлів та піддиректорій у заданій директорії.
            if (file.isDirectory()) {
                // Створення задачі для обробки директорії
                results.add(executor.submit(() -> processDirectory(file, minFileSize)));
            } else if (file.isFile() && file.length() > minFileSize) {
                // Якщо файл задовольняє умову, враховуємо його
                results.add(executor.submit(() -> 1));
            }
        }

        // Підрахунок результатів
        int totalFiles = results.stream()
                .mapToInt(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        return 0;
                    }
                }).sum();
        long endTime = System.currentTimeMillis();

        // Виведення результатів
        System.out.println("Кількість файлів, розмір яких більше " + minFileSize + " байтів: " + totalFiles);
        System.out.println("Час виконання: " + (endTime - startTime) + " мс");

        executor.shutdown();
    }

    // Метод для обробки директорії
    private static int processDirectory(File directory, long minFileSize) {
        int count = 0;
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                count += processDirectory(file, minFileSize); // Рекурсивний обхід
            } else if (file.isFile() && file.length() > minFileSize) {
                count++;
            }
        }
        return count;
    }
}

