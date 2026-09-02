/**
 * QuickSort
 * quickSort() chooses a pivot, partitions the array around the pivot,
 * and recursively sorts the left and right partitions.
 */
public class QuickSort {

    public static <T extends Comparable<T>> void quickSort(T[] arr, boolean ascending, int delayMs) {
        boolean[] sortedMask = new boolean[arr.length];
        boolean[] gapMask = new boolean[arr.length];
        quickSort(arr, 0, arr.length - 1, ascending, delayMs, sortedMask, gapMask);
    }

    public static <T extends Comparable<T>> void quickSort(T[] arr, int low, int high, boolean ascending) {
        boolean[] sortedMask = new boolean[arr.length];
        boolean[] gapMask = new boolean[arr.length];
        quickSort(arr, low, high, ascending, 0, sortedMask, gapMask);
    }

    private static <T extends Comparable<T>> void quickSort(T[] arr, int low, int high, boolean ascending, int delayMs, boolean[] sortedMask, boolean[] gapMask) {
        if (low >= high) {
            if (low == high) {
                sortedMask[low] = true; // single-element partition is trivially sorted
                if (delayMs > 0) {
                    renderSingleElementSorted(arr, low, sortedMask, gapMask, delayMs);
                }
            }
            return;
        }

        renderPartitionStart(arr, low, high, sortedMask, gapMask, delayMs);
        int pivotIndex = partition(arr, low, high, ascending, delayMs, sortedMask, gapMask);
        sortedMask[pivotIndex] = true; // pivot is placed in its final sorted position
        renderPivotPlaced(arr, low, high, pivotIndex, sortedMask, gapMask, delayMs);

        // recurse on left and right partitions
        quickSort(arr, low, pivotIndex - 1, ascending, delayMs, sortedMask, gapMask);
        quickSort(arr, pivotIndex + 1, high, ascending, delayMs, sortedMask, gapMask);
    }

    private static <T extends Comparable<T>> int partition(T[] arr, int low, int high, boolean ascending, int delayMs, boolean[] sortedMask, boolean[] gapMask) {
        T pivot = arr[high]; // choose last element as pivot
        renderPivotChosen(arr, low, high, high, sortedMask, gapMask, delayMs);

        int i = low - 1; // tracks partition boundary of elements placed on the left

        for (int j = low; j < high; j++) {
            renderCompare(arr, low, high, j, high, sortedMask, gapMask, delayMs);

            int value = arr[j].compareTo(pivot);
            boolean belongsLeft = ascending ? (value <= 0) : (value >= 0);

            if (belongsLeft) {
                i++;
                if (i != j) {
                    swap(arr, i, j);
                    renderSwap(arr, low, high, i, j, high, sortedMask, gapMask, delayMs);
                }
            }
        }

        // Place pivot in its correct position
        int finalPivotPos = i + 1;
        if (finalPivotPos != high) {
            swap(arr, finalPivotPos, high);
            renderSwap(arr, low, high, finalPivotPos, high, finalPivotPos, sortedMask, gapMask, delayMs);
        }

        return finalPivotPos;
    }

    private static <T extends Comparable<T>> void swap(T[] arr, int a, int b) {
        T temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    // ---- render wrappers: build a BoxSpec[] for this moment, then hand it to the dumb box drawer ----

    /**
     * Base spec builder shared by all of QuickSort's render moments: marks everything
     * outside [left,right] as inactive, marks sorted positions green, and opens visual
     * gaps at the partition boundaries so the active range reads as its own separated block.
     */
    private static <T> Animator.BoxSpec[] baseSpecs(T[] arr, int left, int right, boolean[] sortedMask, boolean[] gapMask) {
        Animator.BoxSpec[] specs = Animator.freshSpecs(arr);
        for (int k = 0; k < arr.length; k++) {
            if (k < left || k > right) {
                specs[k].state = Animator.CellState.OUT_OF_RANGE;
            } else if (sortedMask[k]) {
                specs[k].state = Animator.CellState.SORTED;
            }
            boolean boundaryGap = (k == left - 1) || (k == right);
            specs[k].gapAfter = boundaryGap || gapMask[k];
        }
        return specs;
    }

    private static <T> void renderPartitionStart(T[] arr, int low, int high, boolean[] sortedMask, boolean[] gapMask, int delayMs) {
        if (delayMs <= 0) return;
        Animator.BoxSpec[] specs = baseSpecs(arr, low, high, sortedMask, gapMask);
        String message = "Partitioning range [" + low + ".." + high + "]";
        Animator.render(arr, specs, message, delayMs);
    }

    private static <T> void renderPivotChosen(T[] arr, int low, int high, int pivotIdx, boolean[] sortedMask, boolean[] gapMask, int delayMs) {
        if (delayMs <= 0) return;
        Animator.BoxSpec[] specs = baseSpecs(arr, low, high, sortedMask, gapMask);
        specs[pivotIdx].state = Animator.CellState.PIVOT;
        specs[pivotIdx].lift = true;
        String message = "Selected pivot: " + arr[pivotIdx] + " at index " + pivotIdx;
        Animator.render(arr, specs, message, delayMs);
    }

    private static <T> void renderCompare(T[] arr, int low, int high, int currentIdx, int pivotIdx, boolean[] sortedMask, boolean[] gapMask, int delayMs) {
        if (delayMs <= 0) return;
        Animator.BoxSpec[] specs = baseSpecs(arr, low, high, sortedMask, gapMask);
        specs[pivotIdx].state = Animator.CellState.PIVOT;
        specs[currentIdx].state = Animator.CellState.COMPARING;
        specs[currentIdx].lift = true;
        String message = "Comparing " + arr[currentIdx] + " (index " + currentIdx + ") with pivot " + arr[pivotIdx];
        Animator.render(arr, specs, message, delayMs);
    }

    private static <T> void renderSwap(T[] arr, int low, int high, int idx1, int idx2, int pivotIdx, boolean[] sortedMask, boolean[] gapMask, int delayMs) {
        if (delayMs <= 0) return;
        Animator.BoxSpec[] specs = baseSpecs(arr, low, high, sortedMask, gapMask);
        specs[idx1].state = Animator.CellState.SWAPPING;
        specs[idx1].lift = true;
        specs[idx2].state = Animator.CellState.SWAPPING;
        specs[idx2].lift = true;
        String message = "Swapped " + arr[idx1] + " (index " + idx1 + ") and " + arr[idx2] + " (index " + idx2 + ")";
        Animator.render(arr, specs, message, delayMs);
    }

    private static <T> void renderPivotPlaced(T[] arr, int low, int high, int pivotIdx, boolean[] sortedMask, boolean[] gapMask, int delayMs) {
        if (delayMs <= 0) return;
        Animator.BoxSpec[] specs = baseSpecs(arr, low, high, sortedMask, gapMask);
        specs[pivotIdx].state = Animator.CellState.SORTED;
        specs[pivotIdx].lift = true;
        String message = "Pivot " + arr[pivotIdx] + " locked in sorted position at index " + pivotIdx;
        Animator.render(arr, specs, message, delayMs);
    }

    private static <T> void renderSingleElementSorted(T[] arr, int index, boolean[] sortedMask, boolean[] gapMask, int delayMs) {
        if (delayMs <= 0) return;
        Animator.BoxSpec[] specs = baseSpecs(arr, index, index, sortedMask, gapMask);
        specs[index].state = Animator.CellState.SORTED;
        String message = "Single element [" + index + "] is sorted";
        Animator.render(arr, specs, message, delayMs);
    }
}
