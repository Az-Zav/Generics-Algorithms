import java.util.Arrays;

/**
 *
 * MergeSort
 * mergeSort() recursively splits indices until each array has one element left
 * once one element left for each sub-array, call merge() to sort the two subarrays being compared and overwrite in original array
 */
public class MergeSort {

    /** Sorts without drawing anything. */
    public static <T extends Comparable<T>> void mergeSort(T[] arr, boolean ascending) {
        mergeSort(arr, ascending, Animator.silent(arr));
    }

    public static <T extends Comparable<T>> void mergeSort(T[] arr, boolean ascending, Animator<T> anim) {
        mergeSort(arr, 0, arr.length - 1, ascending, anim);
        anim.finish("Sorted");
    }

    private static <T extends Comparable<T>> void mergeSort(T[] arr, int left, int right, boolean ascending, Animator<T> anim) {
        if (left >= right) {
            if (left == right) anim.markSorted(left); // single-element base case is trivially sorted
            return;
        }

        int mid = (left + right) / 2;

        try (var range = anim.range(left, right)) {
            // the gap opened here stays visible through both halves' full recursion,
            // until this level's join() heals it -- ancestors' gaps stay open too
            anim.split(mid, "Splitting " + slice(arr, left, right)
                    + " into " + slice(arr, left, mid) + " and " + slice(arr, mid + 1, right));

            mergeSort(arr, left, mid, ascending, anim);          // recurse left array from split
            mergeSort(arr, mid + 1, right, ascending, anim);     // recurse right array from split
            merge(arr, left, mid, right, ascending, anim);
        }
    }

    private static <T extends Comparable<T>> void merge(T[] arr, int left, int mid, int right, boolean ascending, Animator<T> anim) {
        T[] leftArr = Arrays.copyOfRange(arr, left, mid + 1);      // third parameter is exclusive
        T[] rightArr = Arrays.copyOfRange(arr, mid + 1, right + 1); // third parameter is exclusive

        int i = 0, j = 0, k = left; // k tracks the lowest element index of sub-array

        // replace position of lowest element with lowest value while left and right arrays still have values to compare
        while (i < leftArr.length && j < rightArr.length) {
            anim.compare(left + i, mid + 1 + j,
                    "Comparing " + leftArr[i] + " (left half) and " + rightArr[j] + " (right half)");

            int value = leftArr[i].compareTo(rightArr[j]); // if smaller, returns negative value
            boolean takeLeft = ascending ? (value <= 0) : (value >= 0); // determines if sorting order is ascending or descending

            arr[k] = takeLeft ? leftArr[i++] : rightArr[j++];
            anim.place(k, "Placing " + arr[k] + " at index " + k);
            k++;
        }

        // if one array's elements are all taken, add all remaining values of other array to remaining positions
        while (i < leftArr.length) {
            arr[k] = leftArr[i++];
            anim.place(k, "Placing " + arr[k] + " at index " + k);
            k++;
        }
        while (j < rightArr.length) {
            arr[k] = rightArr[j++];
            anim.place(k, "Placing " + arr[k] + " at index " + k);
            k++;
        }

        anim.join(mid, "Merged " + slice(arr, left, right)); // this range is whole again, heal the gap this level opened
    }

    private static <T> String slice(T[] arr, int from, int to) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = from; i <= to; i++) {
            sb.append(arr[i]);
            if (i < to) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
