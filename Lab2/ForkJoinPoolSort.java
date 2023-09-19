import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinPoolSort implements Sorter {
    public final int threads;

    public ForkJoinPoolSort(int threads) {
        this.threads = threads;
    }

    public void sort(int[] arr) {
        ForkJoinPool pool = new ForkJoinPool(threads);
        pool.invoke(new Worker(arr, 0, arr.length - 1));
        pool.shutdown();
    }

    public int getThreads() {
        return threads;
    }

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
            // TODO: Implement the sorting logic here.
            // This is where you should apply your sorting algorithm.
            mergeSort(arr, start, end);
        }

        private void mergeSort(int[] arr, int left, int right) {
            if (left < right) {
                int mid = left + (right - left) / 2;

                // Sort the first and second halves
                mergeSort(arr, left, mid);
                mergeSort(arr, mid + 1, right);

                // Merge the sorted halves
                merge(arr, left, mid, right);
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
