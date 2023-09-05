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
        System.out.println("[Thread " + Thread.currentThread().getName()+ "] signal Count: " + semaphore);
        if (semaphore <= 0) { // if the value prior to increment was -1 then after increment it is 0 therefore <= 0.
            notify(); // Wake up one waiting thread
        }
    }

    //acquire()
    public synchronized void s_wait() throws InterruptedException {
        semaphore--;
        System.out.println("[Thread " + Thread.currentThread().getName()+ "] wait Count: " + semaphore);
        if (semaphore < 0) {
            wait(); // Wait if count becomes negative
        }
    }


    public static void spawnThreads(int n, CountingSemaphore semaphore){
        Runnable task =() -> {
            try {
                semaphore.s_wait();
                System.out.println("[Thread " + Thread.currentThread().getName() + "] acquired the semaphore.");
                Thread.sleep(1000);
                semaphore.signal();
                System.out.println("[Thread " + Thread.currentThread().getName() + "]  released the semaphore.");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted");
            }
        };

        Thread[] threads = new Thread[n];
        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(task, "t_" + i);
            threads[i].start();
        }
    } 
    public static void main(String[] args) {
        //Change these values
        int numOfThreads = 5;
        int numSemaphores = 5; // with equal num of threads and semaphores then there is no waiting, otherwise if there is. 

        CountingSemaphore semaphore = new CountingSemaphore(numSemaphores); // Initially 2
        spawnThreads(numOfThreads, semaphore);
    }
}
