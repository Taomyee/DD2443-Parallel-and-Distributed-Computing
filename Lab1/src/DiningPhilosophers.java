/**
 * ex6
 */

import java.util.concurrent.Semaphore;

public class DiningPhilosophers {
    private static final int NUM_PHILOSOPHERS = 5;
    private static final Semaphore[] chopsticks = new Semaphore[NUM_PHILOSOPHERS];

    public static void main(String[] args) {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            chopsticks[i] = new Semaphore(1); // Initialize all chopsticks as available
        }

        Thread[] philosophers = new Thread[NUM_PHILOSOPHERS];

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            final int philosopherIndex = i;
            philosophers[i] = new Thread(() -> {
                while (true) {
                    try {
                        think(philosopherIndex);
                        eat(philosopherIndex);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            philosophers[i].start();
        }
    }

    private static void think(int philosopherIndex) throws InterruptedException {
        System.out.println("Philosopher " + philosopherIndex + " is thinking.");
        Thread.sleep(1000); // Simulate thinking
    }

    private static void eat(int philosopherIndex) throws InterruptedException {
        int leftChopstick = philosopherIndex;
        int rightChopstick = (philosopherIndex + 1) % NUM_PHILOSOPHERS;

        chopsticks[leftChopstick].acquire();
        System.out.println("Philosopher " + philosopherIndex + " picks up left chopstick.");
        Thread.sleep(1000); // Simulate eating
        chopsticks[rightChopstick].acquire();
        System.out.println("Philosopher " + philosopherIndex + " picks up right chopstick.");

        System.out.println("Philosopher " + philosopherIndex + " is eating.");
        Thread.sleep(1000); // Simulate eating
        chopsticks[leftChopstick].release();
        System.out.println("Philosopher " + philosopherIndex + " releases left chopstick.");
        chopsticks[rightChopstick].release();
        System.out.println("Philosopher " + philosopherIndex + " releases right chopstick.");
    }
}
