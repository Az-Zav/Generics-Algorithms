/**
 *
 * SelectionSort
 * selectionSort() walks through the array one position at a time (i)
 * for each position, scan the remaining unsorted elements (j) to find the target value (smallest or largest)
 * once found, swap the target into position i
 */

public class SelectionSort {

    public static <T extends Comparable<T>> void selectionSort(T[] arr, boolean ascending) {
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            int targetIndex = i; // assume current position already holds the target value

            for (int j = i + 1; j < n; j++) { //scan remaining unsorted elements for a better candidate
                int value = arr[j].compareTo(arr[targetIndex]);
                boolean better = ascending ? (value < 0) : (value > 0); //determines if sorting order is ascending or descending

                if (better) {
                    targetIndex = j;
                }
            }

            swap(arr, i, targetIndex); //place the found target into position i
        }
    }

    private static <T> void swap(T[] arr, int a, int b) {
        if (a == b) return; // no need to swap element with itself
        T temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
}