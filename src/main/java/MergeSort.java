import java.util.Arrays;

/**
 * 
 * MergeSort
 * mergeSort() recursively splits indices until each array has one element left
 * once one element left for each sub-array, call merge() to sort the two subarrays being compared and overwrite in original array
 */

public class MergeSort{

    public static <T extends Comparable<T>> void mergeSort(T[] arr, boolean ascending, int delayMs) {
        boolean[] sortedMask = new boolean[arr.length];
        mergeSort(arr, 0, arr.length - 1, ascending, delayMs, sortedMask);
    }

    private static <T extends Comparable<T>> void mergeSort(T[] arr, int left, int right, boolean ascending, int delayMs, boolean[] sortedMask) {
        if (left >= right) {
            if (left == right) sortedMask[left] = true; // single-element base case is trivially sorted
            return;
        }

        int mid = (left + right)/2;
        mergeSort(arr, left, mid, ascending, delayMs, sortedMask); //recurse left array from split
        mergeSort(arr, mid + 1, right, ascending, delayMs, sortedMask); //recurse right array from split
        merge(arr, left, mid, right, ascending, delayMs, sortedMask);
    }

    private static <T extends Comparable<T>> void merge(T[] arr, int left, int mid, int right, boolean ascending, int delayMs, boolean[] sortedMask) {
        T[] leftArr = Arrays.copyOfRange(arr, left, mid + 1); //Third parameter is exclusive
        T[] rightArr = Arrays.copyOfRange(arr, mid + 1, right + 1); //Third parameter is exclusive

        int i = 0, j = 0, k = left; // k tracks the lowest element index of sub-array

        while (i < leftArr.length && j < rightArr.length) { //replace position of lowest element with lowest value while left and right arrays still have values to compare
            AnimationUtil.render(arr, left + i, mid + 1 + j, false, sortedMask, delayMs); // comparing the two candidates

            int value = leftArr[i].compareTo(rightArr[j]); //if smaller, returns negative value
            boolean takeLeft = ascending ? (value<=0) : (value>=0); //determines if sorting order is ascending or descending

            if (takeLeft) {
                arr[k] = leftArr[i++];
            } else {
                arr[k] = rightArr[j++];
            }
            sortedMask[k] = true; // placed into merged position
            AnimationUtil.render(arr, k, -1, true, sortedMask, delayMs); // show the overwrite
            k++;
        }

        //if one array's elements are all taken, add all remaining values of other array to remaining positions
        while (i < leftArr.length) {
            arr[k] = leftArr[i++];
            sortedMask[k] = true;
            AnimationUtil.render(arr, k, -1, true, sortedMask, delayMs);
            k++;
        }
        while (j < rightArr.length) {
            arr[k] = rightArr[j++];
            sortedMask[k] = true;
            AnimationUtil.render(arr, k, -1, true, sortedMask, delayMs);
            k++;
        }
    }
}