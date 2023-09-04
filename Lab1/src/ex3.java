import java.util.concurrent.atomic.AtomicLong;

public class ex3 {
    static public int sharedInt = 0;
    static public boolean done = false;

    public static void main(String args[]) {
        Object lock = new Object();
        AtomicLong startTime = new AtomicLong(0);
        // spawn a thread to increment sharedInt
        Thread incrementingThread = new Thread(() -> {
            for (int i = 0; i < 1000000; i++) { // 增加 1,000,000 次
                sharedInt++;
            }
            // 3.2
            // done = true;
            // 3.3
            synchronized (lock) {
                done = true;
                lock.notify();
            }
        });

        // spawn a thread to print sharedInt
        Thread printingThread = new Thread(() -> {
            // 3.2
            // while(!done) { }
            //3.3
            // All waiting threads wake up, but due to different execution orders, later ones may not meet the condition and should go back to sleep.
            synchronized (lock) {
                while (!done) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            long endTime = System.nanoTime();
            System.out.println("Time taken by incrementingThread to complete: " + (endTime-startTime.get()) + " ns");
            System.out.println("Value of sharedInt: " + sharedInt);
        });

        incrementingThread.start();
        // printThread may read startTime before writing it, so use AtomicLong to ensure visibility
        startTime.set(System.nanoTime());

        printingThread.start();
    }
}