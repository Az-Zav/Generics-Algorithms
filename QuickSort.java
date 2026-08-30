public class QuickSort {
    
    public static <T extends Comparable<T>> void quickSort(T[] arr, int low, int high, boolean ascending){ //low and high are element indices
        if (low < high) { //stopping condition for recursion if partition has 0 or 1 element
            int pivotIndex = partition(arr, low, high, ascending);
            quickSort(arr, low, pivotIndex - 1, ascending); //recurse on left partition
            quickSort(arr, pivotIndex + 1, high, ascending); //recurse on right partition
        }
    }

    private static <T extends Comparable<T>> int partition(T[] arr, int low, int high, boolean ascending) {
        T pivot = arr[high]; // choose last element as pivot
        int i = low - 1; //tracks the index of smaller partition boundary; -1 indicates empty
        for (int j = low; j < high; j++) {
            int value = arr[j].compareTo(pivot); //if smaller, returns negative value
            boolean belongsLeft = ascending ? (value <= 0) : (value >= 0); //determines if sorting order is ascending or descending
            if (belongsLeft) {
                i++; // increase slot for smaller partition value once found
                swap(arr, i, j); //swap found smaller element with current partition boundary slot
            }
        }
        swap(arr, i + 1, high); // swap pivot(last element) with slot right of partition boundary; place pivot value at the right slot
        return i + 1; //return index of pivot element's new slot
    }

    private static <T extends Comparable<T>> void swap(T[] arr, int a, int b) {
        T temp  = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
}
