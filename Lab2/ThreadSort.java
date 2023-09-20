/**
 * Sort using Java's Thread, Runnable, start(), and join().
 */

public class ThreadSort implements Sorter {
    public final int threads;

    public ThreadSort(int threads) {
        this.threads = threads;
    }

    public void sort(int[] arr) {
        // TODO: sort arr.
        if (arr == null || arr.length <= 1) {
            // no sorting needed
            return;
        }
        int length = arr.length;
        int batchSize = length / threads;
        Thread[] threadArray = new Thread[threads];

        for (int i = 0; i < threads; i++){
            int start = i * batchSize;
            int end = (i == threads - 1) ? length - 1 : (i + 1) * batchSize - 1; // threads -1 is the last thread. The end is set to length -1. If i != threads -1 then ending index is set to (i+1) *batchsize -1.
            threadArray[i] = new Thread(new Worker(arr, start, end));
            threadArray[i].start();
        }
        for (Thread thread: threadArray){
            try {
                thread.join(); // Waits for the thread to die, it will wait for the thread to finish executing its run() method. 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //After all threads have finished executing their run() method then we merge the sorted batches.
        mergeSortedBatches(arr);
    }

    public int getThreads() {
        return threads;
    }

    private void mergeSortedBatches(int[] arr) {
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
                ThreadSort.merge(arr, left, mid, right);
            }
        }
    }
}
