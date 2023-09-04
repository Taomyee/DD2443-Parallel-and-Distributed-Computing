public class ex2 implements Runnable{
    static public int i = 0;

    public void run() {
        //ex2A();
        ex2B();
    }

    public static void ex2A() {
        for (int j = 0; j < 1000000; j++) {
            i++;
        }
    }

    public static synchronized void ex2B() {
        for (int j = 0; j < 1000000; j++) {
            i++;
        }
    }

    private static long runExperiment(int n) {
        long startTime = System.nanoTime();
        spawnThreads(n);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static void ex2C() {
        int warmup = 100;
        int measurement = 64;
        for (int i = 0; i < warmup; i++) {
            runExperiment(4);
        }
        long[] executionTime = new long[measurement];
        for (int i = 0; i < measurement; i++) {
            executionTime[i] = runExperiment(4);
            System.out.println("Execution time [" + i + "]: " +  + executionTime[i]);
        }
        long totalExecutionTime = 0;
        for (long time : executionTime) {
            totalExecutionTime += time;
        }
        System.out.println("Total execution time: " + totalExecutionTime);
        long avgExecutionTime = totalExecutionTime / measurement;
        double stdDeviation = calStdDeviation(executionTime, avgExecutionTime);
        System.out.println("Average execution time: " + avgExecutionTime);
        System.out.println("Standard deviation: " + stdDeviation);
    }

    private static double calStdDeviation(long[] values, long average) {
        double sum = 0;
        for (long value : values) {
            sum += Math.pow(value - average, 2);
        }
        double variance = sum / values.length;
        return Math.sqrt(variance);
    }

    public static void spawnThreads(int n){
        Runnable[] r = new Runnable[n];
        Thread[] threads = new Thread[n];
        for (int i = 0; i < n; i++) {
            r[i] = new ex2();
            threads[i] = new Thread(r[i], "t" + i);
            threads[i].start();
        }
        try {
            for (int i = 0; i < n; i++) {
                threads[i].join();
            }
        } catch (Exception e) {
            System.out.println("Interrupted");
        }
    }

    public static void main(String args[]) {
        // 2.1
        //int cores = Runtime.getRuntime().availableProcessors();
        //System.out.println("Cores: " + cores);

        // 2.1
        // spawnThreads(4);

        // 2.3.1
        //System.out.println(runExperiment(4) / 1000000.0 + " ms");

        // 2.3.2
        ex2C();

        System.out.println(i);
    }
}

