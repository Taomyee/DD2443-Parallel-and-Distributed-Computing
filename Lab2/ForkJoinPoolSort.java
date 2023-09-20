import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/*
ForkJoinPool (executor service) - good for recursive tasks and used for parallelizing computational tasks like sorting. 
RecursiveAction - An instance of RecursiveAction calles fork() it will asynchronously execute compute)= in another thread of the ForkJoinPool
*/

public class ForkJoinPoolSort implements Sorter {
    public final int threads;

    public ForkJoinPoolSort(int threads) {
        this.threads = threads;
    }

    public void sort(int[] arr) {
        if(arr == null ){
            throw new IllegalArgumentException("The array cannot be null");
        }
        if(threads <= 0 ){
            throw new IllegalArgumentException("Must be at least 1 thread.");
        }
        if(arr.length <= 0){
            System.out.println("Allready sorted since array is empty or consists of only one element");
            return;
        }
        ForkJoinPool pool = new ForkJoinPool(threads);
        pool.invoke(new Worker(arr, 0, arr.length - 1));
        pool.shutdown();
    }

    public int getThreads() {
        return threads;
    }

    // RecursiveAction (Abstract class) is extended on the Worker class and overides
    // its compute() method to imlement the mergeSort algorithm.
    private static class Worker extends RecursiveAction {
        private final int[] arr;
        private final int start;
        private final int end;

        Worker(int[] arr, int start, int end) {
            this.arr = arr;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            //This compute() contains the logic for mergeSort
            if (start < end) {
                int middle = start + (end - start) / 2;
                // Subtask for first and second halves
                Worker leftArray = new Worker(arr, start, middle);
                Worker rightArray = new Worker(arr, middle + 1, end);

                // Fork the subtasks to run in parallel, it gets submitted to ForkJoinPool
                // ForkJoinPool manages the available threads to perform the tasks in parrallel.
                leftArray.fork();
                rightArray.fork();

                // wait for the subtasks to finish
                leftArray.join();
                rightArray.join();

                // Merge the sorted halves
                merge(arr, start, middle, end);

            }
        }

        private void merge(int[] arr, int left, int mid, int right) {
            int n1 = mid - left + 1;
            int n2 = right - mid;

            int[] leftArray = new int[n1];
            int[] rightArray = new int[n2];

            // Copy data to temp arrays leftArray[] and rightArray[]
            for (int i = 0; i < n1; i++) {
                leftArray[i] = arr[left + i];
            }
            for (int i = 0; i < n2; i++) {
                rightArray[i] = arr[mid + 1 + i];
            }

            // Merge the temp arrays
            int i = 0, j = 0, k = left;
            while (i < n1 && j < n2) {
                if (leftArray[i] <= rightArray[j]) {
                    arr[k] = leftArray[i];
                    i++;
                } else {
                    arr[k] = rightArray[j];
                    j++;
                }
                k++;
            }

            // Copy remaining elements of leftArray[], if any
            while (i < n1) {
                arr[k] = leftArray[i];
                i++;
                k++;
            }

            // Copy remaining elements of rightArray[], if any
            while (j < n2) {
                arr[k] = rightArray[j];
                j++;
                k++;
            }
        }
    }
}
