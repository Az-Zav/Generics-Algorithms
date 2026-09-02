/**
 *
 * InsertionSort
 * insertionSort() treats the first element as the sorted part, then takes each following element as the key
 * shifts every element of the sorted part that belongs after the key one position to the right, then inserts the key in the gap
 */
public class InsertionSort {

    /** Sorts without drawing anything. */
    public static <T extends Comparable<T>> void insertionSort(T[] arr, boolean ascending) {
        insertionSort(arr, ascending, Animator.silent(arr));
    }

    public static <T extends Comparable<T>> void insertionSort(T[] arr, boolean ascending, Animator<T> anim) {
        for (int i = 1; i < arr.length; i++) { // index 0 is already sorted, so start at the second element
            T key = arr[i]; // element being inserted into the sorted part

            // the sorted prefix plus the key is the active window
            try (var range = anim.range(0, i)) {
                anim.markSortedThrough(i - 1);
                anim.pivot(i, "Key " + key + " taken from index " + i); // held out, drawn magenta until it lands

                int j = i - 1; // last index of the sorted part
                while (j >= 0) {
                    anim.compare(j, "Comparing " + arr[j] + " (index " + j + ") with key " + key);

                    int value = arr[j].compareTo(key);
                    boolean shift = ascending ? (value > 0) : (value < 0); // determines if sorting order is ascending or descending

                    if (!shift) break; // arr[j] belongs before the key, so the gap at j + 1 is the correct spot

                    arr[j + 1] = arr[j]; // move the bigger element to the right to open the gap
                    anim.shift(j + 1, "Shifted " + arr[j + 1] + " right into index " + (j + 1));
                    j--;
                }

                arr[j + 1] = key; // insert the key in the correct spot
                anim.markSortedThrough(i);
                anim.place(j + 1, "Inserted key " + key + " at index " + (j + 1));
            }
        }

        anim.finish("Sorted");
    }
}
