package Pr_3;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static final int ARRAY_SIZE = 15;
    private static final String FILE1 = "array1.txt";
    private static final String FILE2 = "array2.txt";
    private static final String FILE3 = "array3.txt";

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        // Заповнення трьох масивів випадковими числами
        int[] array1 = new Random().ints(ARRAY_SIZE, 0, 1001).toArray();
        int[] array2 = new Random().ints(ARRAY_SIZE, 0, 1001).toArray();
        int[] array3 = new Random().ints(ARRAY_SIZE, 0, 1001).toArray();

        // Запис масивів у файли
        writeArrayToFile(array1, FILE1);
        writeArrayToFile(array2, FILE2);
        writeArrayToFile(array3, FILE3);

        // Створення пулу потоків для виконання обробки масивів паралельно
        // newFixedThreadPool створює фіксовану кількість потоків(3)
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Зчитування масивів з файлів та вивід їхнього вмісту
        System.out.println("Зчитані масиви з файлів:");
        int[] readArray1 = readArrayFromFile(FILE1);
        int[] readArray2 = readArrayFromFile(FILE2);
        int[] readArray3 = readArrayFromFile(FILE3);

        System.out.println("Масив 1: " + Arrays.toString(readArray1));
        System.out.println("Масив 2: " + Arrays.toString(readArray2));
        System.out.println("Масив 3: " + Arrays.toString(readArray3));

        // Паралельна обробка масивів
        Future<int[]> futureArray1 = executor.submit(() -> processArray1(readArray1));
        Future<int[]> futureArray2 = executor.submit(() -> processArray2(readArray2));
        Future<int[]> futureArray3 = executor.submit(() -> processArray3(readArray3));

        int[] processedArray1 = futureArray1.get();
        int[] processedArray2 = futureArray2.get();
        int[] processedArray3 = futureArray3.get();

        executor.shutdown();

        // Вивід оброблених (але не відсортованих) масивів
        System.out.println("\nОброблені масиви (не відсортовані):");
        System.out.println("Оброблений масив 1 (Тільки непарні): " + Arrays.toString(processedArray1));
        System.out.println("Оброблений масив 2 (Ціла частина після ділення на 3): " + Arrays.toString(processedArray2));
        System.out.println("Оброблений масив 3 (Числа в межах 50-250): " + Arrays.toString(processedArray3));

        // Сортування оброблених масивів
        Arrays.sort(processedArray1);
        Arrays.sort(processedArray2);
        Arrays.sort(processedArray3);

        // Вивід відсортованих масивів
        System.out.println("\nВідсортовані оброблені масиви:");
        System.out.println("Відсортований масив 1: " + Arrays.toString(processedArray1));
        System.out.println("Відсортований масив 2: " + Arrays.toString(processedArray2));
        System.out.println("Відсортований масив 3: " + Arrays.toString(processedArray3));

        // Злиття масивів 1 та 2, видалення елементів з третього масиву
        List<Integer> mergedList = mergeArrays(processedArray1, processedArray2, processedArray3);

        // Вивід результату
        System.out.println("\nЗлитий масив без елементів з третього:");
        System.out.println(mergedList);
    }

    // Метод для запису масиву у файл
    private static void writeArrayToFile(int[] array, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int num : array) {
                writer.write(num + " ");
            }
        }
    }

    // Метод для зчитування масиву з файлу
    private static int[] readArrayFromFile(String filename) throws IOException {
        List<Integer> list = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextInt()) {
                list.add(scanner.nextInt());
            }
        }
        return list.stream().mapToInt(i -> i).toArray();
    }

    // Метод обробки першого масиву: залишити лише непарні значення
    private static int[] processArray1(int[] array) {
        return Arrays.stream(array)
                .filter(n -> n % 2 != 0)
                .toArray();
    }

    // Метод обробки другого масиву: поділити всі числа на 3, залишити лише цілу частину
    private static int[] processArray2(int[] array) {
        return Arrays.stream(array)
                .map(n -> n / 3)
                .toArray();
    }

    // Метод обробки третього масиву: залишити числа в діапазоні [50; 250]
    private static int[] processArray3(int[] array) {
        return Arrays.stream(array)
                .filter(n -> n >= 50 && n <= 250)
                .toArray();
    }

    // Злиття масивів 1 та 2, видалення елементів масиву 3
    private static List<Integer> mergeArrays(int[] array1, int[] array2, int[] array3) {
        // Додаємо елементи з array1 та array2 у mergedList
        List<Integer> mergedList = new ArrayList<>();
        for (int num : array1) {
            mergedList.add(num);
        }
        for (int num : array2) {
            mergedList.add(num);
        }

        // Створюємо Set для зберігання унікальних елементів з array3
        Set<Integer> setArray3 = new HashSet<>();
        for (int num : array3) {
            setArray3.add(num);
        }

        // Видаляємо з mergedList всі елементи, що є в setArray3
        mergedList.removeIf(setArray3::contains);

        // Сортуємо mergedList
        Collections.sort(mergedList);
        return mergedList;
    }
}