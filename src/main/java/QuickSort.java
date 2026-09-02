/**
 * QuickSort
 * quickSort() chooses a pivot, partitions the array around the pivot,
 * and recursively sorts the left and right partitions.
 */
public class QuickSort {

    /** Sorts without drawing anything. */
    public static <T extends Comparable<T>> void quickSort(T[] arr, boolean ascending) {
        quickSort(arr, ascending, Animator.silent(arr));
    }

    public static <T extends Comparable<T>> void quickSort(T[] arr, boolean ascending, Animator<T> anim) {
        quickSort(arr, 0, arr.length - 1, ascending, anim);
        anim.finish("Sorted");
    }

    private static <T extends Comparable<T>> void quickSort(T[] arr, int low, int high, boolean ascending, Animator<T> anim) {
        if (low > high) return;

        // the window closes on the way out, so each level's frames are drawn
        // against its own slice and the parent's slice is restored underneath
        try (var range = anim.range(low, high)) {
            if (low == high) {
                anim.settle(low, "Single element [" + low + "] is sorted");
                return;
            }

            anim.frame("Partitioning range [" + low + ".." + high + "]");
            int pivotIndex = partition(arr, low, high, ascending, anim);
            anim.settle(pivotIndex, "Pivot " + arr[pivotIndex] + " locked in sorted position at index " + pivotIndex);

            quickSort(arr, low, pivotIndex - 1, ascending, anim);
            quickSort(arr, pivotIndex + 1, high, ascending, anim);
        }
    }

    private static <T extends Comparable<T>> int partition(T[] arr, int low, int high, boolean ascending, Animator<T> anim) {
        T pivot = arr[high]; // choose last element as pivot
        anim.pivot(high, "Selected pivot: " + pivot + " at index " + high); // stays magenta for the rest of this window

        int i = low - 1; // tracks partition boundary of elements placed on the left

        for (int j = low; j < high; j++) {
            anim.compare(j, "Comparing " + arr[j] + " (index " + j + ") with pivot " + pivot);

            int value = arr[j].compareTo(pivot);
            boolean belongsLeft = ascending ? (value <= 0) : (value >= 0);

            if (belongsLeft) {
                i++;
                if (i != j) {
                    swap(arr, i, j);
                    anim.swap(i, j, "Swapped " + arr[i] + " (index " + i + ") and " + arr[j] + " (index " + j + ")");
                }
            }
        }

        // Place pivot in its correct position
        int finalPivotPos = i + 1;
        if (finalPivotPos != high) {
            swap(arr, finalPivotPos, high);
            anim.swap(finalPivotPos, high, "Swapped pivot " + pivot + " into index " + finalPivotPos);
        }

        return finalPivotPos;
    }

    private static <T> void swap(T[] arr, int a, int b) {
        T temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
}
