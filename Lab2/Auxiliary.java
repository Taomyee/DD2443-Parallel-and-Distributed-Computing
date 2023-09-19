
/**
 * Helper methods.
 */

import java.util.Arrays;
import java.util.Random;

public class Auxiliary {
    /**
     * Generate a pseudo-random array of length `n`.
     */
    public static int[] arrayGenerate(int seed, int n) {
        Random prng = new Random(seed);
        int[] arr = new int[n];
        for (int i = 0; i < n; ++i)
            arr[i] = prng.nextInt();
        //System.out.println("Array: " + Arrays.toString(arr));
        return arr;
    }

    /**
     * Measures the execution time of the 'sorter'.
     * 
     * @param sorter   Sorting algorithm
     * @param n        Size of list to sort
     * @param initSeed Initial seed used for array generation
     * @param m        Measurment rounds.
     * @return result[0]: average execution time
     *         result[1]: standard deviation of execution time
     */
    public static double[] measure(Sorter sorter, int n, int initSeed, int m) {
        double[] result = new double[2];
        // Store execution times for all m rounds
        double[] times = new double[m];
        // Sum of all execution times, used for calculating average
        double totalExecutionTime = 0;

        for (int i = 0; i < m; ++i) {
            // Generate a new random array based on seed and array size
            int[] arr = Auxiliary.arrayGenerate(initSeed + i, n);

            // Record start time
            long startTime = System.nanoTime();

            // Sort the array
            sorter.sort(arr);

            // Record end time
            long endTime = System.nanoTime();

            // Calculate elapsed time in milliseconds
            double elapsedTime = (endTime - startTime) / 1e6;

            // Add to total execution time and save individual time
            totalExecutionTime += elapsedTime;
            times[i] = elapsedTime;
        }

        // Calculate average execution time
        double avgExecutionTime = totalExecutionTime / m;
        result[0] = avgExecutionTime;

        // Calculate standard deviation
        double sumOfSquareDifferences = 0;
        for (int i = 0; i < m; ++i) {
            sumOfSquareDifferences += Math.pow(times[i] - avgExecutionTime, 2);
        }
        double variance = sumOfSquareDifferences / m;
        double stdDeviation = Math.sqrt(variance);
        result[1] = stdDeviation;

        return result;
    }

    /**
     * Checks that the 'sorter' sorts.
     * 
     * @param sorter   Sorting algorithm
     * @param n        Size of list to sort
     * @param initSeed Initial seed used for array generation
     * @param m        Number of attempts.
     * @return True if the sorter successfully sorted all generated arrays.
     */
    public static boolean validate(Sorter sorter, int n, int initSeed, int m) {
        // TODO Check that the sorter works correctly.
        for (int i = 0; i < m; i++) {
            int[] arr = arrayGenerate(initSeed + i, n);
            sorter.sort(arr);
            if (!isSorted(arr)) {
                return false;
            }
        }
        return true;
    }

    // Helper method to check if an array is sorted
    private static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

}
