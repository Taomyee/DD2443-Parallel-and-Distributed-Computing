/**
 * ex6
 */
// https://www.cnblogs.com/fisherss/p/11747602.html
import java.util.concurrent.Semaphore;

public class DiningPhilosophersB {
    private static final int NUM_PHILOSOPHERS = 5;
    private static final Semaphore[] chopsticks = new Semaphore[NUM_PHILOSOPHERS];
    private static final Chop chops = new Chop();

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
                        chops.pickUpChopsticks(philosopherIndex);
                        eat(philosopherIndex);
                        chops.putDownChopsticks(philosopherIndex);
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
        System.out.println("Philosopher " + philosopherIndex + " is eating.");
        Thread.sleep(1000); // Simulate eating
        }
}

// A philosopher is allowed to pick up chopsticks only when both sides are available
class Chop{
    private boolean[] isAvailable = {false, false, false, false, false};

    public synchronized void pickUpChopsticks(int i) throws InterruptedException{
        while(isAvailable[i] || isAvailable[(i+1)%5]){
            wait();
        }
        isAvailable[i] = true;
        isAvailable[(i+1)%5] = true;
    }

    public synchronized void putDownChopsticks(int i){
        isAvailable[i] = false;
        isAvailable[(i+1)%5] = false;
        notify();
    }
}
