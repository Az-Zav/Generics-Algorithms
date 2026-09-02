/**
 *
 * SelectionSort
 * selectionSort() walks through the array one position at a time (i)
 * for each position, scan the remaining unsorted elements (j) to find the target value (smallest or largest)
 * once found, swap the target into position i
 */
public class SelectionSort {

    /** Sorts without drawing anything. */
    public static <T extends Comparable<T>> void selectionSort(T[] arr, boolean ascending) {
        selectionSort(arr, ascending, Animator.silent(arr));
    }

    public static <T extends Comparable<T>> void selectionSort(T[] arr, boolean ascending, Animator<T> anim) {
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            int targetIndex = i; // assume current position already holds the target value

            // the unscanned tail is the active window; everything before i is already green
            try (var range = anim.range(i, n - 1)) {
                anim.frame("Scanning [" + i + ".." + (n - 1) + "] for the "
                        + (ascending ? "smallest" : "largest") + " remaining value");

                for (int j = i + 1; j < n; j++) { // scan remaining unsorted elements for a better candidate
                    anim.compare(j, targetIndex, "Comparing " + arr[j] + " (index " + j + ") with best so far "
                            + arr[targetIndex] + " (index " + targetIndex + ")");

                    int value = arr[j].compareTo(arr[targetIndex]);
                    boolean better = ascending ? (value < 0) : (value > 0); // determines if sorting order is ascending or descending

                    if (better) {
                        targetIndex = j;
                    }
                }

                if (targetIndex != i) { // place the found target into position i
                    swap(arr, i, targetIndex);
                    anim.swap(i, targetIndex, "Swapped " + arr[i] + " into index " + i);
                }
                anim.settle(i, arr[i] + " is final at index " + i);
            }
        }

        anim.finish("Sorted");
    }

    private static <T> void swap(T[] arr, int a, int b) {
        T temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
}
