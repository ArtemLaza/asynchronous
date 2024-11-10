package ДЗ_1;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

// Склад, що має обмежену кількість місць для товарів
class Warehouse {
    private int capacity;
    private int items;     // Поточна кількість товарів
    private Semaphore semaphore;  // Семафор для управління кількістю товарів

    public Warehouse(int capacity) {
        this.capacity = capacity;
        this.items = 0;
        this.semaphore = new Semaphore(capacity);
    }

    // Додавання товару на склад
    public void addItem() throws InterruptedException {
        if (items >= capacity) {
            System.out.println("Склад повний. Постачальник чекає...");
        } else {
            semaphore.acquire(); // Очікуємо вільне місце на складі
            synchronized (this) {
                items++;
                System.out.println("Постачальник додав товар. Товарів на складі: " + items);
            }
        }
    }

    // Вилучення товару зі складу
    public void removeItem() throws InterruptedException {
        synchronized (this) {
            if (items > 0) {
                items--;
                System.out.println("Покупець забрав товар. Товарів на складі: " + items);
                semaphore.release(); // Додаємо одне місце для нового товару
            } else {
                System.out.println("Склад порожній, покупець чекає...");
            }
        }
    }

    // Перевірка, чи є товари на складі
    public boolean hasItems() {
        return items > 0;
    }
}


// Клас постачальника, що додає товари на склад
class Supplier implements Runnable {
    private Warehouse warehouse;

    public Supplier(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public void run() {
        try {
            while (true) {
                warehouse.addItem(); // Додаємо товар на склад
                Thread.sleep(1000); // Час постачання товару
            }
        } catch (InterruptedException e) {
            System.out.println("Постачальника перервано.");
        }
    }
}

// Клас покупця, що забирає товари зі складу
class Customer implements Runnable {
    private Warehouse warehouse;

    public Customer(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (isWorkingHours()) {
                    warehouse.removeItem(); // Забираємо товар зі складу
                } else {
                    System.out.println("Неробочі години, покупець чекає...");
                }
                Thread.sleep(5000); // Час між спробами забрати товар
            }
        } catch (InterruptedException e) {
            System.out.println("Покупця перервано.");
        }
    }

    // Метод, що перевіряє робочі години (з 9 до 18)
    private boolean isWorkingHours() {
        int hour = java.time.LocalTime.now().getHour();
        return hour >= 9 && hour <= 23;
    }
}

public class Main {
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse(4);

        // Створюємо потоки постачальника і покупця
        Thread supplierThread = new Thread(new Supplier(warehouse));
        Thread customerThread = new Thread(new Customer(warehouse));

        supplierThread.start();
        customerThread.start();

        // Завершуємо програми через певний час (наприклад, 30 секунд)
        try {
            TimeUnit.SECONDS.sleep(10);
            supplierThread.interrupt();
            customerThread.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}