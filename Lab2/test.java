public class test {
    public static void main(String[] args) {
        int[] unsortedArray = {64, 25, 33, 90, 39, 100, 18, 12, 22, 11};
        /*SequentialSort sorter = new SequentialSort();
        sorter.sort(unsortedArray);*/
        /*ThreadSort sorter = new ThreadSort(16);
        sorter.sort(unsortedArray);*/
        /*ExecutorServiceSort sorter = new ExecutorServiceSort(3);
        sorter.sort(unsortedArray);*/
        /*ForkJoinPoolSort sorter = new ForkJoinPoolSort(4);
        sorter.sort(unsortedArray);*/
        ParallelStreamSort sorter = new ParallelStreamSort(4);
        sorter.sort(unsortedArray);
        System.out.print("Sorted array: ");
        for (int num : unsortedArray) {
            System.out.print(num + " ");
        }
    }
}
