import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        int[] unsortedArray = { 64, 25, 33, 90, 39, 100, 18, 12, 22, 11 };
        int[] threadOption = { 1, 2, 4, 8 };

        // Arguments input
        String name = args[0];
        int numOfThreads = Integer.parseInt(args[1]);

        int coreCount = Runtime.getRuntime().availableProcessors();
        System.out.println("Available cores: " + coreCount);

        switch (name) {
            case "SequentialSort":
                SequentialSort sequentialSort = new SequentialSort();
                sequentialSort.sort(unsortedArray);
                printArray(unsortedArray, name);
                break;

            case "ThreadSort":
                if (!isValidThreadCount(threadOption, numOfThreads)) {
                    return;
                }
                ThreadSort threadSort = new ThreadSort(numOfThreads);
                threadSort.sort(unsortedArray);
                printArray(unsortedArray, name);
                break;

            case "ExecutorServiceSort":
                            if (!isValidThreadCount(threadOption, numOfThreads)) {
                    return;
                }
                ExecutorServiceSort executorServiceSort = new ExecutorServiceSort(numOfThreads);
                executorServiceSort.sort(unsortedArray);
                printArray(unsortedArray, name);
                break;

            case "ForkJoinPoolSort":
                            if (!isValidThreadCount(threadOption, numOfThreads)) {
                    return;
                }
                ForkJoinPoolSort forkJoinPoolSort = new ForkJoinPoolSort(numOfThreads);
                forkJoinPoolSort.sort(unsortedArray);
                printArray(unsortedArray, name);
                break;

            case "ParallelStreamSort":
                            if (!isValidThreadCount(threadOption, numOfThreads)) {
                    return;
                }
                ParallelStreamSort parallelStreamSort = new ParallelStreamSort(numOfThreads);
                parallelStreamSort.sort(unsortedArray);
                printArray(unsortedArray, name);

                break;
            default:
                System.out.println("You gave this argument: " + name);
                System.out.println("Valid arguments are:");
                System.out.println("- SequentialSort");
                System.out.println("- ThreadSort");
                System.out.println("- ExecutorServiceSort");
                System.out.println("- ForkJoinPoolSort");
                System.out.println("- ParallelStreamSort");
                break;
        }

    }

    public static boolean isValidThreadCount(int[] validOptions, int count) {
        if (Arrays.stream(validOptions).anyMatch(x -> x == count)) {
            return true;
        } else {
            System.out.println("Invalid number of threads. Valid options are: " + Arrays.toString(validOptions));
            return false;
        }
    }

    public static void printArray(int[] arr, String type) {
        System.out.println("----- Type of sort: " + type + " -----");
        System.out.print("Sorted array: ");
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }
}
