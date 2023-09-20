import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceSort implements Sorter {
    private final int threads;

    public ExecutorServiceSort(int threads) {
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
        // Improvement for load-balance is to use a smaller batchsize, instead of 
        // using one task for each thread (one chunk) then there could be more
        // chunks than number of threads that would reduce the idle
        // time of threads since different chunk may take various times completing.
        // Having more tasks in the task queue than there are threads minizmises the thread idle time. 
        // The threads can then take a new task when they are done with their current ones, different chunks
        // may take varrying amounts of time to sort. 
        int length = arr.length;
        int batchSize = length / threads;

        // Create an ExecutorService with the specified number of threads
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        // This loop populates the task queue with sorting tasks for the
        // ExecutorService.
        // It divides the original array into 'threads' number of chunks.
        // Each chunk is defined by its 'start' and 'end' indices in the array.
        // Threads will pick up these tasks for sorting the corresponding array
        // portions.
        // The last thread may get a differently sized chunk if the array size is not
        // perfectly divisible by 'threads'
        for (int i = 0; i < threads; i++) {
            int start = i * batchSize;
            int end = (i == threads - 1) ? length - 1 : (i + 1) * batchSize - 1;

            // Submit sorting tasks to the executor
            executorService.submit(new Worker(arr, start, end));
        }

        // Shutdown the executor and wait for all tasks to complete
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Merge the sorted chunks here (dependent on the specific sorting algorithm)
        mergeSortedBatches(arr);
    }

    public int getThreads() {
        return threads;
    }

    private void mergeSortedBatches(int[] arr) {
        //int[] aux = new int[arr.length]; // Create an auxiliary array for merging

        // Merge adjacent chunks of the array
        for (int chunkSize = 1; chunkSize < arr.length; chunkSize *= 2) {
            for (int left = 0; left < arr.length; left += 2 * chunkSize) {
                int mid = Math.min(left + chunkSize - 1, arr.length - 1);
                int right = Math.min(left + 2 * chunkSize - 1, arr.length - 1);

                merge(arr, left, mid, right);
            }
        }
    }

    public static void merge(int[] arr, int left, int mid, int right) {
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

    private static class Worker implements Runnable {
        private final int[] arr;
        private final int start;
        private final int end;

        Worker(int[] arr, int start, int end) {
            this.arr = arr;
            this.start = start;
            this.end = end;
        }

        public void run() {
            mergeSort(arr, start, end);
        }

        private void mergeSort(int[] arr, int left, int right) {
            if (left < right) {
                int mid = left + (right - left) / 2;

                // Sort the first and second halves
                mergeSort(arr, left, mid);
                mergeSort(arr, mid + 1, right);

                // Merge the sorted halves
                ExecutorServiceSort.merge(arr, left, mid, right);
            }
        }
    }
}
