import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SortingTest {

    // ---------- SelectionSort ----------

    @Test
    void selectionSort_ascending_integers() {
        Integer[] arr = {5, 2, 8, 1, 9};
        SelectionSort.selectionSort(arr, true);
        assertArrayEquals(new Integer[]{1, 2, 5, 8, 9}, arr);
    }

    @Test
    void selectionSort_descending_integers() {
        Integer[] arr = {5, 2, 8, 1, 9};
        SelectionSort.selectionSort(arr, false);
        assertArrayEquals(new Integer[]{9, 8, 5, 2, 1}, arr);
    }

    @Test
    void selectionSort_strings() {
        String[] arr = {"banana", "apple", "cherry"};
        SelectionSort.selectionSort(arr, true);
        assertArrayEquals(new String[]{"apple", "banana", "cherry"}, arr);
    }

    // ---------- QuickSort ----------

    @Test
    void quickSort_ascending_integers() {
        Integer[] arr = {5, 2, 8, 1, 9};
        QuickSort.quickSort(arr, 0, arr.length - 1, true);
        assertArrayEquals(new Integer[]{1, 2, 5, 8, 9}, arr);
    }

    @Test
    void quickSort_descending_integers() {
        Integer[] arr = {5, 2, 8, 1, 9};
        QuickSort.quickSort(arr, 0, arr.length - 1, false);
        assertArrayEquals(new Integer[]{9, 8, 5, 2, 1}, arr);
    }

    // ---------- MergeSort ----------

    @Test
    void mergeSort_ascending_integers() {
        Integer[] arr = {5, 2, 8, 1, 9};
        MergeSort.mergeSort(arr, true, 0);
        assertArrayEquals(new Integer[]{1, 2, 5, 8, 9}, arr);
    }

    @Test
    void mergeSort_descending_integers() {
        Integer[] arr = {5, 2, 8, 1, 9};
        MergeSort.mergeSort(arr, false, 0);
        assertArrayEquals(new Integer[]{9, 8, 5, 2, 1}, arr);
    }

    // ---------- Edge cases ----------

    @Test
    void handles_single_element_array() {
        Integer[] arr = {42};
        SelectionSort.selectionSort(arr, true);
        assertArrayEquals(new Integer[]{42}, arr);
    }

    @Test
    void handles_already_sorted_array() {
        Integer[] arr = {1, 2, 3, 4, 5};
        QuickSort.quickSort(arr, 0, arr.length - 1, true);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void handles_duplicate_values() {
        Integer[] arr = {3, 1, 3, 2, 3};
        MergeSort.mergeSort(arr, true, 0);
        assertArrayEquals(new Integer[]{1, 2, 3, 3, 3}, arr);
    }
}