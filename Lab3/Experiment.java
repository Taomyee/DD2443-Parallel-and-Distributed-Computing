import java.sql.SQLOutput;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Experiment {

    public static long run_experiment(int threads, LockFreeSet<Integer> list, Distribution ops, Distribution values) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        Task[] tasks = new Task[threads];
        for (int i = 0; i < tasks.length; ++i) {
            tasks[i] = new Task(i, list, ops.copy(i), values.copy(-i));
        }

        long startTime = System.nanoTime();
        executorService.invokeAll(Arrays.asList(tasks));
        long endTime = System.nanoTime();
        executorService.shutdown();

        return endTime - startTime;
    }

    // Callable interface is similar to Runnable, but it can return a value.
    public static class Task implements Callable<Void> {
        private final int threadId;
        private final LockFreeSet<Integer> set;
        private final Distribution ops, values;

        public Task(int threadId, LockFreeSet<Integer> set, Distribution ops, Distribution values) {
            this.threadId = threadId;
            this.set = set;
            this.ops = ops;
            this.values = values;
        }

        public Void call() throws Exception {
            for (int i = 0; i < 1_000_000; ++i) {
                int val = values.next();
                int op = ops.next();
                // System.out.println("Thread " + threadId + " " + op + " " + val);
                switch (op) {
                    case 0:
                        set.add(threadId, val);
                        break;
                    case 1:
                        set.remove(threadId, val);
                        break;
                    case 2:
                        set.contains(threadId, val);
                        break;
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        int numThreads = 8;
        try {
            // Create a standard lock free skip list
            LockFreeSet<Integer> lockFreeSet = new LockFreeSkipList<>();

            // Create a discrete distribution with seed 42 such that,
            // A: p(0) = 1/10, p(1) = 1/10, p(2) = 8/10.
            Distribution ops = new Distribution.Discrete(42, new int[]{1, 1, 8});

            // B: p(0) = 1/2, p(1) = 1/2.
            // Distribution ops = new Distribution.Discrete(42, new int[]{5, 5, 0});

            // Create a uniform distribution with seed 84 with values from [0, 100000).
            //Distribution values = new Distribution.Uniform(84, 0, 1_000_000);

            Distribution values = new Distribution.Normal(84, 100, 0, 1_000_000);

            // Run experiment with 16 threads.
            float runTime = run_experiment(numThreads, lockFreeSet, ops, values);
            System.out.println("Runtime: " + runTime / 1e6 + "ms");

            // Get the log
            Log.Entry[] log = lockFreeSet.getLog();

            // Check sequential consistency
            Log.validate(log);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
