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
            // 3.2 ---- This was added with 3.2 a done variable set to true.
            //done = true;
            //3.3 --------- This block was added on 3.3 with guarded block
            synchronized (lock) {
                done = true;
                lock.notify();
            }
        });

        // spawn a thread to print sharedInt
        Thread printingThread = new Thread(() -> {
            // 3.2 ---- This was added with 3.2 where the printingThread constantly checks if the done variable is set to true
            // If true then print the value of the sharedInt.
            //while(!done) { System.out.println(done); }
            //System.out.println("x: " + sharedInt);
            //3.3 --------- This block was added on 3.3 with guarded block
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