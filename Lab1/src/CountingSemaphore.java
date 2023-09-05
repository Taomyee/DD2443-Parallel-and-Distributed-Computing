/**
 * ex5
 */
public class CountingSemaphore {
    private int semaphore;

    public CountingSemaphore(int totalSemaphore) {
        if (totalSemaphore < 0) {
            throw new IllegalArgumentException("Initial count cannot be negative");
        }
        this.semaphore = totalSemaphore;
    }

    // release()
    public synchronized void signal() {
        semaphore++;
        System.out.println("[Thread " + Thread.currentThread().getId()+ "] signal Count: " + semaphore);
        if (semaphore <= 0) {
            notify(); // Wake up one waiting thread
        }
    }

    //acquire()
    public synchronized void s_wait() throws InterruptedException {
        semaphore--;
        System.out.println("[Thread " + Thread.currentThread().getId()+ "] wait Count: " + semaphore);
        if (semaphore < 0) {
            wait(); // Wait if count becomes negative
        }
    }

    public static void main(String[] args) {
        CountingSemaphore semaphore = new CountingSemaphore(1); // Initially 2

        Thread thread1 = new Thread(() -> {
            try {
                semaphore.s_wait();
                System.out.println("Thread 1 acquired the semaphore.");
                Thread.sleep(1000);
                semaphore.signal();
                System.out.println("Thread 1 released the semaphore.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                semaphore.s_wait();
                System.out.println("Thread 2 acquired the semaphore.");
                Thread.sleep(1000);
                semaphore.signal();
                System.out.println("Thread 2 released the semaphore.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread1.start();
        thread2.start();
    }
}
