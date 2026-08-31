/**
 * 
 * InsertionSort
 * insertionSort() treats the first element as the sorted part, then takes each following element as the key
 * shifts every element of the sorted part that belongs after the key one position to the right, then inserts the key in the gap
 */

public class InsertionSort {

    public static <T extends Comparable<T>> void insertionSort(T[] arr, boolean ascending) {
        for (int i = 1; i < arr.length; i++) { // index 0 is already sorted, so start at the second element
            T key = arr[i]; // element being inserted into the sorted part
            int j = i - 1; // last index of the sorted part

            while (j >= 0) {
                int value = arr[j].compareTo(key);
                boolean shift = ascending ? (value > 0) : (value < 0); // determines if sorting order is ascending or descending

                if (!shift) break; // arr[j] belongs before the key, so the gap at j + 1 is the correct spot
                arr[j + 1] = arr[j]; // move the bigger element to the right to open the gap
                j--;
            }

            arr[j + 1] = key; // insert the key in the correct spot
        }
    }
}
