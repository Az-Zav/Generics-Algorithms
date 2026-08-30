import java.util.Arrays;

/**
 * 
 * MergeSort
 * mergeSort() recursively splits indices until each array has one element left
 * once one element left for each sub-array, call merge() to sort the two subarrays being compared and overwrite in original array
 */

public class MergeSort{

    public static <T extends Comparable<T>> void mergeSort(T[] arr, int left, int right, boolean ascending) {
        if (left >= right) return; // do nothing if contains one element

        int mid = (left + right)/2;
        mergeSort(arr, left, mid, ascending); //recurse left array from split
        mergeSort(arr, mid + 1, right, ascending); //recurse right array from split
        merge(arr, left, mid, right, ascending);
    }

    public static <T extends Comparable<T>> void merge(T[] arr, int left, int mid, int right, boolean ascending) {
        T[] leftArr = Arrays.copyOfRange(arr, left, mid + 1); //Third parameter is exclusive
        T[] rightArr = Arrays.copyOfRange(arr, mid + 1, right + 1); //Third parameter is exclusive

        int i = 0, j = 0, k = left; // k tracks the lowest element index of sub-array

        while (i < leftArr.length && j < rightArr.length) { //replace position of lowest element with lowest value while left and right arrays still have values to compare
            int value = leftArr[i].compareTo(rightArr[j]);
            boolean takeLeft = ascending ? (value<=0) : (value>=0); //determines if sorting order is ascending or descending
            
            if (takeLeft) {
                arr[k++] = leftArr[i++]; 
            } else {
                arr[k++] = rightArr[j++];
            }
        }

        //if one array's elements are all taken, add all remaining values of other array to remaining positions
        while (i < leftArr.length) arr[k++] = leftArr[i++]; 
        while (i < rightArr.length) arr[k++] = leftArr[j++];
    }
}